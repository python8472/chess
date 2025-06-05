package ui;

import facade.ServerFacade;
import request.*;
import result.*;
import model.*;
import java.util.List;
import java.util.Scanner;

public class PostLogin {
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade = new ServerFacade("http://localhost:8080");
    private final String username;
    private final String authToken;

    private List<GameData> cachedGames = List.of();  // updated on each list cal

    public PostLogin(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public void run() {
        System.out.println(EscapeSequences.ERASE_SCREEN +
                EscapeSequences.SET_TEXT_BOLD +
                "Welcome, " + username + "!" +
                EscapeSequences.RESET_TEXT_BOLD_FAINT);
        HelpHelper.printPostLoginHelp();


        while (true) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE +
                    "(" + username + ") > " +
                    EscapeSequences.RESET_TEXT_COLOR);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {continue;}

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toLowerCase();

            try {
                switch (command) {
                    case "help" -> HelpHelper.printPostLoginHelp();
                    case "logout" -> {
                        logout();
                        return;
                    }
                    case "create" -> handleCreate(tokens);
                    case "list" -> handleList();
                    case "join" -> handleJoin(tokens);
                    case "observe" -> handleObserve(tokens);
                    default -> System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                            "Unknown command: " + command +
                            EscapeSequences.RESET_TEXT_COLOR);
                }
            } catch (Exception e) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: " + e.getMessage() +
                        EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }

    private void handleList() throws Exception {
        ListGamesResult result = facade.listGames(authToken);
        if (result.getMessage() == null) {
            cachedGames = result.getGames();
            if (cachedGames.isEmpty()) {
                System.out.println("No games available.");
            } else {
                for (int i = 0; i < cachedGames.size(); i++) {
                    GameData game = cachedGames.get(i);
                    System.out.printf("Game %d | Name: %s | White: %s | Black: %s%n",
                            i + 1, game.getGameName(),
                            game.getWhiteUsername(), game.getBlackUsername());
                }
            }
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Failed to list games: " +
                    result.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    private void handleJoin(String[] tokens) throws Exception {
        if (tokens.length < 3) {
            System.out.println("Usage: join <game-number> <WHITE|BLACK>");
            return;
        }

        int index;
        try {
            index = Integer.parseInt(tokens[1]) - 1;
        } catch (NumberFormatException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: game number must be an integer. Use 'list' to see available games." +
                    EscapeSequences.RESET_TEXT_COLOR);
            return;
        }

        if (index < 0 || index >= cachedGames.size()) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: invalid game number. Use 'list' to see available games." +
                    EscapeSequences.RESET_TEXT_COLOR);
            return;
        }

        int gameID = cachedGames.get(index).getGameID();
        String playerColor = tokens[2].toUpperCase();
        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: color must be WHITE or BLACK." +
                    EscapeSequences.RESET_TEXT_COLOR);
            return;
        }

        JoinGameRequest request = new JoinGameRequest(playerColor, gameID);
        JoinGameResult result = facade.joinGame(request, authToken);
        if (result.getMessage() == null) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Joined game " +
                    (index + 1) + " as " + playerColor + EscapeSequences.RESET_TEXT_COLOR);
            new Gameplay(username, authToken, gameID, playerColor).run();
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Failed to join game: " +
                    result.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    private void handleObserve(String[] tokens) throws Exception {
        if (tokens.length < 2) {
            System.out.println("Usage: observe <game-number>");
            return;
        }

        int index;
        try {
            index = Integer.parseInt(tokens[1]) - 1;
        } catch (NumberFormatException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: game number must be an integer, use 'list' to see available games." +
                    EscapeSequences.RESET_TEXT_COLOR);
            return;
        }
        //catch when someone puts number that does not exist
        if (index < 0 || index >= cachedGames.size()) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: invalid game number. Use 'list' to see available games." +
                    EscapeSequences.RESET_TEXT_COLOR);
            return;
        }

        int gameID = cachedGames.get(index).getGameID();
        JoinGameRequest request = new JoinGameRequest("OBSERVER", gameID);
        JoinGameResult result = facade.joinGame(request, authToken);
        if (result.getMessage() == null) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "Observing game " + (index + 1) + EscapeSequences.RESET_TEXT_COLOR);
            new Gameplay(username, authToken, gameID, "OBSERVER").run();
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Failed to observe game: " +
                    result.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    private void handleCreate(String[] tokens) throws Exception {
        if (tokens.length < 2) {
            System.out.println("Usage: create <game-name>");
            return;
        }

        String name = tokens[1];
        CreateGameResult result = facade.createGame(new CreateGameRequest(name), authToken);
        if (result.getMessage() == null) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "Game created with ID " + result.getGameID() + EscapeSequences.RESET_TEXT_COLOR);
            handleList();
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Failed to create game: " +
                    result.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    private void logout() throws Exception {
        var result = facade.logout(new LogoutRequest(authToken), authToken);
        if (result.getMessage() == null) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "Logged out successfully." + EscapeSequences.RESET_TEXT_COLOR);
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Logout failed: " +
                    result.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
        }
    }
}

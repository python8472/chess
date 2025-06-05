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

    private void handleObserve(String[] tokens) throws Exception {
        if (tokens.length < 2) {
            System.out.println("Usage: observe <game-id>");
            return;
        }

        int gameID = Integer.parseInt(tokens[1]);
        int gameIndex;
        try {
            gameIndex = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            System.out.println("Error: game number must be a valid integer (e.g., 'join 1 WHITE').");
            return;
        }

        // Optional: validate that the game ID exists first
        ListGamesResult gamesResult = facade.listGames(authToken);
        boolean found = gamesResult.getGames().stream().anyMatch(g -> g.getGameID() == gameID);
        if (!found) {
            System.out.println("Error: Game ID not found.");
            return;
        }

        JoinGameRequest request = new JoinGameRequest("OBSERVER", gameID);
        JoinGameResult result = facade.joinGame(request, authToken);
        if (result.getMessage() == null) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "Observing game " +
                    gameID +
                    EscapeSequences.RESET_TEXT_COLOR);
            new Gameplay(username, authToken, gameID, "OBSERVER").run(); // internal use
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Failed to observe game: " +
                    result.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    private void logout() throws Exception {
        var result = facade.logout(new LogoutRequest(authToken), authToken);
        if (result.getMessage() == null) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "Logged out successfully." + EscapeSequences.RESET_TEXT_COLOR);
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Logout failed: "
                    + result.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
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
                    "Game created with ID " +
                    result.getGameID() + EscapeSequences.RESET_TEXT_COLOR);
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Failed to create game: " +
                    result.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    private void handleList() throws Exception {
        ListGamesResult result = facade.listGames(authToken);
        if (result.getMessage() == null) {
            List<GameData> games = result.getGames();
            if (games.isEmpty()) {
                System.out.println("No games available.");
            } else {
                for (GameData game : games) {
                    System.out.printf("ID: %d | Name: %s | White: %s | Black: %s%n",
                            game.getGameID(), game.getGameName(),
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
            System.out.println("Usage: join <game-id> <WHITE|BLACK|>");
            return;
        }

        int gameID = Integer.parseInt(tokens[1]);
        String playerColor = tokens[2].toUpperCase();
        String color = switch (playerColor) {
            case "WHITE" -> "WHITE";
            case "BLACK" -> "BLACK";
            default -> throw new IllegalArgumentException("Color must be WHITE or BLACK");
        };

        JoinGameRequest request = new JoinGameRequest(color, gameID);
        JoinGameResult result = facade.joinGame(request, authToken);
        if (result.getMessage() == null) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Joined game " +
                    gameID + " as " + (color) + EscapeSequences.RESET_TEXT_COLOR);
            new Gameplay(username, authToken, gameID, color).run(); // handoff
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Failed to join game: " +
                    result.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
        }
    }

}

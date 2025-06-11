package ui;

import chess.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;
import java.util.function.Consumer;

import java.util.Scanner;

public class Gameplay {
    private final Scanner scanner = new Scanner(System.in);
    private final String playerColor;
    private final String authToken;
    private final int gameID;
    private final GameSocketClient socketClient;
    private final ChessGame.TeamColor pov;
    private ChessGame currentGame;

    public Gameplay(String username, String authToken, int gameID, String color) {
        this.playerColor = color;
        this.authToken = authToken;
        this.gameID = gameID;
        this.pov = (color == null || color.equalsIgnoreCase("BLACK"))
                ? ChessGame.TeamColor.BLACK
                : ChessGame.TeamColor.WHITE;
        this.socketClient = new GameSocketClient((Consumer<ServerMessage>) this::handleServerMessage);
    }

    public void run() {
        try {
            socketClient.connect(authToken, gameID);
        } catch (Exception e) {
            System.out.println("WebSocket connection failed: " + e.getMessage());
            return;
        }

        System.out.println(EscapeSequences.ERASE_SCREEN + EscapeSequences.SET_TEXT_BOLD +
                "Now viewing game as " + (playerColor) +
                EscapeSequences.RESET_TEXT_BOLD_FAINT);

        while (true) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + "(gameplay) > " + EscapeSequences.RESET_TEXT_COLOR);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toLowerCase();

            try {
                switch (command) {
                    case "help" -> HelpHelper.printGameplayHelp();
                    case "redraw" -> drawBoard();
                    case "move" -> {
                        if (tokens.length != 3) {
                            System.out.println("Usage: move <from> <to>");
                            break;
                        }
                        ChessPosition from = parsePosition(tokens[1]);
                        ChessPosition to = parsePosition(tokens[2]);
                        if (from == null || to == null) {
                            System.out.println("Invalid move positions.");
                            break;
                        }
                        ChessMove move = new ChessMove(from, to, null); // add promotion later if needed
                        ChessGame.TeamColor color = playerColor.equalsIgnoreCase("BLACK") ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                        socketClient.sendMove(authToken, gameID, color, move);
                    }
                    case "highlight" -> {
                        if (tokens.length != 2) {
                            System.out.println("Usage: highlight <pos>");
                            break;
                        }
                        ChessPosition pos = parsePosition(tokens[1]);
                        if (pos == null) {
                            System.out.println("Invalid position.");
                            break;
                        }
                        socketClient.sendHighlight(authToken, gameID, pos);
                    }
                    case "resign" -> socketClient.sendResign(authToken, gameID);
                    case "leave" -> {
                        socketClient.sendLeave(authToken, gameID);
                        return;
                    }
                    default -> System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                            "Unknown command: " + command + EscapeSequences.RESET_TEXT_COLOR);
                }
            } catch (Exception e) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Command failed: " + e.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }

    private void drawBoard() {
        if (currentGame == null) {
            System.out.println("Game not loaded yet.");
            return;
        }
        BoardDisplay.displayBoard(currentGame.getBoard(), pov);
    }

    private ChessPosition parsePosition(String str) {
        if (str.length() != 2) return null;
        char file = str.charAt(0);
        char rank = str.charAt(1);
        int col = file - 'a' + 1;
        int row = rank - '1' + 1;
        if (col < 1 || col > 8 || row < 1 || row > 8) return null;
        return new ChessPosition(row, col);
    }

    private void handleServerMessage(ServerMessage msg) {
        switch (msg.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage load = (LoadGameMessage) msg;
                this.currentGame = load.getGame();
                drawBoard();
            }
            case NOTIFICATION -> {
                NotificationMessage note = (NotificationMessage) msg;
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "[NOTIFY] " + note.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
            }
            case ERROR -> {
                ErrorMessage err = (ErrorMessage) msg;
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "[ERROR] " + err.getErrorMessage() + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }
}

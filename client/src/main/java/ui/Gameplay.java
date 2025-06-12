package ui;

import chess.*;
import websocket.messages.*;
import java.util.Scanner;

public class Gameplay {
    private final Scanner scanner = new Scanner(System.in);
    private final String authToken;
    private final int gameID;
    private ChessBoard currentBoard = new ChessBoard();
    private final ChessGame.TeamColor pov;
    private final GameSocketClient socketClient;

    public Gameplay(String username, String authToken, int gameID, String color) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.pov = (color == null || color.equalsIgnoreCase("BLACK")) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        this.socketClient = new GameSocketClient("ws://localhost:8080/ws", this::handleServerMessage);
    }

    public void run() {
        try {
            socketClient.connect(authToken, gameID);
        } catch (Exception e) {
            System.out.println("Error: connection failed " + e.getMessage());
            return;
        }

        while (true) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + "(gameplay) > " + EscapeSequences.RESET_TEXT_COLOR);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toLowerCase();

            try {
                handleCommand(command, tokens);
            } catch (Exception e) {
                System.out.println("Command failed: " + e.getMessage());
            }
        }
    }
    private void handleCommand(String command, String[] tokens) {
        switch (command) {
            case "help" -> HelpHelper.printGameplayHelp();
            case "redraw" -> drawInitialBoard();
            case "highlight" -> handleHighlight(tokens);
            case "move" -> handleMove(tokens);
            case "resign" -> socketClient.sendResign(authToken, gameID);
            case "leave" -> {
                socketClient.sendLeave(authToken, gameID);
                System.out.println("Leaving game...");
                System.exit(0);
            }
            default -> System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                    "Unknown command: " + command + EscapeSequences.RESET_TEXT_COLOR);
        }
    }
    private void handleHighlight(String[] tokens) {
        if (tokens.length != 2) {
            System.out.println("Usage: highlight <square>");
            return;
        }
        ChessPosition pos = ChessPosition.fromAlgebraic(tokens[1]);
        socketClient.sendHighlight(authToken, gameID, pos);
    }
    private void handleMove(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("Usage: move <start> <end> [promo]");
            return;
        }

        ChessPosition start = ChessPosition.fromAlgebraic(tokens[1]);
        ChessPosition end = ChessPosition.fromAlgebraic(tokens[2]);
        ChessPiece.PieceType promo = null;
        if (tokens.length == 4) {
            promo = ChessPiece.PieceType.valueOf(tokens[3].toUpperCase());
        }

        ChessMove move = new ChessMove(start, end, promo);
        socketClient.sendMove(authToken, gameID, pov, move);
    }
    private void drawInitialBoard() {
        BoardDisplay.displayBoard(currentBoard, pov, null);
    }
    private void handleServerMessage(ServerMessage msg) {
        switch (msg.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage load = (LoadGameMessage) msg;
                if (load.getGame() == null) {
                    System.out.println("[ERROR] Received null game from server.");
                } else {
                    currentBoard = load.getGame().getBoard();

                    drawInitialBoard();
                }
            }

            case HIGHLIGHT -> {
                HighlightMessage highlightMsg = (HighlightMessage) msg;
                BoardDisplay.displayBoard(currentBoard, pov, highlightMsg.getHighlights());
            }


            case NOTIFICATION -> {
                NotificationMessage note = (NotificationMessage) msg;
                System.out.println("[Notice] " + note.getMessage());
            }
            case ERROR -> {
                ErrorMessage err = (ErrorMessage) msg;
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + err.getErrorMessage() + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }

}
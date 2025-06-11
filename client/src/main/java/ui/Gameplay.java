package ui;

import chess.*;
import com.google.gson.Gson;
import websocket.commands.*;
import websocket.messages.*;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;
import java.util.function.Consumer;

@ClientEndpoint
public class Gameplay {
    private final Scanner scanner = new Scanner(System.in);
    private final String playerColor;
    private final String authToken;
    private final int gameID;
    private ChessBoard currentBoard = new ChessBoard();
    private ChessGame.TeamColor pov;
    private GameSocketClient socketClient;

    public Gameplay(String username, String authToken, int gameID, String color) {
        this.playerColor = color;
        this.authToken = authToken;
        this.gameID = gameID;
        this.pov = (color == null || color.equals("BLACK")) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        this.socketClient = new GameSocketClient("ws://localhost:8080/ws", this::handleServerMessage);
    }

    public void run() {
        System.out.println(EscapeSequences.ERASE_SCREEN + EscapeSequences.SET_TEXT_BOLD +
                "Now viewing game as " + playerColor + EscapeSequences.RESET_TEXT_BOLD_FAINT);

        drawInitialBoard();

        try {
            socketClient.connect(authToken, gameID);
        } catch (Exception e) {
            System.out.println("WebSocket connection failed: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + "(gameplay) > " + EscapeSequences.RESET_TEXT_COLOR);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) { continue; }

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toLowerCase();

            try {
                switch (command) {
                    case "help" -> HelpHelper.printGameplayHelp();
                    case "redraw" -> drawInitialBoard();
                    case "highlight" -> {
                        if (tokens.length != 2) {
                            System.out.println("Usage: highlight <pos>");
                            break;
                        }
                        ChessPosition pos = ChessPosition.fromAlgebraic(tokens[1]);
                        socketClient.sendHighlight(authToken, gameID, pos);
                    }
                    case "move" -> {
                        if (tokens.length != 3) {
                            System.out.println("Usage: move <start> <end>");
                            break;
                        }
                        ChessPosition start = ChessPosition.fromAlgebraic(tokens[1]);
                        ChessPosition end = ChessPosition.fromAlgebraic(tokens[2]);
                        ChessMove move = new ChessMove(start, end, null);
                        socketClient.sendMove(authToken, gameID, pov, move);
                    }
                    case "leave" -> {
                        socketClient.sendLeave(authToken, gameID);
                        System.out.println("Leaving game...");
                        return;
                    }
                    case "resign" -> socketClient.sendResign(authToken, gameID);
                    default -> System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                            "Unknown command: " + command + EscapeSequences.RESET_TEXT_COLOR);
                }
            } catch (Exception e) {
                System.out.println("Command failed: " + e.getMessage());
            }
        }
    }

    private void drawInitialBoard() {
        BoardDisplay.displayBoard(currentBoard, pov);
    }

    private void handleServerMessage(ServerMessage msg) {
        switch (msg.getServerMessageType()) {
            case LOAD_GAME -> {
                ChessGame game = ((LoadGameMessage) msg).getGame();
                currentBoard = game.getBoard();
                drawInitialBoard();
            }
            case NOTIFICATION -> {
                String note = ((NotificationMessage) msg).getMessage();
                System.out.println("[Notice] " + note);
            }
            case ERROR -> {
                String error = ((ErrorMessage) msg).getErrorMessage();
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + error + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }
}

package ui;

import java.net.HttpURLConnection;
import java.util.Scanner;
import chess.*;
import request.LeaveRequest;
import result.LeaveResult;

public class Gameplay {
    private final Scanner scanner = new Scanner(System.in);
    private final String playerColor;
    private final String authToken;
    private final int gameID;


    public Gameplay(String username, String authToken, int gameID, String color) {
        this.playerColor = color;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public void run() {
        System.out.println(EscapeSequences.ERASE_SCREEN + EscapeSequences.SET_TEXT_BOLD +
                "Now viewing game as " + (playerColor) +
                EscapeSequences.RESET_TEXT_BOLD_FAINT);

        drawInitialBoard();

        while (true) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + "(gameplay) > " + EscapeSequences.RESET_TEXT_COLOR);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {continue;}

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toLowerCase();

            switch (command) {
                case "help" -> HelpHelper.printGameplayHelp();
                case "redraw" -> drawInitialBoard();
                case "leave" -> {
                    System.out.println("Leaving game...");

                    try {
                        LeaveRequest leaveReq = new LeaveRequest(authToken, gameID);
                        HttpURLConnection conn = ServerUtils.makeRequest("POST", "/game/leave", authToken);
                        ServerUtils.sendRequestBody(conn, leaveReq);

                        LeaveResult result = ServerUtils.readResponse(conn, LeaveResult.class);
                        if (result.getMessage() != null) {
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Leave failed: " + result.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
                        } else {
                            System.out.println("Successfully left the game.");
                        }
                    } catch (Exception e) {
                        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Leave failed: " + e.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
                    }

                    return;
                }

                default -> System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Unknown command: " + command + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }

    private void drawInitialBoard() {
        ChessGame.TeamColor pov = (playerColor == null || playerColor.equals("BLACK"))
                ? ChessGame.TeamColor.BLACK
                : ChessGame.TeamColor.WHITE;

        ChessBoard board = new ChessBoard();
        board.resetBoard(); // set up
        BoardDisplay.displayBoard(board, pov);
    }
}

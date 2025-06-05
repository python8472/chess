package ui;

import java.util.Scanner;
import chess.*;

public class Gameplay {
    private final Scanner scanner = new Scanner(System.in);
    private final String playerColor;

    public Gameplay(String username, String authToken, int gameID, String color) {
        this.playerColor = color;
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
                    System.out.println("Dipping from game...");
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

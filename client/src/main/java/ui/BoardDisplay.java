package ui;

import chess.*;

import java.util.Collection;

public class BoardDisplay {

    public static void displayBoard(ChessBoard board, ChessGame.TeamColor pov) {
        displayBoard(board, pov, null); // no highlights
    }

    public static void displayBoard(ChessBoard board, ChessGame.TeamColor pov, Collection<ChessPosition> highlights) {
        debugBoardState(board);  // ← added debug

        int[] rows = (pov == ChessGame.TeamColor.WHITE)
                ? new int[]{8, 7, 6, 5, 4, 3, 2, 1}
                : new int[]{1, 2, 3, 4, 5, 6, 7, 8};

        char[] cols = (pov == ChessGame.TeamColor.WHITE)
                ? new char[]{'a','b','c','d','e','f','g','h'}
                : new char[]{'h','g','f','e','d','c','b','a'};

        System.out.println(EscapeSequences.SET_TEXT_BOLD + "\n    " + getColumnHeader(cols) + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        for (int row : rows) {
            System.out.print(EscapeSequences.SET_TEXT_BOLD + " " + row + " " + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            for (int colIndex = 0; colIndex < cols.length; colIndex++) {
                char colChar = cols[colIndex];
                ChessPosition pos = new ChessPosition(row, colChar - 'a' + 1);
                ChessPiece piece = board.getPiece(pos);

                boolean isHighlighted = highlights != null && highlights.contains(pos);
                boolean isLight = (pov == ChessGame.TeamColor.WHITE)
                        ? ((row + colIndex) % 2 == 0)
                        : ((row + (7 - colIndex)) % 2 == 0);

                System.out.print(getSymbolWithBackground(piece, isLight, isHighlighted));
            }
            System.out.println(EscapeSequences.SET_TEXT_BOLD + " " + row + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        }
        System.out.println("    " + EscapeSequences.SET_TEXT_BOLD + getColumnHeader(cols) + EscapeSequences.RESET_TEXT_BOLD_FAINT + "\n");
    }

    private static String getColumnHeader(char[] cols) {
        StringBuilder sb = new StringBuilder();
        for (char col : cols) {
            sb.append(" ").append(" ").append(col).append(" ");
        }
        return sb.toString();
    }

    private static String getSymbolWithBackground(ChessPiece piece, boolean isLightSquare, boolean isHighlighted) {
        String bgColor;
        if (isHighlighted) {
            bgColor = EscapeSequences.SET_BG_COLOR_YELLOW;
        } else {
            bgColor = isLightSquare
                    ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    : EscapeSequences.SET_BG_COLOR_DARK_GREY;
        }

        String symbol;
        if (piece == null) {
            symbol = "   "; // 3-char empty block
        } else {
            String pieceSymbol = switch (piece.getTeamColor()) {
                case WHITE -> switch (piece.getPieceType()) {
                    case KING -> EscapeSequences.WHITE_KING;
                    case QUEEN -> EscapeSequences.WHITE_QUEEN;
                    case BISHOP -> EscapeSequences.WHITE_BISHOP;
                    case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                    case ROOK -> EscapeSequences.WHITE_ROOK;
                    case PAWN -> EscapeSequences.WHITE_PAWN;
                };
                case BLACK -> switch (piece.getPieceType()) {
                    case KING -> EscapeSequences.BLACK_KING;
                    case QUEEN -> EscapeSequences.BLACK_QUEEN;
                    case BISHOP -> EscapeSequences.BLACK_BISHOP;
                    case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                    case ROOK -> EscapeSequences.BLACK_ROOK;
                    case PAWN -> EscapeSequences.BLACK_PAWN;
                };
            };
            symbol = " " + pieceSymbol + " ";
        }

        return bgColor + symbol + EscapeSequences.RESET_BG_COLOR;
    }

    // 🧠 DEBUG method to show piece contents directly
    private static void debugBoardState(ChessBoard board) {
        System.out.println("[DEBUG] Raw piece matrix:");
        for (int row = 8; row >= 1; row--) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece == null) {
                    System.out.print("____ ");
                } else {
                    String abbrev = piece.getTeamColor().toString().charAt(0) + piece.getPieceType().toString().substring(0, 1);
                    System.out.print(abbrev + "  ");
                }
            }
            System.out.println();
        }
    }
}
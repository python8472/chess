package ui;

import chess.*;

public class BoardDisplay {
    public static void displayBoard(ChessBoard board, ChessGame.TeamColor pov) {
        int[] rows = (pov == ChessGame.TeamColor.WHITE)
                ? new int[]{8, 7, 6, 5, 4, 3, 2, 1}
                : new int[]{1, 2, 3, 4, 5, 6, 7, 8};

        char[] cols = (pov == ChessGame.TeamColor.WHITE)
                ? new char[]{'a','b','c','d','e','f','g','h'}
                : new char[]{'h','g','f','e','d','c','b','a'};

        System.out.println(EscapeSequences.SET_TEXT_BOLD + "\n   " + getColumnHeader(cols) + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        for (int row : rows) {
            System.out.print(EscapeSequences.SET_TEXT_BOLD + row + " " + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            for (int colIndex = 0; colIndex < cols.length; colIndex++) {
                char colChar = cols[colIndex];
                ChessPosition pos = new ChessPosition(row, colChar - 'a' + 1);
                ChessPiece piece = board.getPiece(pos);

                // Calculate correct light/dark pattern
                boolean isLight = (pov == ChessGame.TeamColor.WHITE)
                        ? ((row + colIndex) % 2 == 0)
                        : ((row + (7 - colIndex)) % 2 == 0);

                System.out.print(getSymbolWithBackground(piece, isLight));
            }
            System.out.println(" " + EscapeSequences.SET_TEXT_BOLD + row + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        }
        System.out.println("   " + EscapeSequences.SET_TEXT_BOLD + getColumnHeader(cols) + EscapeSequences.RESET_TEXT_BOLD_FAINT + "\n");
    }

    private static String getColumnHeader(char[] cols) {
        StringBuilder sb = new StringBuilder();
        for (char c : cols) {
            sb.append(" ").append(c).append(" ");
        }
        return sb.toString();
    }

    private static String getSymbolWithBackground(ChessPiece piece, boolean isLightSquare) {
        String bgColor = isLightSquare
                ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                : EscapeSequences.SET_BG_COLOR_DARK_GREY;

        String symbol;
        if (piece == null) {
            symbol = EscapeSequences.EMPTY;
        } else {
            symbol = switch (piece.getTeamColor()) {
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
        }

        return bgColor + symbol + EscapeSequences.RESET_BG_COLOR;
    }
}

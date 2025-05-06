package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class pawn {

    public static Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {
        Collection<ChessMove> moves = new ArrayList<>();

        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1; //get direction
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7; //get start row for 1 or 2 movement

        int row = start.getRow();
        int col = start.getColumn();

        // One step forward based on startrow
        ChessPosition oneForward = new ChessPosition(row + direction, col);
        if (inBounds(oneForward) && board.getPiece(oneForward) == null) {
            addMoveWithOptionalPromotion(moves, start, oneForward, color);

            // Two steps forward based on startrow
            ChessPosition twoForward = new ChessPosition(row + 2 * direction, col);
            if (row == startRow && inBounds(twoForward) && board.getPiece(twoForward) == null) {
                moves.add(new ChessMove(start, twoForward, null));
            }
        }

        // Diagonal captures by checking other colors
        for (int dCol : new int[]{-1, 1}) {
            int newCol = col + dCol;
            int newRow = row + direction;
            ChessPosition diag = new ChessPosition(newRow, newCol);
            if (inBounds(diag)) {
                ChessPiece target = board.getPiece(diag);
                if (target != null && target.getTeamColor() != color) {
                    addMoveWithOptionalPromotion(moves, start, diag, color);
                }
            }
        }

        return moves;
    }

    private static boolean inBounds(ChessPosition pos) {
        return pos.getRow() >= 1 && pos.getRow() <= 8 && pos.getColumn() >= 1 && pos.getColumn() <= 8;
    }

    private static void addMoveWithOptionalPromotion(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, ChessGame.TeamColor color) {
        int promotionRow = (color == ChessGame.TeamColor.WHITE) ? 8 : 1;
        if (end.getRow() == promotionRow) {
            for (ChessPiece.PieceType promo : new ChessPiece.PieceType[]{
                    ChessPiece.PieceType.QUEEN,
                    ChessPiece.PieceType.ROOK,
                    ChessPiece.PieceType.BISHOP,
                    ChessPiece.PieceType.KNIGHT
            }) {
                moves.add(new ChessMove(start, end, promo));
            }
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }
}

package chess.pieces;
import chess.*;
import java.util.ArrayList;
import java.util.Collection;

public class Kngt {
    public static Collection<ChessMove> getKngtMoves(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] jumps = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2}, {1, 2},
                {2, -1}, {2, 1}
        };

        for (int[] jump : jumps) {
            int newRow = start.getRow() + jump[0];
            int newCol = start.getColumn() + jump[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece occupying = board.getPiece(newPos);

                if (occupying == null || occupying.getTeamColor() != color) {
                    moves.add(new ChessMove(start, newPos, null));
                }
            }
        }

        return moves;
    }
}

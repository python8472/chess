package chess.pieces;
import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class king {

    public static Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                { 0, -1},          { 0, 1},
                { 1, -1}, { 1, 0}, { 1, 1}
        }; //king can move all directions 1 square

        for (int[] dir : directions) {
            int newRow = start.getRow() + dir[0];
            int newCol = start.getColumn() + dir[1];

            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) continue;

            ChessPosition newPos = new ChessPosition(newRow, newCol);
            ChessPiece occupyingPiece = board.getPiece(newPos);

            if (occupyingPiece == null || occupyingPiece.getTeamColor() != color) {
                moves.add(new ChessMove(start, newPos, null));
            }
        }

        return moves;
    }
}


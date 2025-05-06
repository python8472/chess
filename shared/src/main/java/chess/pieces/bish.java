package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class bish {

    public static Collection<ChessMove> getBishMoves(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Bishop moves diagonally: NE, NW, SE, SW
        int[][] directions = {
                {-1, 1},  // NE
                {-1, -1}, // NW
                {1, 1},   // SE
                {1, -1}   // SW
        };

        for (int[] dir : directions) {
            int row = start.getRow();
            int col = start.getColumn();

            while (true) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                    break;
                }

                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece occupyingPiece = board.getPiece(newPos);

                if (occupyingPiece == null) {
                    moves.add(new ChessMove(start, newPos, null));
                } else {
                    if (occupyingPiece.getTeamColor() != color) {
                        moves.add(new ChessMove(start, newPos, null));
                    }
                    break;
                }

                row = newRow;
                col = newCol;
            }
        }

        return moves;
    }
}

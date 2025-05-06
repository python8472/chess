package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class bish {

    public static Collection<ChessMove> getBishMoves(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] dir : directions) {
            int row = start.getRow();
            int col = start.getColumn();

            while (true) {
                row += dir[0];
                col += dir[1];

                if (row < 1 || row > 8 || col < 1 || col > 8) break;

                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece dest = board.getPiece(newPos);

                if (dest == null) {
                    moves.add(new ChessMove(start, newPos, null));
                } else {
                    if (dest.getTeamColor() != color) {
                        moves.add(new ChessMove(start, newPos, null));
                    }
                    break;
                }
            }
        }

        return moves;
    }
}

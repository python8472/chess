package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class queen {

    public static Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {
        Collection<ChessMove> moves = new ArrayList<>();

        // 8 directions: N, S, E, W, NE, NW, SE, SW
        int[][] directions = {
                {-1, 0}, {1, 0},  // N, S
                {0, -1}, {0, 1},  // W, E
                {-1, -1}, {-1, 1}, // NW, NE
                {1, -1}, {1, 1}    // SW, SE
        };

        for (int[] dir : directions) {
            int row = start.getRow();
            int col = start.getColumn();

            while (true) {
                row += dir[0];
                col += dir[1];

                if (row < 1 || row > 8 || col < 1 || col > 8) break;

                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPos);

                if (target == null) {
                    moves.add(new ChessMove(start, newPos, null));
                } else {
                    if (target.getTeamColor() != color) {
                        moves.add(new ChessMove(start, newPos, null));
                    }
                    break; // can't go past any piece
                }
            }
        }

        return moves;
    }
}

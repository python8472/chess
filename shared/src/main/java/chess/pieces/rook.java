package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class rook {

    public static Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {
        Collection<ChessMove> moves = new ArrayList<>();

        // 4 directions: N, S, E, W
        int[][] directions = {
                {-1, 0}, {1, 0},  // N, S
                {0, -1}, {0, 1}   // W, E
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
                    break; // can't go past a piece
                }
            }
        }

        return moves;
    }
}

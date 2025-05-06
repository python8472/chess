package chess.pieces;

import chess.*;
import java.util.Collection;
import static chess.pieces.MoveHelper.getMoves;

public class rook {

    public static Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {
        // 4 directions: N, S, E, W
        int[][] directions = {
                {-1, 0}, {1, 0},  // N, S
                {0, -1}, {0, 1}   // W, E
        };

        return getMoves(board, start, color, directions, true);

    }
}

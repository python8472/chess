package chess.pieces;

import chess.*;
import java.util.Collection;
import static chess.pieces.MoveHelper.getMoves;

public class Queen {

    public static Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {
        // 8 directions: N, S, E, W, NE, NW, SE, SW
        int[][] directions = {
                {-1, 0}, {1, 0},  // N, S
                {0, -1}, {0, 1},  // W, E
                {-1, -1}, {-1, 1}, // NW, NE
                {1, -1}, {1, 1}    // SW, SE
        };
        return getMoves(board, start, color, directions, true);
    }
}

package chess.pieces;

import chess.*;
import java.util.Collection;
import static chess.pieces.MoveHelper.getMoves;

public class Bish {

    public static Collection<ChessMove> getBishMoves(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {

        // Bishop moves diagonally: NE, NW, SE, SW
        int[][] directions = {
                {-1, 1},  // NE
                {-1, -1}, // NW
                {1, 1},   // SE
                {1, -1}   // SW
        };

        return getMoves(board, start, color, directions, true);
    }
}

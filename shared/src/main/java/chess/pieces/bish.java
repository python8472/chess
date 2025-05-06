package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static chess.pieces.MoveHelper.getMoves;

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

        return getMoves(board, start, color, directions, true);
    }
}

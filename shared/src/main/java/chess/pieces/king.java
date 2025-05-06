package chess.pieces;
import chess.*;
import java.util.Collection;
import static chess.pieces.MoveHelper.getMoves;

public class king {

    public static Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {

        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                { 0, -1},          { 0, 1},
                { 1, -1}, { 1, 0}, { 1, 1}
        }; //king can move all directions 1 square
        return getMoves(board, start, color, directions, false);
    }
}


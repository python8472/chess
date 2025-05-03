package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class bish extends ChessPiece {

    public bish (ChessGame.TeamColor color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] dir : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += dir[0];
                col += dir[1];
                if (row < 1 || row > 8 || col < 1 || col > 8) break;

                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece destPiece = board.getPiece(newPos);

                if (destPiece == null) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                } else {
                    if (destPiece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
            }
        }

        return moves;
    }
}

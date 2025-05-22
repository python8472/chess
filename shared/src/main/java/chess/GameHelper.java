package chess;
public class GameHelper {

    public static boolean hasNoLegalMoves(ChessBoard board, ChessGame.TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    for (ChessMove move : piece.pieceMoves(board, pos)) {
                        ChessBoard testBoard = cloneBoard(board);
                        testBoard.addPiece(move.getEndPosition(),
                                new ChessPiece(piece.getTeamColor(),
                                        move.getPromotionPiece() != null ? move.getPromotionPiece() : piece.getPieceType()));
                        testBoard.addPiece(pos, null);

                        ChessGame testGame = new ChessGame();
                        testGame.setBoard(testBoard);
                        if (!testGame.isInCheck(teamColor)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static ChessBoard cloneBoard(ChessBoard board) {
        ChessBoard newBoard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null) {
                    newBoard.addPiece(pos, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
        return newBoard;
    }
}
package chess;
public class GameHelper {

    public static boolean hasNoLegalMoves(ChessBoard board, ChessGame.TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (!isEligiblePiece(piece, teamColor)) {continue;}

                if (canPieceEscapeCheck(piece, pos, board, teamColor)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isEligiblePiece(ChessPiece piece, ChessGame.TeamColor teamColor) {
        return piece != null && piece.getTeamColor() == teamColor;
    }

    private static boolean canPieceEscapeCheck(ChessPiece piece, ChessPosition pos, ChessBoard board, ChessGame.TeamColor teamColor) {
        for (ChessMove move : piece.pieceMoves(board, pos)) {
            if (isLegalMove(move, piece, pos, board, teamColor)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLegalMove(ChessMove move, ChessPiece piece, ChessPosition from, ChessBoard board, ChessGame.TeamColor teamColor) {
        ChessBoard testBoard = cloneBoard(board);
        ChessPiece newPiece = new ChessPiece(piece.getTeamColor(),
                move.getPromotionPiece() != null ? move.getPromotionPiece() : piece.getPieceType());
        testBoard.addPiece(move.getEndPosition(), newPiece);
        testBoard.addPiece(from, null);

        ChessGame testGame = new ChessGame();
        testGame.setBoard(testBoard);
        return !testGame.isInCheck(teamColor);
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
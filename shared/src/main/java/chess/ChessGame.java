package chess;

import java.util.*;

public class ChessGame {

    private TeamColor whoseTurn = TeamColor.WHITE;
    private ChessBoard cBoard;
    private boolean gameOver = false;

    public ChessGame() {
        this.cBoard = new ChessBoard();
        this.cBoard.resetBoard();
    }

    public TeamColor getTeamTurn() {
        return whoseTurn;
    }

    public void setTeamTurn(TeamColor team) {
        this.whoseTurn = team;
    }

    public boolean getGameOver() {
        return gameOver || isInCheckmate(TeamColor.WHITE) || isInCheckmate(TeamColor.BLACK)
                || isInStalemate(TeamColor.WHITE) || isInStalemate(TeamColor.BLACK);
    }

    public void setGameOver(boolean b) {
        this.gameOver = b;
    }

    public enum TeamColor {
        WHITE, BLACK
    }

    private List<ChessMove> allTeamMoves(TeamColor teamColor) {
        List<ChessMove> moves = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = getBoard().getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    moves.addAll(piece.pieceMoves(getBoard(), pos));
                }
            }
        }
        return moves;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = getBoard().getPiece(pos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return pos;
                }
            }
        }
        return null;
    }

    private TeamColor oppositeTeam(TeamColor team) {
        return team == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = getBoard().getPiece(startPosition);

        List<ChessMove> legalMoves = new ArrayList<>();
        for (ChessMove move : piece.pieceMoves(getBoard(), startPosition)) {
            ChessBoard simulatedBoard = cloneBoard();
            ChessPiece.PieceType finalType = move.getPromotionPiece() != null ? move.getPromotionPiece() : piece.getPieceType();
            simulatedBoard.addPiece(startPosition, null);
            simulatedBoard.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), finalType));

            ChessGame testGame = new ChessGame();
            testGame.setBoard(simulatedBoard);
            if (!testGame.isInCheck(piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = getBoard().getPiece(move.getStartPosition());

        if (piece == null || piece.getTeamColor() != whoseTurn) {
            throw new InvalidMoveException("Invalid turn " + move.getStartPosition());
        }

        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if (valid == null || !valid.contains(move)) {
            throw new InvalidMoveException("Illegal movement " + move);
        }

        ChessPiece.PieceType finalType = move.getPromotionPiece() != null ? move.getPromotionPiece() : piece.getPieceType();
        getBoard().addPiece(move.getStartPosition(), null);
        getBoard().addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), finalType));
        whoseTurn = oppositeTeam(whoseTurn);
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        for (ChessMove move : allTeamMoves(oppositeTeam(teamColor))) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
            }
        }
        return false;
    }

    private ChessBoard cloneBoard() {
        ChessBoard newBoard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = getBoard().getPiece(pos);
                if (piece != null) {
                    newBoard.addPiece(pos, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
        return newBoard;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && GameHelper.hasNoLegalMoves(getBoard(), teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && GameHelper.hasNoLegalMoves(getBoard(), teamColor);
    }

    public void setBoard(ChessBoard board) {
        this.cBoard = board;
    }

    public ChessBoard getBoard() {
        if (cBoard == null) {
            cBoard = new ChessBoard();
            cBoard.resetBoard();
        }
        return cBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame that = (ChessGame) o;
        return whoseTurn == that.whoseTurn && Objects.equals(cBoard, that.cBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(whoseTurn, cBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" + "whoseTurn=" + whoseTurn + ", board=" + cBoard + '}';
    }
}

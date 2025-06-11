package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor whoseTurn = TeamColor.WHITE; //to track turns
    private ChessBoard cBoard;
    private boolean gameOver = false;


    public ChessGame() {
        cBoard = new ChessBoard();
        cBoard.resetBoard();  // gotta get default
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        // return stored team
        return whoseTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
//        set whoseTurn based on arg team
        whoseTurn = team;
    }

    public boolean getGameOver() {
        return gameOver ||
                isInCheckmate(TeamColor.WHITE) ||
                isInCheckmate(TeamColor.BLACK) ||
                isInStalemate(TeamColor.WHITE) ||
                isInStalemate(TeamColor.BLACK);
    }

    public void setGameOver(boolean b) {
        this.gameOver = b;
    }
    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Finds all possible pseudo-legal moves from all pieces of the given team.
     * @param teamColor the team to scan for
     * @return a list of all moves their pieces can make
     */
    private List<ChessMove> allTeamMoves(TeamColor teamColor) {
        List<ChessMove> moves = new ArrayList<>();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = cBoard.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    moves.addAll(piece.pieceMoves(cBoard, pos));
                }
            }
        }

        return moves;
    }

    /**
     * Helper function
     */

    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = cBoard.getPiece(pos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return pos;
                }
            }
        }
        return null; // shouldn't happen if the board is valid
    }

    private TeamColor oppositeTeam(TeamColor team) {
        return team == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = cBoard.getPiece(startPosition);
        if (piece == null) {return null;}

        List<ChessMove> legalMoves = new ArrayList<>();

        for (ChessMove move : piece.pieceMoves(cBoard, startPosition)) {
            ChessBoard simulatedBoard = cloneBoard();
            ChessPiece.PieceType finalType = move.getPromotionPiece() != null ? move.getPromotionPiece() : piece.getPieceType();



            // Standard move simulation
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

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = cBoard.getPiece(move.getStartPosition());

        if (piece == null || piece.getTeamColor() != whoseTurn) {
            throw new InvalidMoveException("Invalid turn " + move.getStartPosition());
        }

        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if (valid == null || !valid.contains(move)) {
            throw new InvalidMoveException("Illegal movement " + move);
        }

        ChessPiece.PieceType finalType = move.getPromotionPiece() != null ? move.getPromotionPiece() : piece.getPieceType();


        // Perform the move
        cBoard.addPiece(move.getStartPosition(), null);
        cBoard.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), finalType));
        // Flip turn
        whoseTurn = oppositeTeam(whoseTurn);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        if (kingPos == null) {return false;}

        for (ChessMove move : allTeamMoves(oppositeTeam(teamColor))) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
            }
        }
        return false;
    }

    //Helper function for clone the board so no crazy stuff happens
    private ChessBoard cloneBoard() {
        ChessBoard newBoard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = cBoard.getPiece(pos);
                if (piece != null) {
                    newBoard.addPiece(pos, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
        return newBoard;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {return false;}
        return GameHelper.hasNoLegalMoves(cBoard, teamColor);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return whoseTurn == chessGame.whoseTurn && Objects.equals(cBoard, chessGame.cBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(whoseTurn, cBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "whoseTurn=" + whoseTurn +
                ", c_board=" + cBoard +
                '}';
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {return false;}
        return GameHelper.hasNoLegalMoves(cBoard, teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        // set the board to be this new one
        this.cBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        //return chessboard that was saved and created
        return cBoard;
    }
}

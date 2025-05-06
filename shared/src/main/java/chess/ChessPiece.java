package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import chess.pieces.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    //store color and type
    protected final ChessGame.TeamColor pieceColor; //add to store color
    protected final PieceType type; //add to store type

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor; //get color of piece
        this.type = type; //get type of piece
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }


    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        return switch (type) {
            case BISHOP -> bish.getBishMoves(board, myPosition, pieceColor);
            case KING -> king.getKingMoves(board, myPosition, pieceColor);
            case KNIGHT -> kngt.getKngtMoves(board, myPosition, pieceColor);
            case PAWN -> pawn.getPawnMoves(board, myPosition, pieceColor);
            case QUEEN -> queen.getQueenMoves(board, myPosition, pieceColor);
            case ROOK -> rook.getRookMoves(board, myPosition, pieceColor);
            default -> new ArrayList<>();
        };
    }
}

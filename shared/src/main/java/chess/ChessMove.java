package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 *
 *
 * My little To-do list:
 * 1. Add fields to ChessPosition and ChessPiece
 *    - pieceStartPos
 *    - pieceEndPos
 *    - promotionPiece
 *
 *    get them from ChessMove function
 *
 *    finish other two functions
 */
public class ChessMove {

    //store positions and promo piece
    private final ChessPosition pieceStartPos;
    private final ChessPosition pieceEndPos;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        //get positions and promo
        this.pieceStartPos = startPosition;
        this.pieceEndPos = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return pieceStartPos;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return pieceEndPos;
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                "pieceStartPos=" + pieceStartPos +
                ", pieceEndPos=" + pieceEndPos +
                ", promotionPiece=" + promotionPiece +
                '}';
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(pieceStartPos, chessMove.pieceStartPos) && Objects.equals(pieceEndPos, chessMove.pieceEndPos) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceStartPos, pieceEndPos, promotionPiece);
    }
}

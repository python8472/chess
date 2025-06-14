package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int pieceRow;
    private final int pieceCol;

    public ChessPosition(int row, int col) {
        //return positions
        this.pieceCol = col;
        this.pieceRow = row;
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "pieceRow=" + pieceRow +
                ", pieceCol=" + pieceCol +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return pieceRow == that.pieceRow && pieceCol == that.pieceCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceRow, pieceCol);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return pieceRow; //reutnr row
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return pieceCol; //return col
    }

    public static ChessPosition fromAlgebraic(String notation) {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("Invalid algebraic notation: " + notation);
        }
        char file = notation.charAt(0);
        char rank = notation.charAt(1);

        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Invalid position: " + notation);
        }

        int col = file - 'a' + 1;
        int row = Character.getNumericValue(rank);
        return new ChessPosition(row, col);
    }

}

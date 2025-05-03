package chess;

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
}

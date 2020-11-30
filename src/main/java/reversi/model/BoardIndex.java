package reversi.model;

/**
 * Index of a slot on a {@link Board}.
 */
class BoardIndex {

    private final int row;
    private final int col;

    /**
     * Creates a new index that points to a slot on a {@link Board}.
     *
     * @param row The row number of the slot.
     * @param col The column number of the slot.
     */
    BoardIndex(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the next slot in the direction of {@code direction}.
     *
     * @param direction The direction from which the next slot should be
     *                  returned.
     * @return The index of the next slot in the direction of {@code direction}.
     */
    BoardIndex getNext(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException();
        } else {
            switch (direction) {
            case UP:
                return new BoardIndex(row - 1, col);

            case DOWN:
                return new BoardIndex(row + 1, col);

            case LEFT:
                return new BoardIndex(row, col - 1);

            case RIGHT:
                return new BoardIndex(row, col + 1);

            case UPLEFT:
                return new BoardIndex(row - 1, col - 1);

            case UPRIGHT:
                return new BoardIndex(row - 1, col + 1);

            case DOWNLEFT:
                return new BoardIndex(row + 1, col - 1);

            case DOWNRIGHT:
                return new BoardIndex(row + 1, col + 1);

            default:
                throw new Error();
            }
        }
    }

    /**
     * The row number of this index.
     *
     * @return The row number.
     */
    int getRow() {
        return row;
    }

    /**
     * The column number of this index.
     *
     * @return The column number.
     */
    int getCol() {
        return col;
    }
}

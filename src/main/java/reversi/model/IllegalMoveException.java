package reversi.model;

/**
 * An exception that is thrown if a move is made that is not permitted according
 * to the game rules of the game {@link Reversi}.
 */
public class IllegalMoveException extends RuntimeException {

    private static final long serialVersionUID = 2988306404829342231L;

    /**
     * Creates a new IllegalMoveException.
     */
    public IllegalMoveException() {
        super();
    }

    /**
     * Creates a new IllegalMoveException.
     *
     * @param message String that describes the source of the error.
     */
    public IllegalMoveException(String message) {
        super(message);
    }
}

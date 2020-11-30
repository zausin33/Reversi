package reversi.model;

/**
 * A direction on a game {@link Board}
 */
public enum Direction {

    /**
     * The left direction on the board.
     */
    LEFT,

    /**
     * The up direction on the board.
     */
    UP,

    /**
     * The right direction on the board.
     */
    RIGHT,

    /**
     * The down direction on the board.
     */
    DOWN,

    /**
     * The diagonal up and left direction on the board.
     */
    UPLEFT,

    /**
     * The diagonal up and right direction on the board.
     */
    UPRIGHT,

    /**
     * The diagonal down and left direction on the board.
     */
    DOWNLEFT,

    /**
     * The diagonal down and right direction on the board.
     */
    DOWNRIGHT
}

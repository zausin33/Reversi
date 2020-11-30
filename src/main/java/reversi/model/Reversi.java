package reversi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A game board of the game Reversi.
 * A human plays against the machine.
 */
public class Reversi implements Board {

    /**
     * The maximum level of difficulty the game can accept.
     */
    public static final int MAX_LEVEL = 5;

    /**
     * The minimum level of difficulty the game can accept.
     */
    public static final int MIN_LEVEL = 1;

    /**
     * The standard level of difficulty of the game.
     */
    public static final int DEFAULT_LEVEL = 3;

    private static final int[][] SCORE_MATRIX = {
            {9999,   5, 500, 200, 200, 500,   5, 9999},
            {   5,   1,  50, 150, 150,  50,   1,    5},
            { 500,  50, 250, 100, 100, 250,  50,  500},
            { 200, 150, 100,  50,  50, 100, 150,  200},
            { 200, 150, 100,  50,  50, 100, 150,  200},
            { 500,  50, 250, 100, 100, 250,  50,  500},
            {   5,   1,  50, 150, 150,  50,   1,    5},
            {9999,   5, 500, 200, 200, 500,   5, 9999}};

    private final Player firstPlayer;
    private int level;
    private Player[][] board;
    private Player nextPlayer;
    private boolean gameOver;

    /**
     * Creates a new game board.
     *
     * @param firstPlayer Is the starting player of the new game board.
     */
    public Reversi(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
        level = DEFAULT_LEVEL;
        board = new Player[SIZE][SIZE];
        nextPlayer = firstPlayer;
        setStartPosition();
        gameOver = false;
    }

    /**
     * Initializes this game board with the starting position of the game tiles.
     */
    private void setStartPosition() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = Player.NOBODY;
            }
        }

        int middle = SIZE / 2 - 1;
        Player enemy = firstPlayer.getOpponent();
        board[middle][middle] = enemy;
        board[middle][middle + 1] = firstPlayer;
        board[middle + 1][middle + 1] = enemy;
        board[middle + 1][middle] = firstPlayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player next() {
        return nextPlayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board move(int row, int col) {
        if (!isInBoard(row, col)) {
            throw new IllegalArgumentException();
        } else if (gameOver || nextPlayer == Player.COMPUTER) {
            throw new IllegalMoveException();
        } else {
            return makeMove(row, col);
        }
    }

    /**
     * Checks whether the given slot exists on a reversi board.
     *
     * @param row The row number of the Slot to be checked.
     * @param col The column number of the Slot to be checked.
     * @return {@code true} if and only if the given slot exists on a
     *         game board.
     */
    public static boolean isInBoard(int row, int col) {
        return (row > 0 && row <= SIZE && col > 0 && col <= SIZE);
    }

    /**
     * Makes the move of the {@link #nextPlayer} on this game board.
     * The move is executed on a copy of {@code this} by placing a
     * player's tile on the given slot and then flipping the opponent's
     * corresponding tiles.
     * Then the copy on which the move was made is returned.
     *
     * @param row The slot's row number where a tile of the {@link #nextPlayer}
     *            should be placed on.
     * @param col The slot's column number where a tile of the
     *            {@link #nextPlayer} should be placed on.
     * @return A new board with the move executed. If the move is not valid,
     *         e.g., the defined slot was occupied and not at least one tile of
     *         the machine was reversed, then {@code null} will be returned.
     */
    private Board makeMove(int row, int col) {
        assert isInBoard(row, col);

        List<BoardIndex> stonesToFlip = findStonesToFlip(row, col, nextPlayer);

        // Is move on board legal?
        if (getSlot(row, col) == Player.NOBODY && !stonesToFlip.isEmpty()) {

            // Execution of the move on the cloned board.
            Reversi clone = clone();
            clone.board[row - 1][col - 1] = nextPlayer;
            clone.flipStones(stonesToFlip);

            // Only set nextPlayer to the enemy player if the enemy player can
            // make a move.
            if (clone.getPossibleMoves(nextPlayer.getOpponent()).size() > 0) {
                clone.nextPlayer = nextPlayer.getOpponent();
            }

            clone.checkGameOver();
            return clone;
        } else {
            return null;
        }
    }

    /**
     * Searches in every direction from the given slot whether the opponent's
     * tiles can be flipped. Opposing tiles can then be flipped if
     * they are enclosed in a line between the slot given and a tile of
     * {@code player}.
     *
     * @param row The slot's row number, from which to search for the opponent's
     *           enclosed tiles.
     * @param col The slot's column number, from which to search for the
     *            opponent's enclosed tiles.
     * @param player The player under whose opposing tokens is searched
     *               whether they can be flipped.
     * @return A list of indexes which indicate the slots that contain tiles
     *         which can be flipped.
     */
    private List<BoardIndex> findStonesToFlip(int row, int col, Player player) {
        assert isInBoard(row, col);
        assert player != null;

        Player enemy = player.getOpponent();
        BoardIndex index = new BoardIndex(row, col);
        List<BoardIndex> toFlip = new ArrayList<>();
        List<BoardIndex> temp = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            BoardIndex nextSlot = index.getNext(direction);
            int nextRow = nextSlot.getRow();
            int nextCol = nextSlot.getCol();

            // Runs in one direction until the Slot isn't located on the board
            // or there is no longer an opposing tile.
            // Stores all slots containing opposing tiles in temp.
            while (isInBoard(nextRow, nextCol)
                    && getSlot(nextRow, nextCol) == enemy) {
                temp.add(nextSlot);
                nextSlot = nextSlot.getNext(direction);
                nextRow = nextSlot.getRow();
                nextCol = nextSlot.getCol();
            }

            // Checks if the stored opposing tiles are enclosed.
            if (isInBoard(nextRow, nextCol)
                    && getSlot(nextRow, nextCol) == player) {
                toFlip.addAll(temp);
            }

            temp.clear();
        }
        return toFlip;
    }

    /**
     * Converts the game tiles on all given slots to the opposing player's
     * tiles.
     *
     * @param toFlip A list of indexes that indicate all of the slots on
     *               which the tiles must be flipped.
     */
    private void flipStones(List<BoardIndex> toFlip) {
        for (BoardIndex index : toFlip) {
            board[index.getRow() - 1][index.getCol() - 1] = nextPlayer;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reversi clone() {
        Reversi clone;

        try {
            clone = (Reversi) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }

        Player[][] cloneBoard = new Player[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            cloneBoard[i] = board[i].clone();
        }

        clone.board = cloneBoard;
        return clone;
    }

    /**
     * Returns a list of the indexes of all slots on which the given
     * {@code player} could put a game tile.
     *
     * @param player The player for whom it is determined on which slots he
     *               could place his game tile.
     * @return A list of slot indexes, which specify the slots on which
     *         {@code player} is allowed to put a tile according to the rules of
     *         the game.
     */
    private List<BoardIndex> getPossibleMoves(Player player) {
        assert player != null;

        List<BoardIndex> possibleMoves = new ArrayList<>();

        for (int i = 1; i <= SIZE; i++) {
            for (int j = 1; j <= SIZE; j++) {
                if (getSlot(i, j) == Player.NOBODY
                        && !findStonesToFlip(i, j, player).isEmpty()) {
                    possibleMoves.add(new BoardIndex(i, j));
                }
            }
        }

        return possibleMoves;
    }

    /**
     * Checks whether this game is over.
     * This game is over if and only if both of the players have no possibility
     * to place a tile on the board.
     */
    private void checkGameOver() {
        if (getPossibleMoves(nextPlayer).size() == 0
                && getPossibleMoves(nextPlayer.getOpponent()).size() == 0) {
            gameOver = true;
            nextPlayer = Player.NOBODY;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board machineMove() {
        if (nextPlayer == Player.HUMAN || gameOver) {
            throw new IllegalMoveException();
        } else {
            GameTreeNode root = new GameTreeNode(this, 0, 0);
            buildGameTree(root);
            root.setFinalScores();
            return root.getMaxChild().getBoard();
        }
    }

    /**
     * Recursively builds a tree of depth {@link #level}. Each board will have
     * boards with all possible further moves executed as child nodes added to
     * create a tree that predicts all next {@link #level} moves.
     *
     * @param node The node to which all other possible moves are attached as
     *             children.
     */
    private void buildGameTree(GameTreeNode node) {
        assert node != null;

        int depth = node.getDepth();

        if (depth < level) {
            depth++;
            List<BoardIndex> possibleMoves
                    = node.getBoard().getPossibleMoves(node.getBoard().next());
            for (BoardIndex index : possibleMoves) {
                Reversi reversi = (Reversi) node.getBoard().makeMove(
                        index.getRow(), index.getCol());

                assert reversi != null;
                GameTreeNode child = new GameTreeNode(reversi,
                        reversi.determineScore(), depth);
                node.addChild(child);
                buildGameTree(child);
            }
        }
    }

    /**
     * Calculates the score of {@code this} from the three partial scores.
     *
     * @return The score of this board/game.
     */
    private double determineScore() {
        return determineScoreT() + determineScoreM() + determineScoreP();
    }

    /**
     * Calculates the score T for {@code this} based on the
     * {@link #SCORE_MATRIX} and the current occupancy of the game board.
     *
     * @return The score T that is calculated from the current occupancy of the
     *         game board.
     */
    private double determineScoreT() {
        int scoreMachine = 0;
        int scoreHuman = 0;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int points = SCORE_MATRIX[i][j];

                if (board[i][j] == Player.COMPUTER) {
                    scoreMachine += points;
                } else if (board[i][j] == Player.HUMAN) {
                    scoreHuman += points;
                }
            }
        }

        return scoreMachine - 1.5 * scoreHuman;
    }

    /**
     * Calculates the score M based on all moves of both players
     *
     * @return The score M.
     */
    private double determineScoreM() {
        int mobilityMachine = getPossibleMoves(Player.COMPUTER).size();
        int mobilityHuman = getPossibleMoves(Player.HUMAN).size();
        int stonesOnBoard = getNumberOfHumanTiles() + getNumberOfMachineTiles();

        return ((double) SIZE * SIZE / stonesOnBoard)
                * (3.0 * mobilityMachine - 4.0 * mobilityHuman);
    }

    /**
     * Calculates the score P based on the number of free slots around the
     * tiles of the enemy player.
     *
     * @return The score P.
     */
    private double determineScoreP() {
        int potentialMachine = getPotential(Player.COMPUTER);
        int potentialHuman = getPotential(Player.HUMAN);
        int stonesOnBoard = getNumberOfHumanTiles() + getNumberOfMachineTiles();

        return ((double) SIZE * SIZE / (2 * stonesOnBoard))
                * (2.5 * potentialMachine - 3.0 * potentialHuman);
    }

    /**
     * Calculates how many free slots are around the opponent's tiles.
     *
     * @param player The player whose opponent's free slots
     *               around his tiles are calculated.
     * @return The number of free slots around the opposing tiles.
     */
    private int getPotential(Player player) {
        assert player != null;

        int counter = 0;
        Player enemy = player.getOpponent();

        for (int i = 1; i <= SIZE; i++) {
            for (int j = 1; j <= SIZE; j++) {
                BoardIndex index = new BoardIndex(i, j);
                if (getSlot(index.getRow(), index.getCol()) == enemy) {
                    for (Direction direction : Direction.values()) {
                        BoardIndex next = index.getNext(direction);
                        int nextRow = next.getRow();
                        int nextCol = next.getCol();
                        if (isInBoard(nextRow, nextCol)
                                && getSlot(nextRow, nextCol) == Player.NOBODY) {
                            counter++;
                        }
                    }
                }
            }
        }
        return counter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(int level) {
        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new IllegalArgumentException();
        } else {
            this.level = level;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean gameOver() {
        return gameOver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getWinner() {
        if (!gameOver) {
            throw new IllegalStateException();
        } else {
            int human = getNumberOfHumanTiles();
            int machine = getNumberOfMachineTiles();

            if (human > machine) {
                return Player.HUMAN;
            } else if (human < machine) {
                return Player.COMPUTER;
            } else {
                return Player.NOBODY;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfHumanTiles() {
        return getNumberOfTiles(Player.HUMAN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMachineTiles() {
        return getNumberOfTiles(Player.COMPUTER);
    }

    /**
     * Counts the number of tiles of {@code player} on the board.
     *
     * @param player The player whose tiles on the board are counted.
     * @return The number of tiles of {@code player} on the board .
     */
    private int getNumberOfTiles(Player player) {
        assert player != null;

        int number = 0;

        for (Player[] fields : board) {
            for (Player field : fields) {
                if (field == player) {
                    number++;
                }
            }
        }
        return number;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getSlot(int row, int col) {
        if (!isInBoard(row, col)) {
            throw new IllegalArgumentException();
        } else {
            return board[row - 1][col - 1];
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Player[] fields : board) {
            for (Player field : fields) {
                if (field == Player.HUMAN) {
                    builder.append("X ");
                } else if (field == Player.COMPUTER) {
                    builder.append("O ");
                } else {
                    builder.append(". ");
                }
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("\n");
        }

        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}

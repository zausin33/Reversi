package reversi.view;

import reversi.model.Board;
import reversi.model.Player;
import reversi.model.Reversi;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;

import static reversi.model.Board.SIZE;
import static reversi.model.Player.COMPUTER;
import static reversi.model.Player.HUMAN;

/**
 * A JPanel that visualizes the {@link Board} of the game Reversi.
 */
class GameBoard extends JPanel {

    private Board board;
    private int level;
    private Stack<Board> gameStack;
    private Thread machineThread;

    /**
     * Creates this gameBoard and initializes it with the start allocation.
     */
    GameBoard() {
        level = Reversi.DEFAULT_LEVEL;
        gameStack = new Stack<>();
        setLayout(new GridLayout(SIZE, SIZE));
        initializeGameBoard();
        generateNewBoard(HUMAN);
    }

    /**
     * Opens a new game and takes over the starting player of the last game.
     */
    void makeNewGame() {
        generateNewBoard(board.getFirstPlayer());
    }

    /**
     * Creates a new {@link Board}, so opens a new game, sets its level to
     * {@link #level} and its starting player to {@code firstPlayer}. If the
     * machine is the starting player, {@link #machineMove()} is executed
     * immediately.
     *
     * @param firstPlayer Is the starting player of the board to be created.
     */
    private void generateNewBoard(Player firstPlayer) {
        assert firstPlayer != null;
        assert level >= Reversi.MIN_LEVEL && level <= Reversi.MAX_LEVEL;

        stopMachineThread();
        board = new Reversi(firstPlayer);
        board.setLevel(level);
        gameStack.clear();
        update();

        if (firstPlayer == COMPUTER) {
            startMachineThread();
        }
    }

    /**
     * Initializes {@code this} with all slots.
     */
    private void initializeGameBoard() {
        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Slot clicked = (Slot) e.getComponent();
                humanMove(clicked.getRow(), clicked.getCol());
            }
        };

        for (int i = 1; i <= SIZE; i++) {
            for (int j = 1; j <= Board.SIZE; j++) {
                JPanel slot = new Slot(i, j);
                slot.addMouseListener(listener);
                add(slot);
            }
        }
    }

    /**
     * Updates this visualization of the game board by checking which slots are
     * occupied by game tiles of the human player or the machine player and then
     * showing the tiles on the appropriate slots. The point displays are then
     * updated and the undo button is set to enabled or disabled.
     */
    void update() {
        MainFrame frame = (MainFrame) getTopLevelAncestor();

        // Updates all slots.
        for (Component c : getComponents()) {
            if (c instanceof Slot) {
                Slot s = (Slot) c;
                int row = s.getRow();
                int col = s.getCol();
                s.setPlayer(board.getSlot(row, col));
            }
        }

        if (frame != null) {
            // Updates score displays and undo button.
            int blue = board.getNumberOfHumanTiles();
            int red = board.getNumberOfMachineTiles();
            frame.setScoresDisplay(blue, red);
            frame.enableUndoButton(!gameStack.empty());
        }

        repaint();
    }

    /**
     * Starts a new game and switches the start player.
     */
    void switchFirstPlayer() {
        Player newFirstPlayer = board.getFirstPlayer().getOpponent();
        generateNewBoard(newFirstPlayer);
    }

    /**
     * Makes the move of the human player on the {@link Board}.
     * Then if the machine can make a move, the move of the machine is
     * executed with {@link #machineMove()}.
     * If the game ends after the human player's turn, the winner is
     * displayed with {@link #outputWinner ()}.
     * If the game has already been over before the method was called, an
     * error message is displayed by {@link #displayMessage(String)}.
     * If the move does not matter according to the rules of the game, an error
     * message is also displayed by {@link #displayMessage(String)} and the move
     * will not be executed.
     *
     * @param row The slot's row number where a tile of the human player should
     *            be placed on.
     * @param col The slot's column number where a tile of the human player
     *            should be placed on.
     */
    private void humanMove(int row, int col) {
        board.setLevel(level);

        if (board.gameOver()) {
            displayMessage("The game is already over! You must start a new "
                    + "one.");
        } else if (board.next() != HUMAN) {
            displayMessage("The machine has to make it's move first!");
        } else {
            Board newBoard = board.move(row, col);

            // Was move of human player not legal?
            if (newBoard == null) {
                displayMessage("No valid move!");

            } else {
                gameStack.push(board);
                board = newBoard;
                update();

                // Is the game over after move of human player?
                if (board.gameOver()) {
                    outputWinner();

                    // Is machine not able to make a move after move of human?
                } else if (board.next() == HUMAN) {
                    displayMessage("Machine must miss a turn.");

                    // The game continues as normal after move of human.
                } else {
                    startMachineThread();
                }
            }
        }
    }

    /**
     * Makes the move of the machine on the {@link Board}, if the game isn't
     * over.
     * If the game ends after the move, the winner is announced with
     * {@link #outputWinner()}.
     */
    private void machineMove() {
        if (!board.gameOver()) {
            Board newBoard = board.machineMove();
            assert newBoard != null;
            board = newBoard;
            update();

            // Is the game over after move of machine?
            if (board.gameOver()) {
                outputWinner();

                // Can't human player make a move after move of machine?
            } else if (board.next() == COMPUTER) {
                displayMessage("You must miss a turn.");
                startMachineThread();
            }
        }
    }

    /**
     * Sets the {@link #level} of the game to the given value.
     *
     * @param level The value to which the level should be set.
     */
    void setLevel(int level) {
        assert level > 0;

        this.level = level;
    }

    /**
     * Opens a dialog window that shows the transferred message.
     *
     * @param message The string that is the message to be output.
     */
    private void displayMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Displays who is the winner.
     */
    private void outputWinner() {
        switch (board.getWinner()) {
        case HUMAN:
            displayMessage("Congratulations! You won.");
            break;

        case COMPUTER:
            displayMessage("Sorry! Machine wins.");
            break;

        case NOBODY:
            displayMessage("Nobody wins. Tie.");
            break;

        default:
            throw new Error();
        }
    }

    /**
     * Undoes the last move of the player and, if the machine has already moved,
     * also the last move of the machine.
     */
    void undo() {
        stopMachineThread();
        board = gameStack.pop();
        update();
    }

    /**
     * Stops the running machineThread.
     */
    @SuppressWarnings("deprecation")
    void stopMachineThread() {
        if (machineThread != null) {
            machineThread.stop();
        }
    }

    /**
     * Generates a new {@code thread} for executing {@link #machineMove()}.
     */
    private void startMachineThread() {
        machineThread = new Thread(this::machineMove);
        machineThread.start();
    }
}


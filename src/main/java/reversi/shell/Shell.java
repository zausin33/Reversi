package reversi.shell;

import reversi.model.Board;
import reversi.model.Player;
import reversi.model.Reversi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The user interface.
 * It reads the user input and executes the appropriate commands on the
 * {@link Board}/game.
 */
public final class Shell {

    private static final String PROMPT = "reversi> ";
    private static Board board;
    private static int level;

    /**
     * Utility class implies private constructor.
     */
    private Shell() { }

    /**
     * Creates a new {@link Board}, allows the user to make inputs, checks them
     * and if they match a command, the command is executed on the Board.
     *
     * @param args Is a string array of program start variables.
     * @throws IOException If an error occurs from an IO device, this exception
     *         will be thrown.
     */
    public static void main(String[] args) throws IOException {
        level = Reversi.DEFAULT_LEVEL;
        generateNewBoard(Player.HUMAN);
        BufferedReader stdin
                = new BufferedReader(new InputStreamReader(System.in));

        execute(stdin);
    }

    /**
     * Prompts users for input to the console. The input is checked for
     * correctness and if it is correct, it will be handed over to
     * {@link #chooseCommand(String[])} for execution.
     *
     * @param stdin BufferedReader for user input.
     * @throws IOException If an error occurs from an IO device, this exception
     *                     will be thrown.
     */
    private static void execute(BufferedReader stdin)
            throws IOException {
        boolean quit = false;

        while (!quit) {
            System.out.print(PROMPT);
            String input = stdin.readLine(); // reads a line

            // no further input?
            if (input == null) {
                break;
            }

            // split input on white spaces
            String[] tokens = input.trim().split("\\s+");
            if (checkNumberTokens(tokens, 1)) {
                quit = chooseCommand(tokens);
            }
        }
    }

    /**
     * Checks if the number of {@code tokens}, which represents the the
     * individual components of the input, matches with {@code number}. If this
     * is not the case, an error message will be displayed by
     * {@link #error(String)} and {@code false} returned.
     *
     * @param tokens Is the string array of tokens.
     * @param number Is the required number of tokens.
     * @return {@code true} if the number of tokens equals the required number,
     *         otherwise {@code false} is returned.
     */
    private static boolean checkNumberTokens(String[] tokens, int number) {
        if (tokens.length < number || tokens[0].length() == 0) {
            error("Your entry is not a valid command");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Selects based on the input, which command to execute on the
     * {@link Board}. Was the entry no valid command, an error message is
     * displayed by {@link #error(String)}. Finally it will return if the
     * program should be terminated.
     *
     * @param tokens The string array of tokens, which represent the individual
     *               components of a command.
     * @return {@code true} if the program should be terminated.
     */
    private static boolean chooseCommand(String[] tokens) {
        boolean quit = false;
        char first = tokens[0].toCharArray()[0];
        first = Character.toLowerCase(first);

        switch (first) {
        case 'n':   // new
            generateNewBoard(board.getFirstPlayer());
            break;

        case 'l':   // level
            if (checkNumberTokens(tokens, 2)) {
                setLevel(tokens);
            }
            break;

        case 'm':   // move
            if (checkNumberTokens(tokens, 3)) {
                move(tokens);
            }
            break;

        case 's':   // switch
            switchFirstPlayer();
            break;

        case 'p':   // print
            System.out.println(board);
            break;

        case 'h':   // help
            help();
            break;

        case 'q':   // quit
            quit = true;    // terminates program
            break;

        default:
            error("Your entry is not a valid command");
            break;
        }

        return quit;
    }

    /**
     * Creates a new {@link Board}, set its level to {@link #level} and its
     * starting player to {@code firstPlayer}. If the machine is the
     * starting player, {@link #machineMove()} is executed immediately.
     *
     * @param firstPlayer Is the starting player of the board to be created.
     */
    private static void generateNewBoard(Player firstPlayer) {
        assert firstPlayer != null;
        assert level >= Reversi.MIN_LEVEL && level <= Reversi.MAX_LEVEL;

        board = new Reversi(firstPlayer);
        board.setLevel(level);

        if (firstPlayer == Player.COMPUTER) {
            machineMove();
        }
    }

    /**
     * Sets the {@link #level} of the game to the given value. If the given
     * value is not an integer between 1 and 5, an error message is
     * displayed by {@link #error(String)}.
     *
     * @param tokens The string array in which the value to which the level
     *               should be set is stored.
     */
    private static void setLevel(String[] tokens) {
        Integer tempLvl = parseToInt(tokens[1]);

        if (tempLvl != null) {
            if (tempLvl < Reversi.MIN_LEVEL || tempLvl > Reversi.MAX_LEVEL) {
                error("The level must be an Integer from " + Reversi.MIN_LEVEL
                        + " to " + Reversi.MAX_LEVEL);
            } else {
                board.setLevel(tempLvl);
                level = tempLvl;
            }
        }
    }

    /**
     * Makes the move of the human player on the {@link Board}.
     * Then if the machine can make a move, the move of the machine is
     * executed with {@link #machineMove()}.
     * If the game ends after the human player's turn, the winner is
     * displayed with {@link #outputWinner ()}.
     * If the game has already been over before the method was called, an
     * error message is displayed by {@link #error(String)} and the move
     * will not be executed.
     * If the row or column number does not refer to a slot on the board or
     * the move does not matter according to the rules of the game, an error
     * message is also displayed by {@link #error(String)} and the move
     * will not be executed.
     *
     * @param tokens The string array, in which the row number and column number
     *               of the move are stored.
     */
    private static void move(String[] tokens) {
        Integer row = parseToInt(tokens[1]);
        Integer col = parseToInt(tokens[2]);

        if (board.gameOver()) {
            error("The Game is over. You must start a new one.");
        } else if (row != null && col != null) {
            if (!Reversi.isInBoard(row, col)) {
                error("The row and column number have to be integers from 1 "
                        + "to " + Board.SIZE);
            } else {
                Board newBoard = board.move(row, col);

                // Was move of human player not legal?
                if (newBoard == null) {
                    error("No valid Move!");

                    // Can't machine make a move after move of human player?
                } else if (board.next() == newBoard.next()) {
                    System.out.println("Machine must miss a turn.");
                    board = newBoard;

                    // Is the game over after move of human player?
                } else if (newBoard.gameOver()) {
                    outputWinner();
                    board = newBoard;

                    // The game continues as normal after move of human
                    // player.
                } else {
                    board = newBoard;
                    machineMove();
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
    private static void machineMove() {
        if (!board.gameOver()) {
            Board newBoard = board.machineMove();
            assert newBoard != null;

            // Is the game over after move of machine?
            if (newBoard.gameOver()) {
                board = newBoard;
                outputWinner();

                // Can't human player make a move after move of machine?
            } else if (board.next() == newBoard.next()) {
                System.out.println("You must miss a turn.");
                board = newBoard;
                machineMove();

                // The game continues as normal after move of machine.
            } else {
                board = newBoard;
            }
        }
    }

    /**
     * Starts a new game and switch the start player.
     */
    private static void switchFirstPlayer() {
        Player newFirstPlayer = board.getFirstPlayer().getOpponent();
        generateNewBoard(newFirstPlayer);
    }

    /**
     * Displays who is the winner.
     */
    private static void outputWinner() {
        assert board.gameOver();

        switch (board.getWinner()) {
        case HUMAN:
            System.out.println("Congratulations! You won.");
            break;

        case COMPUTER:
            System.out.println("Sorry! Machine wins.");
            break;

        default:
            System.out.println("Nobody wins. Tie.");
            break;
        }
    }

    /**
     * Displays an error massage.
     *
     * @param message The string that gives the source of the error.
     */
    private static void error(String message) {
        System.out.println("Error! " + message);
    }

    /**
     * Returns a help text that briefly describes all possible commands.
     */
    private static void help() {
        final String format = "%-10s %-15s %-14s %-12s%n";

        System.out.println("Available commands:");
        System.out.printf(format, "NEW", "", "", "Creates a new game. The level"
                + " of difficulty as well as the first player are taken from "
                + "the previous game. If it is the first game, the human "
                + "player begins and the level of difficulty is "
                + Reversi.DEFAULT_LEVEL + ".");
        System.out.printf(format, "LEVEL", "<integer lvl>", "", "Sets the "
                + "difficulty level to the value <lvl>, which must be between "
                + Reversi.MIN_LEVEL + " and " + Reversi.MAX_LEVEL + ".");
        System.out.printf(format, "MOVE", "<integer row>", "<integer col>",
                "Makes a move of the human player. <row> specifies the row "
                        + "(1-" + Board.SIZE + ") and <col> the column (1-" + Board.SIZE
                        + ") of the board on which a tile is to be placed.");
        System.out.printf(format, "SWITCH", "", "", "Starts a new game and lets"
                + " the second player open the game.");
        System.out.printf(format, "PRINT", "", "", "Prints out the current "
                + "occupancy of the board as an matrix.");
        System.out.printf(format, "HELP", "", "", "Prints out this help.");
        System.out.printf(format, "QUIT", "", "", "Exits the program.");
    }

    /**
     * If possible, converts a string to an integer. Returns {@code null} and
     * displays an error message if the handed string can not be converted to an
     * integer.
     *
     * @param str The string to be converted to an integer.
     * @return {@code null}, if the conversion was not successful, otherwise the
     *         appropriate integer.
     */
    private static Integer parseToInt(String str) {
        Integer result;
        try {
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            error("The number has to be an integer!");
            result = null;
        }
        return result;
    }
}

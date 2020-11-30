package reversi.view;

import reversi.model.Reversi;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Label;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import static reversi.model.Board.SIZE;

/**
 * The main frame in which the game {@link Reversi} is visualized.
 */
public class MainFrame extends JFrame {

    private static final int FONT_SIZE = 17;
    private JPanel panelCenter;
    private GameBoard gameBoard;
    private JPanel panelSouth;
    private JComboBox<Integer> levelSelection;
    private JLabel pointsBlue;
    private JLabel pointsRed;
    private JButton undoButton;
    private JButton newGameButton;
    private JButton switchPlayerButton;
    private JButton quitButton;

    /**
     * Generates this main frame, on which the game {@link Reversi} can then be
     * played and initializes it with the start allocation.
     */
    public MainFrame() {
        super("Reversi");
        setLayout(new BorderLayout());
        Container c = getContentPane();

        panelSouth = new JPanel();
        initializePanelSouth();

        panelCenter = new JPanel();
        panelCenter.setLayout(new BorderLayout());
        initializePanelCenter();

        c.add(panelCenter, BorderLayout.CENTER);
        c.add(panelSouth, BorderLayout.SOUTH);
        gameBoard.update();
        keyActions();
    }

    /**
     * Creates a new frame and starts the game.
     *
     * @param args Is a string array of program start variables.
     */
    public static void main(String[] args) {
        JFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    /**
     * Creates all components of the {@link #panelCenter} and adds them to the
     * panel which are the axis labels and the {@link GameBoard}.
     */
    private void initializePanelCenter() {
        gameBoard = new GameBoard();
        JPanel yAxis = new JPanel();
        JPanel xAxis = new JPanel();

        yAxis.setLayout(new BoxLayout(yAxis, BoxLayout.Y_AXIS));
        xAxis.setLayout(new BoxLayout(xAxis, BoxLayout.X_AXIS));
        xAxis.add(new JLabel("       "), BorderLayout.WEST);
        xAxis.add(Box.createHorizontalGlue());

        for (int i = 1; i <= SIZE; i++) {
            JLabel label = new JLabel("   " + i + "   ");
            yAxis.add(Box.createVerticalGlue());
            yAxis.add(label);
            yAxis.add(Box.createVerticalGlue());
            xAxis.add(Box.createHorizontalGlue());
            xAxis.add(new Label(label.getText()));
            xAxis.add(Box.createHorizontalGlue());
        }

        panelCenter.add(gameBoard, BorderLayout.CENTER);
        panelCenter.add(xAxis, BorderLayout.NORTH);
        panelCenter.add(yAxis, BorderLayout.WEST);
    }

    /**
     * Creates all components of the {@link #panelSouth} and adds them to the
     * panel. Generates, among other things, all buttons with their action
     * listeners.
     */
    private void initializePanelSouth() {
        pointsBlue = new JLabel();
        pointsRed = new JLabel();
        newGameButton = new JButton("New");
        switchPlayerButton = new JButton("switch");
        undoButton = new JButton("Undo");
        quitButton = new JButton("Quit");
        levelSelection = new JComboBox<>();

        pointsBlue.setForeground(Color.BLUE);
        pointsBlue.setFont(new Font("Sans-Serif", Font.BOLD, FONT_SIZE));
        pointsRed.setForeground(Color.RED);
        pointsRed.setFont(new Font("Sans-Serif", Font.BOLD, FONT_SIZE));

        for (int i = Reversi.MIN_LEVEL; i <= Reversi.MAX_LEVEL; i++) {
            levelSelection.addItem(i);
        }
        levelSelection.setSelectedItem(Reversi.DEFAULT_LEVEL);

        panelSouth.add(pointsBlue);
        panelSouth.add(levelSelection);
        panelSouth.add(newGameButton);
        panelSouth.add(switchPlayerButton);
        panelSouth.add(undoButton);
        panelSouth.add(quitButton);
        panelSouth.add(pointsRed);

        newGameButton.addActionListener(e -> gameBoard.makeNewGame());
        switchPlayerButton.addActionListener(
                e -> gameBoard.switchFirstPlayer());
        undoButton.addActionListener(e -> gameBoard.undo());
        quitButton.addActionListener(e -> quit());
        levelSelection.addActionListener(e -> gameBoard.setLevel(
                levelSelection.getItemAt(levelSelection.getSelectedIndex())));
    }

    /**
     * Sets the score displays for the number of pieces of both players to the
     * given values.
     *
     * @param blue The number of game tiles of the blue player, so the human.
     * @param red  The number of game tiles of the red player, so the machine.
     */
    void setScoresDisplay(int blue, int red) {
        assert blue > 0 && red > 0;

        pointsBlue.setText("   " + blue + "   ");
        pointsRed.setText("   " + red + "   ");
    }

    /**
     * Enables the undo button if the value passed is {@code true}, otherwise
     * the undo button is disabled.
     *
     * @param doEnable The boolean value that indicates whether the undoButton
     *                 should be enabled or disabled.
     */
    void enableUndoButton(boolean doEnable) {
        undoButton.setEnabled(doEnable);
    }

    /**
     * Associates certain key combinations with certain buttons so that the key
     * combinations can be pressed instead of the buttons.
     */
    private void keyActions() {
        KeyStroke altN = KeyStroke.getKeyStroke(KeyEvent.VK_N,
                KeyEvent.ALT_DOWN_MASK);
        KeyStroke altS = KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.ALT_DOWN_MASK);
        KeyStroke altU = KeyStroke.getKeyStroke(KeyEvent.VK_U,
                KeyEvent.ALT_DOWN_MASK);
        KeyStroke altQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                KeyEvent.ALT_DOWN_MASK);

        registerKeys(newGameButton, (ActionEvent e) -> gameBoard.makeNewGame(),
                altN);
        registerKeys(switchPlayerButton,
                (ActionEvent e) -> gameBoard.switchFirstPlayer(), altS);
        registerKeys(undoButton, (ActionEvent e) -> gameBoard.undo(), altU);
        registerKeys(quitButton, (ActionEvent e) -> quit(), altQ);
    }

    /**
     * Links the given key combination with a {@code Button} and a
     * {@code ActionListener}.
     *
     * @param component The {@code Button} to be linked.
     * @param listener  the {@code ActionListener} to be linked.
     * @param keyStroke The key combination.
     */
    private void registerKeys(JComponent component, ActionListener listener,
                              KeyStroke keyStroke) {
        component.registerKeyboardAction(listener, keyStroke,
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Exits the program and closes this frame.
     */
    private void quit() {
        for (Window w : Window.getWindows()) {
            w.dispose();
        }
        gameBoard.stopMachineThread();
    }
}



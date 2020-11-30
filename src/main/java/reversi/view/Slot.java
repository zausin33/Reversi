package reversi.view;

import reversi.model.Player;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import static reversi.model.Player.HUMAN;
import static reversi.model.Player.NOBODY;

/**
 * A Slot which represents a single cell of the {@link GameBoard}.
 */
public class Slot extends JPanel {

    private final int row;
    private final int col;
    private Player player;

    /**
     * Creates a new slot.
     *
     * @param row The row number of this slot.
     * @param col The column number of this slot.
     */
    Slot(int row, int col) {
        super();
        this.row = row;
        this.col = col;
        setBackground(Color.green);
    }

    /**
     * Paints this slot and also paints the game tile when there is one placed
     * on the slot.
     *
     * @param g Graphics for painting on the components.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        if (player != NOBODY) {
            if (player == HUMAN) {
                g2.setColor(Color.BLUE);
            } else {
                g2.setColor(Color.RED);
            }
            g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
        }
    }

    /**
     * The column number of this slot.
     *
     * @return The column number.
     */
    int getCol() {
        return col;
    }

    /**
     * The row number of this slot.
     *
     * @return The row number.
     */
    int getRow() {
        return row;
    }

    /**
     * Sets which {@link Player} has placed a game tile on this slot.
     *
     * @param player The player whose tile is on the slot. If there is no tile
     *               on the slot, the player is {@code NOBODY}.
     */
    void setPlayer(Player player) {
        this.player = player;
    }
}

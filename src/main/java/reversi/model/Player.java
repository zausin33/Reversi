package reversi.model;

/**
 * The two players of the game.
 */
public enum Player {

    /**
     * The human player.
     */
    HUMAN {
        /**
         * Returns the opponent of the human player.
         *
         * @return The opponent of human, so {@code COMPUTER}.
         */
        @Override
        public Player getOpponent() {
            return COMPUTER;
        }
    },

    /**
     * The machine player.
     */
    COMPUTER {
        /**
         * Returns the opponent of the computer.
         *
         * @return The opponent of the computer, so {@code HUMAN}.
         */
        @Override
        public Player getOpponent() {
            return HUMAN;
        }
    },

    /**
     * None of both players
     */
    NOBODY {

        /**
         * Returns {@code null} when called, because NOBODY has no opponent.
         *
         * @return {@code null}.
         */
        @Override
        public Player getOpponent() {
            return null;
        }
    };

    /**
     * Returns the opponent of {@code this}.
     *
     * @return The opposing player.
     */
    public abstract Player getOpponent();
}

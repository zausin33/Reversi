package reversi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A node in a game tree.
 */
class GameTreeNode {

    private List<GameTreeNode> children;
    private Reversi board;
    private double score;
    private int depth;


    /**
     * Creates a new node.
     *
     * @param board The game board/current game state of the node.
     * @param score The score of {@code board}.
     * @param depth The depth of {@code this} in the tree.
     */
    GameTreeNode(Reversi board, double score, int depth) {
        children = new ArrayList<>();
        this.board = board;
        this.score = score;
        this.depth = depth;
    }

    /**
     * Adds a child node to {@code this}.
     *
     * @param child The new child node that will be added.
     */
    void addChild(GameTreeNode child) {
        assert child != null;
        children.add(child);
    }

    /**
     * Runs through the tree and sets its final score for each node.
     */
    void setFinalScores() {
        if (!children.isEmpty()) {
            for (GameTreeNode child : children) {
                child.setFinalScores();
            }

            if (board.next() == Player.HUMAN) {
                score += getMinScoreOfChildren();
            } else {
                score += getMaxScoreOfChildren();
            }
        }
    }

    /**
     * Returns the highest score of all child nodes.
     *
     * @return The highest score of all child nodes.
     */
    private double getMaxScoreOfChildren() {
        return getMaxChild().score;
    }

    /**
     * Returns the smallest score of all child nodes.
     *
     * @return The smallest score of all child nodes.
     */
    private double getMinScoreOfChildren() {
        double min = Double.POSITIVE_INFINITY;

        for (GameTreeNode child : children) {
            if (child.score < min) {
                min = child.score;
            }
        }

        return min;
    }

    /**
     * Returns the child with the highest score of all child nodes.
     *
     * @return The child node with the highest score. If several child nodes
     *         have the same highest score, the one inserted first is returned.
     */
    GameTreeNode getMaxChild() {
        GameTreeNode maxChild = null;
        double maxScore = Double.NEGATIVE_INFINITY;

        for (GameTreeNode child : children) {
            if (child.score > maxScore) {
                maxChild = child;
                maxScore = child.score;
            }
        }

        return maxChild;
    }

    /**
     * Returns the game board.
     *
     * @return The game board.
     */
    Reversi getBoard() {
        return board;
    }

    /**
     * Returns the depth of {@code this} in the game tree.
     *
     * @return The depth of {@code this}.
     */
    int getDepth() {
        return depth;
    }
}



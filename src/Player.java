import java.util.ArrayList;
import java.util.Collections;

/**
 * A player within the game of Scrabble. A glorified pieceBag with slightly different behaviors
 */
public class Player extends PieceBag {

    /**
     * Name of player
     */
    private final String name;
    /**
     * Whether they are in the game or not. They can leave the game by giving up, or are removed by having no pieces
     */
    private boolean inGame;
    /**
     * The amount of points the player has
     */
    private int points;

    /**
     * Constructs a player given a name, all other values are defaulted
     * @param name Name given to player
     */
    public Player(String name) {
        super();
        this.name = name;
        inGame = true;
        points = 0;
    }

    /**
     * Gets player name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the players amount of points
     * @return points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Adds points to the player's total
     * @param points amount to add
     */
    public void addPoints(int points) {
        this.points += points;
    }

    /**
     * Removes pieces from player's bag, given they exist
     * @return true if player has pieces, false if player does not have pieces
     */
    public boolean TakePieces(ArrayList<Character> pieces) {
        for (Character piece : pieces) {
            if (!this.pieces.remove(piece)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sorts the player's pieces, it's not in PieceBag as the PieceBag does not need to be sorted
     */
    public void sort() {
        Collections.sort(pieces);
    }

    /**
     * Adds an array of pieces to the players hand, and sorts the pieces
     * @param pieces Pieces to be added, a blank is a space
     */
    public void addPieces(ArrayList<Character> pieces) {
        for (Character piece : pieces) {
            this.pieces.add(piece);
        }

        sort();
    }

    /**
     * Exchanges player's hand for a new hand using the pieceBag and sorts the hand
     * @param pieceBag PieceBag to exchange with
     */
    public void exchangePieces(PieceBag pieceBag) {
        for (Character piece : pieces) {
            pieceBag.addPieces(piece, 1);
            pieces.remove(piece);
        }

        for (int i = 0; i < 7 && pieceBag.getSize() > 0; i++) {
            pieces.add(pieceBag.takePiece());
        }

        sort();
    }

    /**
     * Returns whether the player is in the game or not
     * @return inGame
     */
    public boolean isInGame() {
        return inGame;
    }

    /**
     * Takes player out of the game
     */
    public void outOfGame() {
        inGame = false;
    }

    @Override
    public String toString() {
        return String.format("%s: %d %s", name, points, (inGame) ? "Is in game" : "Is not in game");
    }
}

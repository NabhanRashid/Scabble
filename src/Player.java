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

    private ArrayList<Character> piecesInUse;

    /**
     * Constructs a player given a name, all other values are defaulted
     * @param name Name given to player
     */
    public Player(String name) {
        super();
        this.name = name;
        inGame = true;
        points = 0;
        piecesInUse = new ArrayList<>();
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
     * Gets a letter from the player
     * @return letter, '-' if it is a blank
     */
    public int getLetters(int index) {
        if (pieces.get(index) == ' ') {
            return '_';
        } else {
            return pieces.get(index);
        }
    }

    /**
     * Adds points to the player's total
     * @param points amount to add
     */
    public void addPoints(int points) {
        this.points += points;
    }

    /**
     * Sorts the player's pieces, it's not in PieceBag as the PieceBag does not need to be sorted
     */
    public void sort() {
        Collections.sort(pieces);
    }

    /**
     * Exchanges player's hand for a new hand using the pieceBag and sorts the hand
     * @param pieceBag PieceBag to exchange with
     */
    public void exchangePieces(PieceBag pieceBag) {
        // This looks weird, but it's not done in another way due to java not liking concurrent access to an
        // ArrayList. Interesting
        for (int i = 0, end = pieces.size(); i < end; i++) {
            char removedPiece = pieces.remove(0);

            pieceBag.addPieces(removedPiece, 1);
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

    /**
     * Uses a piece from player's bag, given they exist
     *
     * @return true if player has the piece, false if player does not
     */
    public boolean tempUse(char letter) {
        if (pieces.contains(letter)) {
            pieces.remove((Character) letter);
            piecesInUse.add(letter);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Discards the values of piecesInUse and draws new letters from the PieceBag
     *
     * @param pieceBag bag to draw pieces from
     */
    public void successfulPlay(PieceBag pieceBag) {
        piecesInUse.clear();
        while (pieces.size() < 7 && pieceBag.getSize() > 0) {
            pieces.add(pieceBag.takePiece());
        }
        sort();
    }

    /**
     * Returns the pieces that failed to be placed to the player's hand
     */
    public void unsuccessfulPlay() {
        for (Character piece : piecesInUse) {
            pieces.add(piece);
        }
        piecesInUse.clear();
        sort();
    }
}

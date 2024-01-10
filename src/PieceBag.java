import java.util.ArrayList;
import java.util.Collections;

public class PieceBag {
    /**
     * ArrayList of pieces that the bag currently has, stored as characters
     */
    protected ArrayList<Character> pieces;

    /**
     * Instantiation for PieceBag that initializes the ArrayList
     */
    public PieceBag() {
        pieces = new ArrayList<Character>();
    }

    /**
     * Shuffles the PieceBag after letters have been added
     */
    public void randomize() {
        Collections.shuffle(pieces);
    }

    /**
     * Takes a piece from the top of the bag, removing it
     *
     * @return piece
     */
    public char takePiece() {
        char piece = (char) pieces.get(0);
        pieces.remove(0);
        return piece;
    }

    /**
     * Adds pieces to the bag, randomizing afterwards
     *
     * @param piece character to be added
     * @param amount how many of that character to be added
     */
    public void addPieces(char piece, int amount) {
        for (int i = 0; i < amount; i++) {
            pieces.add(piece);
        }
        randomize();
    }

    /**
     * Gives the number of pieces in the bag
     *
     * @return size
     */
    public int getSize() {
        return pieces.size();
    }
}

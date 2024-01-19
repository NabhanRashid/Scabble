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
        pieces = new ArrayList<>();
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
        char piece = pieces.get(0);
        try {
            pieces.remove(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("There are no more pieces");
        }
        return piece;
    }

    /**
     * Adds pieces to the bag, randomizing afterward
     *
     * @param piece character to be added, a blank is a space
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

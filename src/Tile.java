/**
 * The tiles of the board
 */
public class Tile {
    /**
     * The letter which the tile represents 0 for a tile which doesn't have something played on it
     */
    private char letter;
    /**
     * Whether the tile is a blank tile (A wild tile which doesn't have a point count)
     */
    private boolean isBlank;
    /**
     * The height of the tile, for 2D will be a boolean of 0 or 1, for 3D will be >= 0
     */
    private int height;

    /**
     * The value of each piece in the game
     */
    private static final int[] piecePoints = {
            1, 3, 3, 2, 1, // A B C D E
            4, 2, 4, 1, 8, // F G H I J
            5, 1, 3, 1, 1, // K L M N O
            3, 10, 1, 1, 1, // P Q R S T
            1, 4, 4, 8, 4, 10}; // U V W X Y Z

    /**
     * Creates a new Tile with nothing on it
     */
    public Tile() {
        this.letter = 0;
        this.height = 0;
        this.isBlank = false;
    }

    /**
     * Gets the letter the tile represents
     * @return letter
     */
    public char getLetter() {
        return letter;
    }

    /**
     * Gets whether the tile is a blank
     * @return isBlank
     */
    public boolean isBlank() {
        return isBlank;
    }

    /**
     * Gets the height of the tile
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the value of the letter the tile represents
     * @return Value of piece, 0 for blank, -1 for no represented letter
     */
    public int getPoint() {
        if (isBlank) {
            return 0;
        } else if (letter == 0) {
            return -1;
        } else {
            return piecePoints[letter - 'A'];
        }
    }

    /**
     * Gets value of character
     * @param character character for which value to get
     * @return value of character
     */
    static public int getPoint(char character) {
        if (character == '_') {
            return 0;
        }

        return piecePoints[Character.toUpperCase(character) - 'A'];
    }

    /**
     * Adds onto the tile, overlapping it with a new character, and adding to the height
     * @param letter letter to set the tile to
     * @param isBlank whether the tile is a blank for points
     */ //Hello
    public void addPiece(char letter, boolean isBlank) {
        this.letter = letter;
        this.isBlank = isBlank;
        height += 1;
    }

    /**
     * Adds onto the tile, but setting the height to a specified amount
     * @param letter letter to set the tile to
     * @param isBlank whether the tile is blank for points
     * @param height the height to set the tile to
     */
    public void addPiece(char letter, boolean isBlank, int height) {
        this.letter = letter;
        this.isBlank = isBlank;
        this.height = height;
    }

    public Tile(Tile other) {
        this.letter = other.letter;
        this.isBlank = other.isBlank;
        this.height = other.height;
    }
}

import java.util.ArrayList;

public class Board {

    /**
     * List of names for word files for the save info
     * This save info will work similar to the system used by git
     */
    private String[] wordFileNames;

    /**
     * Stores the list of all valid words
     */
    protected String[] wordList;
    /**
     * Holds the current board of tiles
     */
    protected Tile[][] currentTiles;
    /**
     * Holds an ArrayList of the players
     */
    protected ArrayList<Player> players;
    /**
     * Holds a temporary board for the current turn's play
     */
    protected Tile[][] temporaryTiles;
    /**
     * Bag of pieces in use
     */
    protected PieceBag bag;
    /**
     * Turn number
     */
    protected int turn;
    /**
     * Holds a board of modifiers for the small tile board
     *
     * The following applies to all modifier arrays:
     *      2 means double letter
     *      -2 means double word
     *      3 means triple letter
     *      -3 means triple word
     */
    protected static final int[][] smallBoardMultipliers = {
            {-2,0,0,0,2,0,2,0,0,0,-2},
            {0,-2,0,0,0,2,0,0,0,-2,0},
            {0,0,-2,0,0,0,0,0,-2,0,0},
            {0,0,0,3,0,0,0,3,0,0,0},
            {2,0,0,0,2,0,2,0,0,0,2},
            {0,2,0,0,0,-2,0,0,0,2,0},
            {2,0,0,0,2,0,2,0,0,0,2},
            {0,0,0,3,0,0,0,3,0,0,0},
            {0,0,-2,0,0,0,0,0,-2,0,0},
            {0,-2,0,0,0,2,0,0,0,-2,0},
            {-2,0,0,0,2,0,2,0,0,0,-2}};
    /**
     * Holds a board of modifiers for the middle tile board
     */
    protected static final int[][] mediumBoardMultipliers = {
            {-2,0,0,0,2,0,2,0,0,0,-2},
            {0,-2,0,0,0,2,0,0,0,-2,0},
            {0,0,-2,0,0,0,0,0,-2,0,0},
            {0,0,0,3,0,0,0,3,0,0,0},
            {2,0,0,0,2,0,2,0,0,0,2},
            {0,2,0,0,0,-2,0,0,0,2,0},
            {2,0,0,0,2,0,2,0,0,0,2},
            {0,0,0,3,0,0,0,3,0,0,0},
            {0,0,-2,0,0,0,0,0,-2,0,0},
            {0,-2,0,0,0,2,0,0,0,-2,0},
            {-2,0,0,0,2,0,2,0,0,0,-2}};
    /**
     * Holds a board of modifiers for the large tile board
     */
    protected static final int[][] largeBoardMultipliers = {
            {-2,0,0,0,2,0,2,0,0,0,-2},
            {0,-2,0,0,0,2,0,0,0,-2,0},
            {0,0,-2,0,0,0,0,0,-2,0,0},
            {0,0,0,3,0,0,0,3,0,0,0},
            {2,0,0,0,2,0,2,0,0,0,2},
            {0,2,0,0,0,-2,0,0,0,2,0},
            {2,0,0,0,2,0,2,0,0,0,2},
            {0,0,0,3,0,0,0,3,0,0,0},
            {0,0,-2,0,0,0,0,0,-2,0,0},
            {0,-2,0,0,0,2,0,0,0,-2,0},
            {-2,0,0,0,2,0,2,0,0,0,-2}};

    private static final int[] defaultMediumPieceCounts = {
            9, 2, 2,
    };

    /**
     * Creates a board from given information by player, all other information will be filled by program
     * @param boardSize The size of the board, 0 is small, 1, is medium, 2 is large.
     * @param wordFileNames Name of files from which to compile the word list. Do note the official word list must be explicitly included
     * @param playerNames Name of players, the number of players is implicitly included
     * @param pieceBagCounts The count of each piece, (e.g : 1 A, 5 B, 100 C...), -1 will take the default given by the board. 27 values A-Z plus blank count
     */
    public Board(int boardSize, String[] wordFileNames, String[] playerNames, String[] pieceBagCounts) {

    }

}

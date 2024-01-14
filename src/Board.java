import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Board {

    /**
     * List of names for word files for the save info
     * This save info will work similar to the system used by git
     */
    private final String[] wordFileNames;

    /**
     * Stores the list of all valid words
     */
    protected ArrayList<String> wordList;
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
     * Holds a board of modifiers for the small tile board (11x11)
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
     * Holds a board of modifiers for the middle tile board (15x15)
     */
    protected static final int[][] mediumBoardMultipliers = {
            {-3,0,0,2,0,0,0,-3,0,0,0,2,0,0,-3},
            {0,-2,0,0,0,3,0,0,0,3,0,0,0,-2,0},
            {0,0,-2,0,0,0,2,0,2,0,0,0,-2,0,0},
            {2,0,0,-2,0,0,0,2,0,0,0,-2,0,0,2},
            {0,0,0,0,-2,0,0,0,0,0,-2,0,0,0,0},
            {0,3,0,0,0,3,0,0,0,3,0,0,0,3,0},
            {0,0,2,0,0,0,2,0,2,0,0,0,2,0,0},
            {-3,0,0,2,0,0,0,-2,0,0,0,2,0,0,-3},
            {0,0,2,0,0,0,2,0,2,0,0,0,2,0,0},
            {0,3,0,0,0,3,0,0,0,3,0,0,0,3,0},
            {0,0,0,0,-2,0,0,0,0,0,-2,0,0,0,0},
            {2,0,0,-2,0,0,0,2,0,0,0,-2,0,0,2},
            {0,0,-2,0,0,0,2,0,2,0,0,0,-2,0,0},
            {0,-2,0,0,0,3,0,0,0,3,0,0,0,-2,0},
            {-3,0,0,2,0,0,0,-3,0,0,0,2,0,0,-3}};
    /**
     * Holds a board of modifiers for the large tile board (19x19)
     */
    protected static final int[][] largeBoardMultipliers = {
            {-3,0,0,3,0,0,0,3,0,-3,0,3,0,0,0,3,0,0,-3},
            {0,-3,0,0,2,0,2,0,0,-3,0,0,2,0,2,0,0,-3,0},
            {0,0,-3,0,0,2,0,0,0,-3,0,0,0,2,0,0,-3,0,0},
            {3,0,0,-2,0,0,0,3,0,0,0,3,0,0,0,-2,0,0,3},
            {0,2,0,0,-2,0,0,0,2,0,2,0,0,0,-2,0,0,2,0},
            {0,0,2,0,0,-2,0,0,0,2,0,0,0,-2,0,0,2,0,0},
            {0,2,0,0,0,0,-2,0,0,0,0,0,-2,0,0,0,0,2,0},
            {3,0,0,3,0,0,0,3,0,0,0,3,0,0,0,3,0,0,3},
            {0,0,0,0,2,0,0,0,2,0,2,0,0,0,2,0,0,0,0},
            {-3,-3,-3,0,0,2,0,0,0,-2,0,0,0,2,0,0,-3,-3,-3},
            {0,0,0,0,2,0,0,0,2,0,2,0,0,0,2,0,0,0,0},
            {3,0,0,3,0,0,0,3,0,0,0,3,0,0,0,3,0,0,3},
            {0,2,0,0,0,0,-2,0,0,0,0,0,-2,0,0,0,0,2,0},
            {0,0,2,0,0,-2,0,0,0,2,0,0,0,-2,0,0,2,0,0},
            {0,2,0,0,-2,0,0,0,2,0,2,0,0,0,-2,0,0,2,0},
            {3,0,0,-2,0,0,0,3,0,0,0,3,0,0,0,-2,0,0,3},
            {0,0,-3,0,0,2,0,0,0,-3,0,0,0,2,0,0,-3,0,0},
            {0,-3,0,0,2,0,2,0,0,-3,0,0,2,0,2,0,0,-3,0},
            {-3,0,0,3,0,0,0,3,0,-3,0,3,0,0,0,3,0,0,-3}};

    public void display() {
        for (int i = 0; i < players.size(); i++) {
            System.out.printf(players.get(i).getName() + ": " + players.get(i).getPoints() + "\t");
        }
        System.out.println("\n\n");
        System.out.println("Your letters: " );
    }

    /**
     * The default number of each piece for a small sized board
     * Created using ([Area of small board]/[Area of medium board] * medCounts) with ceiling rounding (It's an about 0.5 ratio)
     */
    private static final int[] defaultSmallPieceCounts = {
            5, 1, 1, 2, 6, // A B C D E
            1, 2, 1, 5, 1, // F G H I J
            1, 2, 1, 3, 4, // K L M N O
            1, 1, 3, 2, 3, // P Q R S T
            2, 1, 1, 1, 1, // U V W X Y
            1, 1 // Z Blank
    };

    /**
     * The default number of each piece for a medium sized board
     */
    private static final int[] defaultMediumPieceCounts = {
            9, 2, 2, 4, 12, // A B C D E
            2, 3, 2, 9, 1, // F G H I J
            1, 4, 2, 6, 8, // K L M N O
            2, 1, 6, 4, 6, // P Q R S T
            4, 2, 2, 1, 2, // U V W X Y
            1, 2 // Z Blank
    };

    /**
     * The default number of each piece for a big sized board
     * Created using ([Area of big board]/[Area of medium board] * medCounts) with ceiling rounding (It's about 1.6 ratio)
     */
    private static final int[] defaultBigPieceCounts = {
            15, 4, 4, 7, 20, // A B C D E
            4, 5, 4, 15, 2, // F G H I J
            2, 7, 4, 10, 13, // K L M N O
            4, 2, 10, 7, 10, // P Q R S T
            7, 4, 4, 2, 4, // U V W X Y
            2, 4 // Z Blank
    };

    /**
     * Creates a board from given information by player, all other information will be filled by program
     * @param boardSize The size of the board, 0 is small, 1, is medium, 2 is large.
     * @param wordFileNames Name of files from which to compile the word list. Do note the official word list must be explicitly included
     * @param playerNames Name of players, the number of players is implicitly included
     * @param pieceBagCounts The count of each piece, (e.g : 1 A, 5 B, 100 C...), -1 will take the default given by the board. 27 values A-Z plus blank count
     */
    public Board(int boardSize, String[] wordFileNames, String[] playerNames, int[] pieceBagCounts) {
        // Initialization of the tileBoard
        switch (boardSize) {
            case 0 -> {
                currentTiles = new Tile[11][11];
                temporaryTiles = new Tile[11][11];
            }
            case 1 -> {
                currentTiles = new Tile[15][15];
                temporaryTiles = new Tile[15][15];
            }
            case 2 -> {
                currentTiles = new Tile[19][19];
                temporaryTiles = new Tile[19][19];
            }
            default -> throw new InvalidParameterException("Not a possible board size");
        }

        for (int i = 0, size = currentTiles.length; i < size; i++) {
            for (int j = 0; j < size; j++) {
                currentTiles[i][j] = new Tile();
                temporaryTiles[i][j] = new Tile();
            }
        }

        // Definition of wordFiles to be used
        this.wordFileNames = wordFileNames.clone();

        // Addition of players with names

        players = new ArrayList<>();

        for (String name : playerNames) {
            if (name.contains(",")) {
                throw new InvalidParameterException("Do not include a name with a comma please");
            }
            players.add(new Player(name));
        }

        // Initialization and creation of the PieceBag
        if (pieceBagCounts.length != 27) {
            throw new InvalidParameterException("Define the amount of all pieces please");
        }

        bag = new PieceBag();

        for (int i = 0; i < 26; i++) {
            int count = pieceBagCounts[i];

            if (count < -1) {
                throw new InvalidParameterException("Do not add a number of pieces less than -1");
            }

            if (count == -1) {
                switch (boardSize) {
                    case 0 -> bag.addPieces((char) ('A' + i), defaultSmallPieceCounts[i]);
                    case 1 -> bag.addPieces((char) ('A' + i), defaultMediumPieceCounts[i]);
                    case 2 -> bag.addPieces((char) ('A' + i), defaultBigPieceCounts[i]);
                }
            }

            bag.addPieces((char) ('A' + i), count);
        }

        if (pieceBagCounts[26] < -1) {
            throw new InvalidParameterException("Do not add a number of pieces less than -1");
        }
        if (pieceBagCounts[26] == -1) {
            switch (boardSize) {
                case 0 -> bag.addPieces(' ', defaultSmallPieceCounts[26]);
                case 1 -> bag.addPieces(' ', defaultMediumPieceCounts[26]);
                case 2 -> bag.addPieces(' ', defaultBigPieceCounts[26]);
            }
        } else {
            bag.addPieces(' ', pieceBagCounts[26]);
        }

        // Creation of wordList

        if (wordFileNames.length < 1) {
            throw new InvalidParameterException("Must have at least one list of words");
        }

        wordList = new ArrayList<>();

        for (String fileName : wordFileNames) {
            File file = new File(fileName);

            try {
                Scanner fileReader = new Scanner(file);

                while (fileReader.hasNextLine()) {
                    wordList.add(fileReader.nextLine());
                }

            } catch (FileNotFoundException e) {
                throw new InvalidParameterException("File " + file + " does not exist");
            }
        }

        Collections.sort(wordList);

        // TODO


        // The turn starts at 0
        turn = 0;
    }

}

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

abstract class Board {

    /**
     * List of names for word files for the save info
     * This save info will work similar to the system used by git
     */
    private String[] wordFileNames;

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

        // The turn starts at 0
        turn = 0;

        // Give players their pieces
        for (Player player : players) {
            player.exchangePieces(bag);
        }
    }

    /**
     * Outputs the display for each player
     * Prints the following:
     * All player points
     * Current hand of letters
     * Current board state, with emoji tiles
     */
    public void display() {
        int currentPlayer = turn;
        for(Player player : players) {
            System.out.printf(player.getName() + ": " + player.getPoints() + "\t");
        }
        System.out.print("\n\n\nYour letters: ");
        for (int i = 0; i < 6; i++) {
            System.out.print(players.get(currentPlayer).getLetters(i) + ", ");
        }
        System.out.print(players.get(currentPlayer).getLetters(6) + "\n\n");
        printTiles(currentTiles);
    }

    /**
     * Prints a board of tiles to the console
     *
     * @param board board to be printed
     */
    public void printTiles(Tile[][] board) {
        for (int j = 0; j < board.length; j++) {
            for (int i = 0; i < board[0].length; i++) {
                if(board[j][i].isBlank()) {
                    System.out.print(board[j][i].getLetter());
                } else if (board[j][i].getLetter() == 0) {
                    System.out.print(Character.toString(0x2B1C));
                } else {
                    int unicodeEmoji = 0x1F1E5 + (board[j][i].getLetter() - 'A');
                    System.out.print(Character.toString(unicodeEmoji));
                }
            }
            System.out.println();
        }
    }

    /**
     * Saves the board to the given fileName
     * @param fileName Name of file to save to
     */
    abstract public void saveBoard(String fileName);

    /**
     * Exchange current player's pieces with pieces in the bag, and go to next player's turn
     */
    public void exchangeLetters() {
        players.get(turn).exchangePieces(bag);
        skipTurn();
    }

    /**
     * Go to the next player's turn, ensure that the player loops
     * @return true if there is a player which must play, false if there isn't
     */
    public boolean skipTurn() {
        int firstPlayer = turn;

        while (!players.get(turn).isInGame()) {
            turn += 1 % players.size();
            if (firstPlayer == turn) {
                return false;
            }
        }
        return true;
    }

    /**
     * Allows the player to play a word on the board
     * Player plays the word letter by letter using
     * Checks if the word is properly placed, and if so counts the player's turn
     * If there are no invalid words, tallies points and updates board
     * If there are invalid words, keeps current board and ends turn
     *
     * @param startPos Starting coordinates
     * @param direction Direction the word heads in
     * @return Placement success
     */
    public boolean placeWord(int[] startPos, int direction) {
        Scanner input = new Scanner(System.in);
        temporaryTiles = currentTiles;
        int[] currentPos = startPos;
        int option = 0;
        boolean placing = true;
        while (placing) {
            letterPlacement(currentPos);
            // For direction, up is 0, right is 1, down is 2, left is 3
            switch (direction) {
                case 0 -> currentPos[1] += 1;
                case 1 -> currentPos[0] += 1;
                case 2 -> currentPos[1] -= 1;
                case 3 -> currentPos[0] -= 1;
            }
            option = 0;
            while (option == 0) {
                printTiles(temporaryTiles);
                System.out.println("""
                    Choose one of the following options:
                    \tKeep playing word
                    \tFinish word
                    \tRestart turn
                    """);
                try {
                    option = Integer.parseInt(input.nextLine());
                    switch (option) {
                        case 1:
                        case 2:
                            placing = false;
                        case 3:
                            System.out.println("Restarting turn");
                            return false;
                        default:
                            System.out.println("Not a valid option");
                            option = 0;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Not a valid option");
                }
            }
        }
        if (!placementValidity()) {
            System.out.println("Word placement wasn't valid. Restarting turn");
            return false;
        }
        if (boardWordScan() == -1) {
            System.out.println("There are invalid word(s) in your placement. You do not receive any points");
        } else {
            System.out.println("Placement successful. Your play is worth " + boardWordScan() + " points");
            players.get(turn).addPoints(boardWordScan());
            currentTiles = temporaryTiles;
        }
        skipTurn();
        return true;
    }

    /**
     * Scans the entire board to ensure all words are valid
     * @return Points player receives if all words are valid, -1 if any are not
     */
    public int boardWordScan() {

        int playerPoints = 0;

        // Checking for validity of all vertical words
        for (int col = 0, end = temporaryTiles.length; col < end; col++) {
            for (int row = 0; row < end; row++) {
                // If a tile in the vertical line is not empty
                if (temporaryTiles[col][row].getLetter() != 0) {
                    int wordStart = row;
                    while (row != end && temporaryTiles[col][row].getLetter() != 0) {
                        row++;
                    }

                    // Now wordStart is the beginning of the word (inclusive) and row is the end of the word (exclusive)

                    if (!isValidWord(new int[]{col, wordStart}, new int[]{col, row})) {
                        return -1;
                    }

                    playerPoints += wordPoints(new int[]{col, wordStart}, new int[]{col, row});
                }
            }
        }

        // Checking validity for all horizontal words
        for (int row = 0, end = temporaryTiles.length; row < end; row++) {
            for (int col = 0; col < end; col++) {
                // If a tile in horizontal line is not empty
                if (temporaryTiles[col][row].getLetter() != 0) {
                    int wordStart = col;
                    while (col != end && temporaryTiles[col][row].getLetter() != 0) {
                        col++;
                    }

                    // Now wordStart is the beginning of the word (inclusive) and row is the end of the word (exclusive)

                    if (!isValidWord(new int[]{wordStart, row}, new int[]{col, row})) {
                        return -1;
                    }

                    playerPoints += wordPoints(new int[]{wordStart, row}, new int[]{col, row});
                }
            }
        }

        return playerPoints;
    }

    /**
     * Checks whether a word is within the wordList (Using binary search)
     * @param startPoint start point of where the word is (inclusive)
     * @param endPoint end point of where the word is (exclusive)
     * @return true if word is found, false if not
     */
    private boolean isValidWord(int[] startPoint, int[] endPoint) {
        // TODO
    }

    /**
     * Calculates how many points a word is worth, including pointModifiers (modified only if a letter has been PLACED on it)
     * @param startPoint start point of where the word is (inclusive)
     * @param endPoint end point of where the word is (exclusive)
     * @return points gotten from word, or 0 if the no letter in word has been placed on turn
     */
    public int wordPoints(int[] startPoint, int[] endPoint) {
        // TODO
    }

    /**
     * Compares a tile on temporaryTiles to its counterpart on currentTiles
     *
     * @param pos Location of the tile
     * @return Whether the tile has changed or not
     */
    boolean hasTileChanged(int[] pos) {
        return temporaryTiles[pos[0]][pos[1]].getHeight() != currentTiles[pos[0]][pos[1]].getHeight();
    }
    void letterPlacement(int[] pos) {
        // TODO
    }
    boolean placementValidity() {
        // TODO
    }

}

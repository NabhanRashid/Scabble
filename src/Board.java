import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

abstract class Board {

    /**
     * List of names for word files for the save info
     * This save info will work similar to the system used by git
     */
    protected ArrayList<String> wordFileNames;

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
    public static final int[] defaultSmallPieceCounts = {
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
    public static final int[] defaultMediumPieceCounts = {
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
    public static final int[] defaultBigPieceCounts = {
            15, 4, 4, 7, 20, // A B C D E
            4, 5, 4, 15, 2, // F G H I J
            2, 7, 4, 10, 13, // K L M N O
            4, 2, 10, 7, 10, // P Q R S T
            7, 4, 4, 2, 4, // U V W X Y
            2, 4 // Z Blank
    };

    /**
     * Creates an empty board object
     */
    public Board() {

    }

    /**
     * Creates a board from given information by player, all other information will be filled by program
     * @param boardSize The size of the board, 0 is small, 1, is medium, 2 is large.
     * @param wordFileNames Name of files from which to compile the word list. Do note the official word list must be explicitly included
     * @param playerNames Name of players, the number of players is implicitly included
     * @param pieceBagCounts The count of each piece, (e.g : 1 A, 5 B, 100 C...), -1 will take the default given by the board. 27 values A-Z plus blank count
     */
    public Board(int boardSize, ArrayList<String> wordFileNames, ArrayList<String> playerNames, int[] pieceBagCounts) {
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
        this.wordFileNames =  wordFileNames;

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
            } else {
                bag.addPieces((char) ('A' + i), count);
            }
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

        if (wordFileNames.isEmpty()) {
            throw new InvalidParameterException("Must have at least one list of words");
        }

        wordList = new ArrayList<>();
        //hi :) -tai

        for (String fileName : wordFileNames) {
            File file = new File(fileName);

            try {
                Scanner fileReader = new Scanner(file);

                while (fileReader.hasNextLine()) {
                    wordList.add(fileReader.nextLine().toUpperCase());
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
     * Constructs everything in the game not including the board itself from a save file
     * @param fileName file from which to construct
     */
    public Board(String fileName) {
        try {

            Scanner reader = new Scanner(new File(fileName));

            reader.nextLine();

            // Second & Third-X Lines
            int numberOfPlayers = Integer.parseInt(reader.nextLine());

            players = new ArrayList<>();

            for (int i = 0; i < numberOfPlayers; i++) {
                String[] currentPlayer = reader.nextLine().split(",");

                Player currentPlayerObject = new Player(currentPlayer[0]);

                int numberOfPieces = Integer.parseInt(currentPlayer[1]);

                for (int piece = 0; piece < numberOfPieces; piece++) {
                    currentPlayerObject.addPieces(currentPlayer[piece + 2].charAt(0), 1);
                }
                currentPlayerObject.sort();

                currentPlayerObject.addPoints(Integer.parseInt(currentPlayer[numberOfPieces + 2]));
                if (currentPlayer[numberOfPieces + 3].equals("f")) {
                    currentPlayerObject.outOfGame();
                }

                players.add(currentPlayerObject);
            }

            // Third Last Line
            this.bag = new PieceBag();

            String[] bagLine = reader.nextLine().split(",");

            for (int i = 0; i < bagLine.length; i++) {
                bag.addPieces(bagLine[i].charAt(0), 1);
            }

            wordFileNames = new ArrayList<>();

            for (String wordFileName : reader.nextLine().split(",")) {
                wordFileNames.add(wordFileName);
            }

            wordList = new ArrayList<>();

            for (String nameOfFile : wordFileNames) {
                File file = new File(nameOfFile);

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

            turn = Integer.parseInt(reader.nextLine());

            reader.close();
        } catch (IOException e) {
            throw new InvalidParameterException("File could not be opened");
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
        for(Player player : players) {
            System.out.printf(player.getName() + ": " + player.getPoints() + "\t");
        }
        printTiles(currentTiles);
    }

    /**
     * Prints a board of tiles to the console
     *
     * @param board board to be printed
     */
    public void printTiles(Tile[][] board) {
        System.out.print("\n\n\nYour letters: ");
        for (int i = 0; i < players.get(turn).getSize(); i++) {
            if (i == players.get(turn).getSize() - 1) {
                System.out.print((char) players.get(turn).getLetters(i) + "\n\n");
            } else {
                System.out.print((char) players.get(turn).getLetters(i) + ", ");
            }
        }
        if (players.get(turn).pieces.isEmpty()) {
            System.out.println("\n\n");
        }

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[0].length; x++) {
                if(board[x][y].isBlank()) {
                    System.out.print(board[x][y].getLetter() + " ");
                } else if (board[x][y].getLetter() == 0) {
                    // The unicode for a blank space
                    switch (boardSize()) {
                        case 11:
                            switch (smallBoardMultipliers[x][y]) {
                                case 2:
                                    System.out.print(Character.toString(0x1F7E6));
                                    break;
                                case -2:
                                    System.out.print(Character.toString(0x1F535));
                                    break;
                                case 3:
                                    System.out.print(Character.toString(0x1F7E5));
                                    break;
                                case -3:
                                    System.out.print(Character.toString(0x1F534));
                                    break;
                                case 0:
                                    System.out.print(Character.toString(0x2B1C));
                                    break;
                            }
                            break;
                        case 15:
                            switch (mediumBoardMultipliers[x][y]) {
                                case 2:
                                    System.out.print(Character.toString(0x1F7E6));
                                    break;
                                case -2:
                                    System.out.print(Character.toString(0x1F535));
                                    break;
                                case 3:
                                    System.out.print(Character.toString(0x1F7E5));
                                    break;
                                case -3:
                                    System.out.print(Character.toString(0x1F534));
                                    break;
                                case 0:
                                    System.out.print(Character.toString(0x2B1C));
                                    break;
                            }
                            break;
                        case 19:
                            switch (largeBoardMultipliers[x][y]) {
                                case 2:
                                    System.out.print(Character.toString(0x1F7E6));
                                    break;
                                case -2:
                                    System.out.print(Character.toString(0x1F535));
                                    break;
                                case 3:
                                    System.out.print(Character.toString(0x1F7E5));
                                    break;
                                case -3:
                                    System.out.print(Character.toString(0x1F534));
                                    break;
                                case 0:
                                    System.out.print(Character.toString(0x2B1C));
                                    break;
                            }
                            break;
                    }
                } else {
                    // The unicode is for the A emoji letter
                    int unicodeEmoji = 0x1F170 + (board[x][y].getLetter() - 'A');
                    System.out.print(Character.toString(unicodeEmoji) + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Checks if the middle tile is occupied after the first turn
     * @return true if middle tile is unoccupied
     */
    public boolean isFirstTurn() {
        return currentTiles[boardSize() / 2][boardSize() / 2].getHeight() == 0;
    }

    /**
     * Gets the current player and their info
     *
     * @return Current player
     */
    public Player currentPlayer() {
        return players.get(turn);
    }

    /**
     * Gets the size of the board
     *
     * @return Height/width of board
     */
    public int boardSize() {
        return currentTiles.length;
    }

    /**
     * Saves the board to the given fileName
     * @param fileName Name of file to save to
     */
    public void saveBoard(String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName, true);

            writer.write(players.size() + "\n");

            for (Player p : players) {
                writer.write(p.getName() + "," + p.pieces.size() + ",");

                for (Character t : p.pieces) {
                    writer.write(t + ",");
                }

                writer.write(p.getPoints() + ",");

                if (p.isInGame()) {
                    writer.write("t\n");
                } else {
                    writer.write("f\n");
                }
            }

            // Pieces in bag, will have comma at end
            for (Character t : bag.pieces) {
                writer.write(t + ",");
            }
            writer.write("\n");

            for (String name : wordFileNames) {
                writer.write(name + ",");
            }
            writer.write("\n");

            writer.write(turn + "\n");

            writer.close();
        } catch (IOException e) {
            throw new InvalidParameterException("Had issues with file creation");
        }
    }

    /**
     * Exchange current player's pieces with pieces in the bag, and go to next player's turn
     */
    public void exchangeLetters() {
        players.get(turn).exchangePieces(bag);
        skipTurn();
    }

    /**
     * Go to the next player's turn, ensure that the player pattern loops
     * @return true if there is a player which must play, false if there isn't
     */
    public boolean skipTurn() {
        int firstPlayer = turn;

         do {
            turn = (turn + 1) % players.size();
            if (firstPlayer == turn) {
                return false;
            }
        } while (!players.get(turn).isInGame());

         if (players.get(turn).pieces.isEmpty()) {
             players.get(turn).outOfGame();
             System.out.println("Skipping " + players.get(turn).getName() + " due to having no pieces");

             skipTurn();
         }

         return true;
    }

    /**
     * Returns false if there are still any players that can go
     *
     * @return if the game has ended
     */
    public boolean endOfGame() {
        for (Player player: players) {
            if (player.isInGame()) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<String> winningPlayers() {
        Player winner = players.get(0);
        ArrayList<String> winnerNames = new ArrayList<>();
        winnerNames.add(players.get(0).getName());
        for (Player player: players) {
            if (player.getPoints() > winner.getPoints()) {
                winnerNames.clear();
                winner = player;
                winnerNames.add(winner.getName());
            } else if (player.getPoints() == winner.getPoints()) {
                winnerNames.add(player.getName());
            }
        }

        return winnerNames;
    }

    /**
     * Takes the current player out of the game
     */
    public void giveUp() {
        players.get(turn).outOfGame();
    }

    /**
     * Allows the player to play a word on the board
     * Player plays the word letter by letter using
     * Checks if the word is properly placed, and if so counts the player's turn
     * If there are no invalid words, tallies points and updates board
     * If there are invalid words, keeps current board and ends turn
     *
     * @param pos Starting coordinates
     * @param direction Direction the word heads in
     * @return Placement success
     */
    public boolean placeWord(int[] pos, int direction) {
        Scanner input = new Scanner(System.in);
        for (int x = 0; x < currentTiles.length; x++) {
            for (int y = 0; y < currentTiles.length; y++) {
                temporaryTiles[x][y] = new Tile(currentTiles[x][y]);
            }
        }

        int option = 0;
        boolean placing = true;
        while (placing) {
            letterPlacement(pos);
            // For direction, up is 0, right is 1, down is 2, left is 3
            switch (direction) {
                case 0 -> pos[1] -= 1;
                case 1 -> pos[0] += 1;
                case 2 -> pos[1] += 1;
                case 3 -> pos[0] -= 1;
            }
            option = 0;
            while (option == 0) {
                printTiles(temporaryTiles);
                System.out.println("""
                        Choose one of the following options:
                        \t1: Keep playing word
                        \t2: Finish word
                        \t3: Restart turn
                        """);
                try {
                    option = Integer.parseInt(input.nextLine());
                    switch (option) {
                        case 1:
                            if (players.get(turn).pieces.isEmpty()) {
                                System.out.println("You have no pieces left, you can't do that");
                                option = 0;
                                continue;
                            }
                            try {
                                currentTiles[pos[0]][pos[1]].getLetter();
                            } catch (IndexOutOfBoundsException e) {
                                System.out.println("You cannot keep playing, you have gone off the board. Press enter to continue");
                                input.nextLine();
                                option = 0;
                                continue;
                            }
                            break;
                        case 2:
                            if (isFirstTurn()) {
                                if (temporaryTiles[temporaryTiles.length / 2][temporaryTiles.length / 2].getHeight() == 0) {
                                    System.out.println("You cannot place your word, no tile has been placed on the center tile. Press enter to restart");
                                    input.nextLine();
                                    players.get(turn).unsuccessfulPlay();
                                    return false;
                                }
                            }
                            placing = false;
                            break;
                        case 3:
                            System.out.println("Restarting turn");
                            players.get(turn).unsuccessfulPlay();
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

            for (int x = 0; x < currentTiles.length; x++) {
                for (int y = 0; y < currentTiles.length; y++) {
                    temporaryTiles[x][y] = new Tile(currentTiles[x][y]);
                }
            }
            players.get(turn).unsuccessfulPlay();
            return false;
        }
        if (boardWordScan() == -1) {
            System.out.println("There are invalid word(s) in your placement. You do not receive any points");
            players.get(turn).unsuccessfulPlay();
        } else {
            System.out.println("Placement successful. Your play is worth " + boardWordScan() + " points");
            players.get(turn).addPoints(boardWordScan());
            players.get(turn).successfulPlay(bag);

            if (bag.getSize() == 0) {
                System.out.println("THE BAG HAS RUN OUT OF PIECES");
            }

            for (int x = 0; x < currentTiles.length; x++) {
                for (int y = 0; y < currentTiles.length; y++) {
                    currentTiles[x][y] = new Tile(temporaryTiles[x][y]);
                }
            }
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
        String word = "";
        int[] currentPosition = new int[2];
        currentPosition[0] = startPoint[0];
        currentPosition[1] = startPoint[1];

        while (currentPosition[0] != endPoint[0] || currentPosition[1] != endPoint[1]) {
            word += temporaryTiles[currentPosition[0]][currentPosition[1]].getLetter();

            currentPosition[0] += (endPoint[0] - currentPosition[0] == 0) ? 0 : 1;
            currentPosition[1] += (endPoint[1] - currentPosition[1] == 0) ? 0 : 1;
        }
        if (word.length() < 2) {
            return true;
        }

        return Collections.binarySearch(wordList, word) >= 0;
    }

    /**
     * Calculates how many points a word is worth, including pointModifiers (modified only if a letter has been PLACED on it)
     * @param startPoint start point of where the word is (inclusive)
     * @param endPoint end point of where the word is (exclusive)
     * @return points gotten from word, or 0 if the no letter in word has been placed on turn
     */
    public int wordPoints(int[] startPoint, int[] endPoint) {
        if (Math.abs(startPoint[0] - endPoint[0]) + Math.abs(startPoint[1] - endPoint[1]) < 2) {
            return 0;
        }

        int wordsPoints = 0;

        int wordMultiplier = 1;
        int[] currentPosition = new int[2];
        currentPosition[0] = startPoint[0];
        currentPosition[1] = startPoint[1];
        boolean countsToPoints = false;

        int[][] boardForMultiplier;

        switch (temporaryTiles.length) {
            case 11 -> boardForMultiplier = smallBoardMultipliers;
            case 15 -> boardForMultiplier = mediumBoardMultipliers;
            case 19 -> boardForMultiplier = largeBoardMultipliers;
            default -> throw new RuntimeException("Somehow the board doesn't have one of the preset lengths");
        }

        while (currentPosition[0] != endPoint[0] || currentPosition[1] != endPoint[1]) {
            int tilePoint = temporaryTiles[currentPosition[0]][currentPosition[1]].getPoint();

            if (tilePoint == -1) {
                throw new RuntimeException("The given range by wordPoints has a tile with nothing on it");
            }

            if (hasTileChanged(currentPosition)) {
                countsToPoints = true;


                switch (boardForMultiplier[currentPosition[0]][currentPosition[1]]) {
                    case 2 -> wordsPoints += tilePoint * 2;
                    case 3 -> wordsPoints += tilePoint * 3;
                    case -2 -> {
                        wordMultiplier *= 2;
                        wordsPoints += tilePoint;
                    }
                    case -3 -> {
                        wordMultiplier *= 3;
                        wordsPoints += tilePoint;
                    }
                    default -> wordsPoints += tilePoint;
                }
            } else {
                wordsPoints += tilePoint;
            }

            currentPosition[0] += (endPoint[0] - currentPosition[0] == 0) ? 0 : 1;
            currentPosition[1] += (endPoint[1] - currentPosition[1] == 0) ? 0 : 1;
        }
        if (countsToPoints) {
            return wordsPoints * wordMultiplier;
        } else {
            return 0;
        }
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
    public abstract void letterPlacement(int[] pos);

    public abstract boolean placementValidity();
}

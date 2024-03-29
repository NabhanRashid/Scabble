import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class ThreeDBoard extends Board {
    /**
     * Creates a board from given information by player, all other information will be filled by program
     *
     * @param boardSize      The size of the board, 0 is small, 1, is medium, 2 is large.
     * @param wordFileNames  Name of files from which to compile the word list. Do note the official word list must be explicitly included
     * @param playerNames    Name of players, the number of players is implicitly included
     * @param pieceBagCounts The count of each piece, (e.g : 1 A, 5 B, 100 C...), -1 will take the default given by the board. 27 values A-Z plus blank count
     */
    public ThreeDBoard(int boardSize, ArrayList<String> wordFileNames, ArrayList<String> playerNames, int[] pieceBagCounts) {
        super(boardSize, wordFileNames, playerNames, pieceBagCounts);
    }

    /**
     * Constructs a board from a 3D saveFile
     * @param fileName Name of file to take save data from
     */
    public ThreeDBoard(String fileName) {
        super(fileName);

        try {
            Scanner reader = new Scanner(new File(fileName));

            String[] firstLine = reader.nextLine().split(",");

            // First Line
            if (!firstLine[0].equals("3D")) {
                throw new InvalidParameterException("File is not of a 3D game");
            }

            int size;

            switch (Integer.parseInt(firstLine[1])) {
                case 0:
                    size = 11;
                    break;
                case 1:
                    size = 15;
                    break;
                case 2:
                    size = 19;
                    break;
                default:
                    throw new InvalidParameterException("File does not contain a valid size");
            }

            this.currentTiles = new Tile[size][size];
            this.temporaryTiles = new Tile[size][size];

            // Looks complicated, really just reversing what happens in the saveGame
            for (int yPos = 0; yPos < size; yPos++) {
                for (int xPos = 0; xPos < size; xPos++) {
                    currentTiles[xPos][yPos] = new Tile();
                    temporaryTiles[xPos][yPos] = new Tile();
                    String tile = firstLine[yPos * size + xPos + 2];
                    int height;

                    if (tile.charAt(0) == ' ') {
                        height = Integer.parseInt(tile.substring(1, tile.length() - 1));

                        temporaryTiles[xPos][yPos].addPiece(tile.charAt(tile.length() - 1), true, height);
                        currentTiles[xPos][yPos].addPiece(tile.charAt(tile.length() - 1), true, height);
                    } else {
                        // If there is no tile on the position, just skip
                        if (tile.length() == 1) {
                            continue;
                        }

                        height = Integer.parseInt(tile.substring(0, tile.length() - 1));

                        temporaryTiles[xPos][yPos].addPiece(tile.charAt(tile.length() - 1), false, height);
                        currentTiles[xPos][yPos].addPiece(tile.charAt(tile.length() - 1), false, height);
                    }
                }
            }

            reader.close();

        } catch (IOException e) {
            throw new InvalidParameterException("File could not be opened");
        }
    }

    /**
     * Save a 3D board using the 3D notation
     * @param fileName Name of file to save to
     */
    @Override
    public void saveBoard(String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName, false);

            String size;

            switch (this.temporaryTiles.length) {
                case 11 -> size = "0";
                case 15 -> size = "1";
                case 19 -> size = "2";
                default -> throw new RuntimeException("Size of board is irregular");
            }

            writer.write("3D," + size + ",");

            // Pseudo 2D array, simple entered row by row
            for (int yPos = 0; yPos < currentTiles.length; yPos++) {
                for (int xPos = 0; xPos < currentTiles.length; xPos++) {
                    if (currentTiles[xPos][yPos].isBlank()) {
                        writer.write(" " + currentTiles[xPos][yPos].getHeight() + currentTiles[xPos][yPos].getLetter() + ",");
                    } else {
                        writer.write(currentTiles[xPos][yPos].getHeight() + currentTiles[xPos][yPos].getLetter() + ",");
                    }
                }
            }
            writer.write("\n");
            writer.close();
            super.saveBoard(fileName);

        } catch (IOException e) {
            throw new InvalidParameterException("Had issues with file creation");
        }
    }

    /**
     * Plays a letter in the specified tile
     * If there are no letters there, asks for a letter to play
     * If there is a letter there, asks whether to skip over the tile or play above it
     *
     * @param pos Position of tile being played on
     */
    @Override
    public void letterPlacement(int[] pos) {
        Scanner input = new Scanner(System.in);
        if (temporaryTiles[pos[0]][pos[1]].getLetter() == 0) {
            System.out.println("What letter would you like to play?" +
                    "\nPut an underscore in front of the letter to use a blank");
            String letter;
            boolean notPlaced = true;
            while (notPlaced) {
                letter = input.nextLine().toUpperCase();

                if (letter.isEmpty()) {
                    System.out.println("Please put something");
                    continue;
                }
                // If the inputted letter is not in the format of a blank tile or a normal tile then prompt again
                while (letter.isEmpty() || !((letter.length() == 1 && Character.isLetter(letter.charAt(0))) || (letter.charAt(0) == '_' && letter.length() == 2 && Character.isLetter(letter.charAt(0))))) {
                    System.out.println("Not a valid letter, select another letter:");
                    letter = input.nextLine().toUpperCase();
                }
                // Take and use blank piece, or throw error
                if (letter.charAt(0) == '_') {
                    if (players.get(turn).tempUse(' ')) {
                        temporaryTiles[pos[0]][pos[1]].addPiece(letter.charAt(1), true);
                        System.out.println("Blank placed, representing the letter " + letter.charAt(1));
                        notPlaced = false;
                    } else {
                        System.out.println("You do not have that letter. Choose another letter:");
                    }
                // Take and use normal piece, or throw error
                } else {
                    if (players.get(turn).tempUse(letter.charAt(0))) {
                        temporaryTiles[pos[0]][pos[1]].addPiece(letter.charAt(0), false);
                        System.out.println("Letter " + letter.charAt(0) + " placed");
                        notPlaced = false;
                    } else {
                        System.out.println("You do not have that letter. Choose another letter:");
                    }
                }
            }
        } else {
            int choice = 0;
            while (choice == 0) {
                try {
                    System.out.println("The letter " + temporaryTiles[pos[0]][pos[1]].getLetter() + " is occupying this space already." +
                            "\nChoose an option using the numbers:" +
                            "\n\t1. Play over it" +
                            "\n\t2. Leave it\n");
                    choice = Integer.parseInt(input.nextLine());
                    switch (choice) {
                        case 1:
                            System.out.println("What letter would you like to play?" +
                                    "\nPut an underscore in front of the letter to use a blank");
                            String letter;
                            boolean notPlaced = true;
                            while (notPlaced) {
                                letter = input.nextLine().toUpperCase();
                                // While the input does not follow the format of a blank or normal piece
                                while (letter.isEmpty() || !((letter.length() == 1 && Character.isLetter(letter.charAt(0))) || (letter.charAt(0) == '_' && letter.length() == 2 && Character.isLetter(letter.charAt(1))))) {
                                    System.out.println("Not a valid letter, select another letter:");
                                    letter = input.nextLine().toUpperCase();
                                }
                                // Use and place a blank piece, or throw error if don't have
                                if (letter.charAt(0) == '_') {
                                    if (players.get(turn).tempUse(' ')) {
                                        temporaryTiles[pos[0]][pos[1]].addPiece(letter.charAt(1), true);
                                        System.out.println("Blank placed, representing the letter " + letter.charAt(1));
                                        notPlaced = false;
                                    } else {
                                        System.out.println("You do not have that letter. Choose another letter:");
                                    }
                                // Use and place a piece, or throw error if don't have
                                } else {
                                    if (players.get(turn).tempUse(letter.charAt(0))) {
                                        temporaryTiles[pos[0]][pos[1]].addPiece(letter.charAt(0), false);
                                        System.out.println("Letter " + letter.charAt(0) + " placed");
                                        notPlaced = false;
                                    } else {
                                        System.out.println("You do not have that letter. Choose another letter:");
                                    }
                                }
                            }
                            break;
                        case 2:
                            break;
                        default:
                            System.out.println("Not a valid option.");
                            choice = 0;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Not a integer. Choose an option");
                }
            }
        }
    }

    /**
     * Checks if the placement is allowed
     * The following conditions must be met
     * One of the letters played is next to a previously played word
     * The height difference is a maximum of 1 between adjacent letters
     *
     * @return If placement is valid
     */
    @Override
    public boolean placementValidity() {
        if (isFirstTurn()) {
            return true;
        }

        boolean adjacentTile = false;
        // For every tile
        for (int j = 0; j < temporaryTiles.length; j++) {
            for (int i = 0; i < temporaryTiles[0].length; i++) {
                // If that tile was placed now
                if(hasTileChanged(new int[] {i, j})) {
                    // Check horizontally and vertically adjacent tiles for another tile, in which case adjacent tile
                    // is true, but if that adjacent tile does not follow staircase rule, return false
                    for (int m = -1; m < 2; m++) {
                        try {
                            if (currentTiles[i+m][j].getHeight() != 0) {
                                adjacentTile = true;
                                if (Math.abs(temporaryTiles[i+m][j].getHeight() - temporaryTiles[i][j].getHeight()) > 1) {
                                    return false;
                                }
                            }
                            if (currentTiles[i][j+m].getHeight() != 0) {
                                adjacentTile = true;
                                if (Math.abs(temporaryTiles[i][j+m].getHeight() - temporaryTiles[i][j].getHeight()) > 1) {
                                    return false;
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                    }
                }
            }
        }
        return adjacentTile;
    }

    @Override
    public void printTiles(Tile[][] board) {
        System.out.print("\n\n\nYour letters: ");
        for (int i = 0; i < players.get(turn).getSize(); i++) {
            if (i == players.get(turn).getSize() - 1) {
                System.out.print((char) players.get(turn).getLetters(i) + ": " +
                        Tile.getPoint((char) players.get(turn).getLetters(i)) + "\n\n");
            } else {
                System.out.print((char) players.get(turn).getLetters(i) + ": " +
                        Tile.getPoint((char) players.get(turn).getLetters(i)) + ", ");
            }
        }

        // I don't think this will ever be reached
        if (players.get(turn).pieces.isEmpty()) {
            System.out.println("\n\n");
        }

        // Print the board in a janky but visually understandable way
        for (int y = 0; y < board.length; y++) {
            for (int i = 0; i < board.length; i++) {
                System.out.print(" ----");
            }
            System.out.println();
            for (int x = 0; x < board[0].length; x++) {
                System.out.print("|");
                System.out.printf("%02d", board[x][y].getHeight());

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
            System.out.println("|");
        }
        for (int i = 0; i < board.length; i++) {
            System.out.print(" ----");
        }
        System.out.println();
    }
}


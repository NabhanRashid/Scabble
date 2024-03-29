import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class TwoDBoard extends Board {
    /**
     * Creates a board from given information by player, all other information will be filled by program
     *
     * @param boardSize      The size of the board, 0 is small, 1, is medium, 2 is large.
     * @param wordFileNames  Name of files from which to compile the word list. Do note the official word list must be explicitly included
     * @param playerNames    Name of players, the number of players is implicitly included
     * @param pieceBagCounts The count of each piece, (e.g : 1 A, 5 B, 100 C...), -1 will take the default given by the board. 27 values A-Z plus blank count
     */
    public TwoDBoard(int boardSize, ArrayList<String> wordFileNames, ArrayList<String> playerNames, int[] pieceBagCounts) {
        super(boardSize, wordFileNames, playerNames, pieceBagCounts);
    }

    /**
     * Constructs a board from a save file
     * @param fileName File from which save data is taken
     */
    public TwoDBoard(String fileName) {
        super(fileName);

        try {
            Scanner reader = new Scanner(new File(fileName));

            String[] firstLine = reader.nextLine().split(",");

            // First Line
            if (!firstLine[0].equals("2D")) {
                throw new InvalidParameterException("File is not of a 2D game");
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

            for (int yPos = 0; yPos < size; yPos++) {
                for (int xPos = 0; xPos < size; xPos++) {
                    currentTiles[xPos][yPos] = new Tile();
                    temporaryTiles[xPos][yPos] = new Tile();
                    String tile = firstLine[yPos * size + xPos + 2];

                    if (tile.charAt(0) == '-') {
                        continue;
                    }
                    if (tile.charAt(0) == ' ') {
                        currentTiles[xPos][yPos].addPiece(tile.charAt(1), true);
                        temporaryTiles[xPos][yPos].addPiece(tile.charAt(1), true);
                    } else {
                        currentTiles[xPos][yPos].addPiece(tile.charAt(0), false);
                        temporaryTiles[xPos][yPos].addPiece(tile.charAt(0), false);
                    }
                }
            }

            reader.close();

        } catch (IOException e) {
            throw new InvalidParameterException("File could not be opened");
        }
    }

    /**
     * Saves the game to a file in the 2D save type
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

            writer.write("2D," + size + ",");

            // Pseudo 2D array, simple entered row by row
            for (int yPos = 0; yPos < currentTiles.length; yPos++) {
                for (int xPos = 0; xPos < currentTiles.length; xPos++) {
                    if (currentTiles[xPos][yPos].getHeight() == 0) {
                        writer.write("-,");
                    } else if (currentTiles[xPos][yPos].isBlank()) {
                        writer.write(" " + currentTiles[xPos][yPos].getLetter() + ",");
                    } else {
                        writer.write(currentTiles[xPos][yPos].getLetter() + ",");
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
     * If there is a letter there, skips over the tile
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
                while (letter.isEmpty() || !((letter.length() == 1 && Character.isLetter(letter.charAt(0))) || (letter.charAt(0) == '_' && letter.length() == 2 && Character.isLetter(letter.charAt(1))))) {
                    System.out.println("Not a valid letter, select another letter:");
                    letter = input.nextLine().toUpperCase();
                }
                // Use blank piece or throw error if don't have
                if (letter.charAt(0) == '_') {
                    if (players.get(turn).tempUse(' ')) {
                        temporaryTiles[pos[0]][pos[1]].addPiece(letter.charAt(1), true);
                        System.out.println("Blank placed, representing the letter " + letter.charAt(1));
                        notPlaced = false;
                    } else {
                        System.out.println("You do not have that letter. Choose another letter:");
                    }
                // Use piece or throw error if don't have
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
            // Skip the letter
            System.out.println("The letter " + temporaryTiles[pos[0]][pos[1]].getLetter() + " is occupying this space already." +
                    "\nAs such, this placement will be skipped." +
                    "\nPress enter to continue;");
            input.nextLine();
        }
    }

    /**
     * Checks if the placement is allowed
     * This condition must be met: one of the letters played is next to a previously played word
     *
     * @return If placement is valid
     */
    @Override
    public boolean placementValidity() {
        if (isFirstTurn()) {
            return true;
        }

        // For each tile
        for (int j = 0; j < temporaryTiles.length; j++) {
            for (int i = 0; i < temporaryTiles[0].length; i++) {
                // If a piece was placed this turn, check if an adjacent tile exists, if yes, then return true
                // Otherwise continue to next tile.
                if(hasTileChanged(new int[] {i, j})) {
                    for (int m = -1; m < 2; m++) {
                        try {
                            if (currentTiles[i+m][j].getHeight() != 0) {
                                return true;
                            }
                            if (currentTiles[i][j+m].getHeight() != 0) {
                                return true;
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                    }
                }
            }
        }
        return false;
    }
}

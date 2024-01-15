import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
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
    public TwoDBoard(int boardSize, String[] wordFileNames, String[] playerNames, int[] pieceBagCounts) {
        super(boardSize, wordFileNames, playerNames, pieceBagCounts);
    }

    public TwoDBoard(String fileName) {

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
        if (temporaryTiles[pos[1]][pos[0]].getLetter() == 0) {
            System.out.println("What letter would you like to play?" +
                    "\nPut an underscore in front of the letter to use a blank");
            String letter;
            boolean notPlaced = true;
            while (notPlaced) {
                letter = input.nextLine();
                while (!((letter.length() == 1 && Character.isLetter(letter.charAt(0))) || (letter.charAt(0) == '_' && letter.length() == 2 && Character.isLetter(letter.charAt(0))))) {
                    System.out.println("Not a valid letter, select another letter:");
                    letter = input.nextLine();
                }
                if (letter.charAt(0) == '_') {
                    if (players.get(turn).tempUse(' ')) {
                        temporaryTiles[pos[1]][pos[0]].addPiece(letter.charAt(1), true);
                        System.out.println("Blank placed, representing the letter " + letter.charAt(1));
                        notPlaced = false;
                    } else {
                        System.out.println("You do not have that letter. Choose another letter:");
                    }
                } else {
                    if (players.get(turn).tempUse(letter.charAt(0))) {
                        temporaryTiles[pos[1]][pos[0]].addPiece(letter.charAt(0), true);
                        System.out.println("Letter " + letter.charAt(0) + " placed");
                        notPlaced = false;
                    } else {
                        System.out.println("You do not have that letter. Choose another letter:");
                    }
                }
            }
        } else {
            System.out.println("The letter " + temporaryTiles[pos[1]][pos[0]].getLetter() + "is occupying this space already." +
                    "\nAs such, this placement will be skipped.");
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
        for (int j = 0; j < currentTiles.length; j++) {
            for (int i = 0; i < currentTiles[0].length; i++) {
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
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
    public ThreeDBoard(int boardSize, String[] wordFileNames, String[] playerNames, int[] pieceBagCounts) {
        super(boardSize, wordFileNames, playerNames, pieceBagCounts);
    }

    /**
     * Constructs a board from a 3D saveFile
     * @param fileName Name of file to take save data from
     */
    public ThreeDBoard(String fileName) {
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

            for (int yPos = 0; yPos < size; yPos++) {
                for (int xPos = 0; xPos < size; xPos++) {
                    currentTiles[xPos][yPos] = new Tile();
                    String tile = firstLine[yPos * size + xPos + 2];
                    int height = Integer.parseInt(tile.substring(1, tile.length() - 1));

                    if (tile.charAt(0) == ' ') {

                        for (int i = 0; i < height; i++) {
                            currentTiles[xPos][yPos].addPiece(tile.charAt(tile.length() - 1), true, height);
                        }
                    } else {
                        currentTiles[xPos][yPos].addPiece(tile.charAt(0), false, height);
                    }
                }
            }

            // Second & Third-X Lines
            int numberOfPlayers = Integer.parseInt(reader.nextLine());

            for (int i = 0; i < numberOfPlayers; i++) {
                String[] currentPlayer = reader.nextLine().split(",");

                Player currentPlayerObject = new Player(currentPlayer[0]);

                int numberOfPieces = Integer.parseInt(currentPlayer[1]);

                for (int piece = 0; piece < numberOfPieces; piece++) {
                    currentPlayerObject.addPieces(currentPlayer[piece + 2].charAt(0), 1);
                }

                currentPlayerObject.addPoints(Integer.parseInt(currentPlayer[numberOfPieces + 2]));
                if (currentPlayer[numberOfPieces + 3].equals("f")) {
                    currentPlayerObject.outOfGame();
                }
            }

            // Third Last Line
            this.bag = new PieceBag();

            String[] bagLine = reader.nextLine().split(",");

            for (int i = 0; i < bagLine.length; i++) {
                bag.addPieces(bagLine[i].charAt(0), 1);
            }

            wordFileNames = reader.nextLine().split(",");

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
     * Plays a letter in the specified tile
     * If there are no letters there, asks for a letter to play
     * If there is a letter there, asks whether to skip over the tile or play above it
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
            int choice = 0;
            while (choice == 0) {
                try {
                    System.out.println("The letter " + temporaryTiles[pos[1]][pos[0]].getLetter() + "is occupying this space already." +
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
                        case 2:
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
        boolean adjacentTile = false;
        for (int j = 0; j < currentTiles.length; j++) {
            for (int i = 0; i < currentTiles[0].length; i++) {
                if(hasTileChanged(new int[] {i, j})) {
                    for (int m = -1; m < 2; m++) {
                        try {
                            if (currentTiles[i+m][j].getHeight() != 0) {
                                adjacentTile = true;
                                if (Math.abs(currentTiles[i+m][j].getHeight() - temporaryTiles[i][j].getHeight()) > 1) {
                                    return false;
                                }
                            }
                            if (currentTiles[i][j+m].getHeight() != 0) {
                                adjacentTile = true;
                                if (Math.abs(currentTiles[i][j+m].getHeight() - temporaryTiles[i][j].getHeight()) > 1) {
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
}


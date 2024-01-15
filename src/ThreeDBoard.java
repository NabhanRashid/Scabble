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

    @Override
    public void saveBoard(String fileName) {

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
                letter = input.nextLine();
                while (!((letter.length() == 1 && Character.isLetter(letter.charAt(0))) || (letter.charAt(0) == '_' && letter.length() == 2 && Character.isLetter(letter.charAt(0))))) {
                    System.out.println("Not a valid letter, select another letter:");
                    letter = input.nextLine();
                }
                if (letter.charAt(0) == '_') {
                    if (players.get(turn).tempUse(' ')) {
                        temporaryTiles[pos[0]][pos[1]].addPiece(letter.charAt(1), true);
                        System.out.println("Blank placed, representing the letter " + letter.charAt(1));
                        notPlaced = false;
                    } else {
                        System.out.println("You do not have that letter. Choose another letter:");
                    }
                } else {
                    if (players.get(turn).tempUse(letter.charAt(0))) {
                        temporaryTiles[pos[0]][pos[1]].addPiece(letter.charAt(0), true);
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
                    System.out.println("The letter " + temporaryTiles[pos[0]][pos[1]].getLetter() + "is occupying this space already." +
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
                                        temporaryTiles[pos[0]][pos[1]].addPiece(letter.charAt(1), true);
                                        System.out.println("Blank placed, representing the letter " + letter.charAt(1));
                                        notPlaced = false;
                                    } else {
                                        System.out.println("You do not have that letter. Choose another letter:");
                                    }
                                } else {
                                    if (players.get(turn).tempUse(letter.charAt(0))) {
                                        temporaryTiles[pos[0]][pos[1]].addPiece(letter.charAt(0), true);
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

    @Override
    public boolean placementValidity() {
        return false;
    }
}

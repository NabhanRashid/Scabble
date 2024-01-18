import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static final String FILE_PATH = "Game_Files/";
    public static final String WORD_FILE_PATH = "Game_Files/Word_List_Files/";
    public static final String SAVE_FILE_PATH = "Game_Files/Save_Files/";
    public static final Scanner INPUT = new Scanner(System.in);
    public static Board board;

    /**
     * Prompts user until they give a valid input within range or one of the characters
     * @param min Minimum number user can input (inclusive)
     * @param max Maximum number user can input (inclusive)
     * @param characters The characters allowed from the user
     * @return Input which is guaranteed to be valid
     */
    public static String validInput(int min, int max, String characters) {
        String input;
        int inputNumber = -1;

        boolean validInput;
        do {
            validInput = true;
            input = INPUT.nextLine().toLowerCase();

            for (char character : characters.toCharArray()) {
                if (input.charAt(0) == character) {
                    break;
                }
            }

            try {
                inputNumber = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("That was not a valid input please try again");
                validInput = false;
                continue;
            }
            if (inputNumber < min || inputNumber > max) {
                System.out.println("That was not one of the options");
                validInput = false;
            }

        } while (!validInput);

        return input;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Scabble! :D\n");
        System.out.println("Would you like to 1. start a new game or 2. load a previous game?");
        System.out.println("Please enter your choice as a number below");

        int choice = Integer.parseInt(validInput(1, 2, ""));

        if (choice == 1) {
            System.out.println("Starting a new game!\n");
            System.out.println("What type of game would you like?");
            System.out.println("Your possible game types are:");
            System.out.println(
                    """
                            \t1: Two Dimensional Scrabble (The original)
                            \t2: Three Dimensional Scrabble (The remastered)"""
            );
            int boardType = Integer.parseInt(validInput(1, 2, ""));

            System.out.println("What size would you like your board to be?");
            System.out.println("Your possible sizes are: ");
            System.out.println(
                    """
                            \t1: Small
                            \t2: Medium
                            \t3: Large"""
            );
            int boardSize = Integer.parseInt(validInput(1, 3, "")) - 1;

            System.out.println("Would you like to use the default wordList with over 250 thousand words?");
            ArrayList<String> wordFileNames = new ArrayList<>();

            char answer = validInput(1, -1, "yn").charAt(0);

            if (answer == 'y') {
                wordFileNames.add("Game_Files/Collins Scrabble Words (2019).txt");
            }

            System.out.println("Please add any additional files within the Word_List_Files directory" +
                    " from which to get words (Type \"No More\" to stop)");
            String fileName;
            do {
                System.out.print("Your file name " +
                        "(Remember to ensure they are in the specified format else they won't work properly): ");

                fileName = INPUT.nextLine();

                if (!(new File(SAVE_FILE_PATH + fileName)).exists()) {
                    System.out.println("Please add a file that exists within the Word_List_Files directory");
                    continue;
                }

                wordFileNames.add(SAVE_FILE_PATH + fileName);

            } while (!fileName.equalsIgnoreCase("no more"));
            // Removing the extraneous "no more"
            wordFileNames.removeLast();

            System.out.println("Please let each player add their name (Type \"No More\" to stop)");
            System.out.print("We recommend up to ");
            if (boardSize == 0) {
                System.out.print("3");
            } else if (boardSize == 1) {
                System.out.print("4");
            } else {
                System.out.print("6");
            }
            System.out.println(" players for this board size");

            String playerName;
            ArrayList<String> playerNames = new ArrayList<>();

            do {
                System.out.println("Enter your name (You are allowed any character but a comma)");
                playerName = INPUT.nextLine();

                if (playerName.contains(",")) {
                    System.out.println("That name had a comma in it, please try again");
                }
                playerNames.add(playerName);

            } while (!playerName.equalsIgnoreCase("no more"));
            // Removing extraneous "no more"
            playerNames.removeLast();

            System.out.println("Would you like the default number of pieces in the bag? (y/n)");
            int[] pieceBagCounts = new int[27];

            if (validInput(1, -1, "yn").charAt(0) == 'y') {
                for (int i = 0; i < 27; i++) {
                    pieceBagCounts[i] = -1;
                }
            } else {
                System.out.println("Please enter the amount of each piece you would like in the bag");

                int[] defaultCounts;
                switch (boardSize) {
                    case 0 -> defaultCounts = Board.defaultSmallPieceCounts;
                    case 1 -> defaultCounts = Board.defaultMediumPieceCounts;
                    case 2 -> defaultCounts = Board.defaultBigPieceCounts;
                    default -> throw new Error("This should never happen");
                }

                String numberOfPieces;
                for (int i = 0; i < 26; i++) {
                    System.out.println("How many " + ('A' + i) + " pieces would you like in the bag?");
                    System.out.print("The default number of pieces is " + defaultCounts[i]);
                    System.out.print(". If you'd like that enter \"d\": ");

                    numberOfPieces = validInput(0, 999999, "d");

                    if (numberOfPieces.charAt(0) == 'd') {
                        pieceBagCounts[i] = -1;
                    } else {
                        pieceBagCounts[i] = Integer.parseInt(numberOfPieces);
                    }
                }
                System.out.println("How many blank pieces would you like in the bag?");
                System.out.print("The default number of pieces is " + defaultCounts[26]);
                System.out.print(". If you'd like that enter \"d\": ");

                numberOfPieces = validInput(0, 999999, "d");

                if (numberOfPieces.charAt(0) == 'd') {
                    pieceBagCounts[26] = -1;
                } else {
                    pieceBagCounts[26] = Integer.parseInt(numberOfPieces);
                }
            }

            if (boardType == 1) {
                board = new TwoDBoard(boardSize, wordFileNames, playerNames, pieceBagCounts);
            } else if (boardType == 2) {
                board = new ThreeDBoard(boardSize, wordFileNames, playerNames, pieceBagCounts);
            }

        // Creation of a game from a saveFile
        } else if (choice == 2) {
            boolean givenValidFilePath;

            do {
                givenValidFilePath = true;
                System.out.println("Where is your file located? Please finish the file location below");
                System.out.print("Game_Files/");
                String fileName = FILE_PATH + INPUT.nextLine();

                try {
                    Scanner fileReader = new Scanner(new File(fileName));

                    // Get the first argument, should be "2D" or "3D"
                    String gameType = fileReader.nextLine().split(",")[0];

                    if (gameType.equals("2D")) {
                        board = new TwoDBoard(fileName);
                    } else if (gameType.equals("3D")) {
                        board = new ThreeDBoard(fileName);
                    } else {
                        System.out.println("The file found at " + fileName + "Did not lead to a game file");
                        System.out.println("Please try again");
                        givenValidFilePath = false;
                    }

                } catch (IOException e) {
                    System.out.println("The file path " + fileName + "Did not lead to a game file");
                    System.out.println("Please try again");
                    givenValidFilePath = false;
                }
            } while (!givenValidFilePath);
        }


    }

    /**
     * Shows a player their board and asks them what they would like to do
     * Runs whatever choice they make
     * For placeWord, fetches the starting position and direction
     * For saveBoard, fetches the file name to be saved under
     */
    public void playerTurn() {
        // TODO clear the board somehow
        System.out.println("It is now " + board.currentPlayer().getName() + "'s turn. Press enter to continue.");
        INPUT.nextLine();
        int input = 0;
        while (input < 1 || input > 4) {
            board.display();
            System.out.print("""
                    What would you like to do?
                    \t1. Place word
                    \t2. Exchange letters
                    \t3. Skip turn
                    \t4. Give up
                    \t5. Save game

                    Use numbers to choose an option.""");
            try {
                input = Integer.parseInt(INPUT.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please input the option as a number. ");
            }
            switch (input) {
                case 1:
                    board.display();
                    int[] pos = new int[2];
                    int direction;
                    System.out.println("Where would you like to start?");
                    System.out.println("Enter the column number (1 is the leftmost column)");
                    while (true) {
                        try {
                            pos[0] = Integer.parseInt(INPUT.nextLine());
                            if (pos[0] > board.boardSize() || pos[0] < 0) {
                                System.out.println("Not a valid column. Please enter a number within the range of the board");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Please input a valid number");
                        }
                    }
                    System.out.println("Enter the row number (1 is the top row)");
                    while (true) {
                        try {
                            pos[1] = Integer.parseInt(INPUT.nextLine());
                            if (pos[1] > board.boardSize() || pos[1] < 0) {
                                System.out.println("Not a valid row. Please enter a number within the range of the board");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Please input a valid number");
                        }
                    }
                    pos[0] -= 1;
                    pos[1] -= 1;
                    System.out.println("What direction would you like to play in?");
                    System.out.println("Up is 0, right is 1, down is 2, left is 3");
                    while (true) {
                        try {
                            direction = Integer.parseInt(INPUT.nextLine());
                            if (direction > 3 || direction < 0) {
                                System.out.println("Not a valid direction. Please enter a number between 1 and 4");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Please input a valid number");
                        }
                    }
                    if (board.placeWord(pos, direction)) {
                    } else {
                        input = 0;
                    }
                case 2:
                    board.exchangeLetters();
                    System.out.println("Letters successfully exchanged");
                case 3:
                    if (board.skipTurn()) {
                        System.out.println("Turn has been skipped");
                    } else {
                        System.out.println("No other players can play. Go again.");
                    }
                case 4:
                    board.giveUp();
                case 5:
                    System.out.println("What file name would you like to save your game to?");
                    String fileName = INPUT.nextLine();
                    try {
                        board.saveBoard(FILE_PATH + fileName);
                        System.out.println("File saved successfully!");
                    } catch (RuntimeException f) { //Needs error?
                        System.out.println("We done goofed");
                    }
                default:
                    System.out.println("Not a valid option. Please try again.");
            }
            if (board.currentPlayer().getSize() == 0 && board.bag.getSize() == 0) {
                board.giveUp();
            }
        }
    }
}
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.security.InvalidParameterException;
import java.util.Scanner;

public class Main {
    public static final String FILE_PATH = "Game_Files/";
    public static final Scanner INPUT = new Scanner(System.in);
    public static Board board;

    /**
     * Prompts user until they give a valid input within range
     * @param min Minimum number user can input (inclusive)
     * @param max Maximum number user can input (inclusive)
     * @return Valid number as int
     */
    public static int validIntegerInput(int min, int max) {
        String input;
        int inputNumber = -1;

        boolean validInput;
        do {
            validInput = true;
            input = INPUT.nextLine();
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

        return inputNumber;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Scabble! :D\n");
        System.out.println("Would you like to 1. start a new game or 2. load a previous game?");
        System.out.println("Please enter your choice as a number below");

        int choice = validIntegerInput(1, 2);

        if (choice == 1) {
            System.out.println("Starting a new game!\n");
            System.out.println("What type of game would you like?");
            System.out.println("Your possible game types are:");
            System.out.println(
                    """
                            \t1: Two Dimensional Scrabble (The original)
                            \t2: Three Dimensional Scrabble (The remastered)"""
            );
            int boardType = validIntegerInput(1, 2);

            System.out.println("What size would you like your board to be?");
            System.out.println("Your possible sizes are: ");
            System.out.println(
                    """
                            \t1: Small
                            \t2: Medium
                            \t3: Large"""
            );
            int boardSize = validIntegerInput(1, 3) - 1;

            // TODO The rest of the Board Construction variables

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
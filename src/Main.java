import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

public class Main {
    public static final String FILE_PATH = "Game_Files/";
    public static final Scanner INPUT = new Scanner(System.in);
    public static Board board;
    public static void main(String[] args) {

    }

    /**
     * Shows a player their board and asks them what they would like to do
     * Runs whatever choice they make
     * For placeWord, fetches the starting position and direction
     * For saveBoard, fetches the file name to be saved under
     */
    public void playerTurn() {
        // clear the board somehow
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
                    } catch (RuntimeException) { //Needs error?
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
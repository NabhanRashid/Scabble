import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

public class Main {
    public static final String FILE_PATH = "Game_Files/";
    public static final Scanner INPUT = new Scanner(System.in);
    public static Board board;
    public static void main(String[] args) {

    }

    public void playerTurn() {
        // clear the board somehow and print something to wait for enter
        board.display();
        int input = 0;
        while (input < 1 || input > 4) {
            System.out.print("""
                    What would you like to do?
                    \t1. Place word
                    \t2. Exchange letters
                    \t3. Skip turn
                    \t4. Save game

                    Use numbers to choose an option.""");
            try {
                input = Integer.parseInt(INPUT.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please input the option as a number. ");
            }
            switch (input) {
                case 1:

                    System.out.println();
                    board.placeWord();
                case 2:
                    board.exchangeLetters();

                case 3:
                    if (board.skipTurn()) {
                        System.out.println("Turn has been skipped");
                    }

                case 4:
                    System.out.println();
                    board.saveBoard();
                default:
                    System.out.println("Not a valid option. Please try again.");
            }
        }
    }
}
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static final String FILE_PATH = "Game_Files/";
    public static final Scanner INPUT = new Scanner(System.in);
    public static Board board;
    public static void main(String[] args) {
        board = new Board(0,
                new String[]{"Game_Files/Collins Scrabble Words (2019).txt", "Game_Files/Word_List_Files/TheWeirdWordFromFriends.txt"},
                new String[]{"Nabob", "Kevin", "Tai", "Derek"},
                new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1});

                System.out.println(board.wordList);

                System.out.println(Arrays.deepToString(board.currentTiles));
                System.out.println(board.players);
                System.out.println(board.bag);
    }

//    public void playerTurn() {
//        int input = 0;
//        while (input < 1 || input > 4) {
//            System.out.printf("What would you like to do?\n" +
//                    "\t1. Place word\n" +
//                    "\t2. Exchange letters\n" +
//                    "\t3. Skip turn\n" +
//                    "\t4. Save game\n\n" +
//                    "Use numbers to choose an option.");
//            try {
//                input = Integer.parseInt(INPUT.nextLine());
//            } catch (NumberFormatException e) {
//                System.out.println("Please input the option as a number. ");
//            }
//            switch (input) {
//                case 1:
//                    board.;
//                case 2:
//                    board.;
//                case 3:
//                    board.;
//                case 4:
//                    board.;
//                default:
//                    System.out.println("Not a valid option. Please try again.");
//            }
//        }
//    }
}
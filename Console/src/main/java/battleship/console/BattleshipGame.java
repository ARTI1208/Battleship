package battleship.console;

import battleship.Ocean;
import battleship.ships.EmptySea;
import battleship.ships.Ship;

import java.util.Scanner;

public class BattleshipGame {

    private static Scanner scanner = new Scanner(System.in);

    /**
     * Entry point for the program
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        do {
            Ocean ocean = new Ocean();
            System.out.println(ocean.toString());
            while (!ocean.isGameOver()) {
                int row = getLimitedInt(0, Ocean.HEIGHT, "Input row.");
                int column = getLimitedInt(0, Ocean.WIDTH, "Input column.");
                boolean shootResult = ocean.shootAt(row, column);
                System.out.println(ocean.toString());
                printShootResult(shootResult, ocean, row, column);
            }
            System.out.println("You won! Shots fired: " + ocean.getShotsFired() + ". Wanna play more? (y/n)");
        } while (scanner.nextLine().equals("y"));
    }

    /**
     * Prints the result of shoot invocation
     *
     * @param result the result of shoot invocation
     * @param ocean  ocean where thr shot was done
     * @param row    shot row
     * @param column shot column
     */
    private static void printShootResult(boolean result, Ocean ocean, int row, int column) {
        if (row < 0 || row >= Ocean.HEIGHT || column < 0 || column >= Ocean.WIDTH) {
            System.out.println("Invalid coordinates {" + row + ";" + column + "}!");
            return;
        }
        Ship hitShip = ocean.getShipArray()[row][column];
        if (result) {
            if (hitShip.isSunk())
                System.out.println("You've sank the " + hitShip.getShipType());
            else
                System.out.println("You've hit the ship at {" + row + ";" + column + "}!");
        } else {
            if (hitShip instanceof EmptySea)
                System.out.println("You've missed! Nothing at {" + row + ";" + column + "}");
            else if (hitShip.isSunk())
                System.out.println("You've already sank this ship!");
            else
                System.out.println("Something went wrong");
        }
    }

    /**
     * Parses string inputted by user to int
     *
     * @param errorCode number to return if there was a parse error
     * @return number inputted by user if there were not parse errors, errorCode otherwise
     */
    private static int getNextInt(int errorCode) {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            return errorCode;
        }
    }

    /**
     * Asks user for a number until it is in range [{@code inclusiveMin}; {@code exclusiveMax})
     *
     * @param inclusiveMin minimum value, inclusive
     * @param exclusiveMax maximum value, exclusive
     * @param prompt       a prompt to display for user
     * @return number in range [{@code inclusiveMin}; {@code exclusiveMax})
     */
    @SuppressWarnings("SameParameterValue")
    private static int getLimitedInt(int inclusiveMin, int exclusiveMax, String prompt) {
        int num;
        prompt += " Value must be in range [" + inclusiveMin + "-" + (exclusiveMax - 1) + "]";
        String promptInvalidInput = "Your input is invalid. " + prompt;
        System.out.println(prompt);
        while ((num = getNextInt(exclusiveMax)) < inclusiveMin || num >= exclusiveMax) {
            System.out.println(promptInvalidInput);
        }
        return num;
    }
}
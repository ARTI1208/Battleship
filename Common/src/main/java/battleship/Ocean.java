package battleship;

import battleship.ships.*;

import java.awt.*;
import java.util.Random;

@SuppressWarnings("unused")
public class Ocean extends AbstractOcean<Boolean> {

    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;

    private int shotsFired;
    private int hitCount;
    private int shipsSunk;
    private Ship[][] ships = new Ship[HEIGHT][WIDTH];

    public Ocean() {
        placeAllShipsRandomly();
    }

    public Ocean(boolean placeShips) {
        if (placeShips)
            placeAllShipsRandomly();
        else {
            for (int i = 0; i < HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    ships[i][j] = new EmptySea(i, j);
                }
            }
        }
    }

    /**
     * Randomly places ships on the ocean
     */
    public void placeAllShipsRandomly() {

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                ships[i][j] = new EmptySea(i, j);
            }
        }

        Random random = new Random();
        int shipCount;
        int row;
        int column;
        boolean horizontal;
        for (int i = 4; i > 0; --i) {
            shipCount = 4 - i + 1;
            for (int j = 0; j < shipCount; j++) {
                Ship ship = createShipOfSize(i);
                do {
                    row = random.nextInt(WIDTH);
                    column = random.nextInt(HEIGHT);
                    horizontal = random.nextBoolean();
                }
                while (!ship.checkAndPlace(row, column, horizontal, this));
            }
        }
    }

    /**
     * Creates ship based on number of cells ir occupies
     *
     * @param n cell count
     * @return new {@link Ship} of given cell count
     * @throws IllegalArgumentException if count is not in [1..4] range
     */
    private Ship createShipOfSize(int n) {
        switch (n) {
            case 1:
                return new Submarine();
            case 2:
                return new Destroyer();
            case 3:
                return new Cruiser();
            case 4:
                return new Battleship();
            default:
                throw new IllegalArgumentException("Size must be in [1..4]");
        }
    }

    /**
     * Checks whether the cell is occupied by ship
     *
     * @param row    cell row to check
     * @param column cell column to check
     * @return false if cell contains {@link EmptySea}, true otherwise
     */
    public boolean isOccupied(int row, int column) {
        return !(ships[row][column] instanceof EmptySea);
    }

    public boolean isHit(int row, int column) {
        return ships[row][column].isHit(row, column);
    }

    /**
     * Performs a shoot to the given cell. Increases {@link Ocean#shotsFired} count always,
     * {@link Ocean#hitCount} if cell contains a part of unsunk ship,
     * {@link Ocean#shipsSunk} if ship was sunken by the shot.
     * <p>
     * Also, if ship was sunken by the shot, reveals the adjacent {@link EmptySea} cells.
     *
     * @param row    cell row to shoot
     * @param column cell column to shoot
     * @return true if cell contains unsunk ship, false otherwise
     */
    @Override
    public Boolean shootAt(int row, int column) {
        ++shotsFired;
        boolean wasSunk = ships[row][column].isSunk();
        boolean result = ships[row][column].shootAt(row, column);
        if (!isOccupied(row, column)) {
            return false;
        }
        ++hitCount;
        if (!wasSunk && ships[row][column].isSunk()) {
            ++shipsSunk;
            Rectangle shipRect = ships[row][column].getShipRect();
            if (shipRect == null) {
                return result;
            }
            for (int i = shipRect.y; i < shipRect.y + shipRect.height; i++) {
                for (int j = shipRect.x; j < shipRect.x + shipRect.width; j++) {
                    if (ships[i][j] instanceof EmptySea) {
                        ships[i][j].shootAt(i, j);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets value of {@link Ocean#shotsFired}
     *
     * @return value of {@link Ocean#shotsFired}
     */
    public int getShotsFired() {
        return shotsFired;
    }

    /**
     * Gets value of {@link Ocean#hitCount}
     *
     * @return value of {@link Ocean#hitCount}
     */
    public int getHitCount() {
        return hitCount;
    }

    /**
     * Gets value of {@link Ocean#shipsSunk}
     *
     * @return value of {@link Ocean#shipsSunk}
     */
    public int getShipsSunk() {
        return shipsSunk;
    }

    /**
     * Checks whether the game has ended
     *
     * @return true if all ships are sunken, false otherwise
     */
    public boolean isGameOver() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (!(ships[i][j] instanceof EmptySea) && !ships[i][j].isSunk()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns matrix of {@link Ship} inheritors representing the Ocean map
     *
     * @return matrix of {@link Ship} inheritors
     */
    public Ship[][] getShipArray() {
        return ships;
    }

    private String getBattleInfo() {
        return "Info:\n \tShoots fired:\t" +
                shotsFired +
                "\n\tHits:\t" +
                hitCount +
                "\n\tShips sunk:\t" +
                shipsSunk +
                "\n\n";
    }

    /**
     * Gets the string representation of the current state of the battle,
     * containing info about fired shots, hit, sunken ships count and the map
     *
     * @return string representation of the current state of the battle
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getBattleInfo());
        builder.append("Map:\n");

        for (int j = -1; j < WIDTH; ++j) {
            if (j == -1) builder.append(" ");
            else builder.append(j);

            if (j == WIDTH - 1) builder.append("\n");
            else builder.append("|");
        }

        for (int i = 0; i < HEIGHT; ++i) {
            for (int j = -1; j < WIDTH; ++j) {
                if (j == -1) builder.append(i);
                else builder.append(ships[i][j].isHit(i, j) ? ships[i][j].toString() : "o");

                if (j == WIDTH - 1) builder.append("\n");
                else builder.append("|");
            }
        }

        return builder.toString();
    }
}

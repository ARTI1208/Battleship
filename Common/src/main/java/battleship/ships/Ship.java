package battleship.ships;

import battleship.Ocean;

import java.awt.*;
import java.io.Serializable;

@SuppressWarnings("unused")
public abstract class Ship implements Serializable {

    protected int bowRow = -1;
    protected int bowColumn = -1;
    protected int length;
    protected boolean[] hit;
    private boolean horizontal;

    /**
     * Gets value of {@link Ship#horizontal}
     *
     * @return true if ship is oriented horizontally, lase if vertically
     */
    public boolean isHorizontal() {
        return horizontal;
    }

    /**
     * Sets Ship orientation
     *
     * @param horizontal value for {@link Ship#horizontal}
     */
    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * Gets value of {@link Ship#length}
     *
     * @return value of {@link Ship#length}
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets value of {@link Ship#bowRow}
     *
     * @return value of {@link Ship#bowRow}
     */
    public int getBowRow() {
        return bowRow;
    }

    /**
     * Sets value for {@link Ship#bowRow}
     *
     * @param row value for {@link Ship#bowRow}
     */
    public void setBowRow(int row) {
        bowRow = row;
    }

    /**
     * Gets value of {@link Ship#bowColumn}
     *
     * @return value of {@link Ship#bowColumn}
     */
    public int getBowColumn() {
        return bowColumn;
    }

    /**
     * Sets value for {@link Ship#bowColumn}
     *
     * @param column value for {@link Ship#bowColumn}
     */
    public void setBowColumn(int column) {
        bowColumn = column;
    }

    /**
     * Gets string representation of ship type
     *
     * @return string representation of ship type
     */
    public String getShipType() {
        return getClass().getSimpleName().toLowerCase();
    }

    /**
     * Checks whether the ship can be placed on the Ocean
     *
     * @param row        int value for bow row of ship
     * @param column     int value for bow column of ship
     * @param horizontal orientation of ship, horizontal or vertical
     * @param ocean      Ocean to place ship in
     * @return true if ship can be placed with these params, false otherwise
     */
    public boolean okToPlaceShipAt(int row, int column, boolean horizontal, Ocean ocean) {
        return okToPlaceShipAt(row, column, horizontal, ocean, false);
    }

    public boolean okToPlaceShipAt(int row, int column, boolean horizontal, Ocean ocean, boolean removeSelf) {
        if (row < 0 || row >= Ocean.HEIGHT || column < 0 || column >= Ocean.WIDTH) {
            return false;
        }

        Rectangle shipWithFrame = getShipWithFrameRect(row, column, horizontal);
        if (shipWithFrame == null) {
            return false;
        }

        for (int i = shipWithFrame.y, endRow = shipWithFrame.y + shipWithFrame.height; i < endRow; ++i) {
            for (int j = shipWithFrame.x, endColumn = shipWithFrame.x + shipWithFrame.width; j < endColumn; ++j) {
                if (ocean.isOccupied(i, j) && (!removeSelf || ocean.getShipArray()[i][j] != this)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean canRotate(Ocean ocean) {
        removeFromOcean(ocean);
        boolean res = okToPlaceShipAt(bowRow, bowColumn, !horizontal, ocean);
        checkAndPlace(bowRow, bowColumn, horizontal, ocean);
        return res;
    }


    /**
     * Gets the rectangle, describing positions
     *
     * @param row        ship bow row
     * @param column     ship bow column
     * @param horizontal ship orientation
     * @return rectangle, describing positions
     */
    private Rectangle getShipWithFrameRect(int row, int column, boolean horizontal) {
        Rectangle rectangle = new Rectangle();
        rectangle.y = row > 0 ? row - 1 : 0;
        rectangle.x = column > 0 ? column - 1 : 0;
        if (horizontal) {
            if (column + length > Ocean.WIDTH) {
                return null;
            }
            rectangle.height = row > 0 && row < Ocean.HEIGHT - 1 ? 3 : 2;
            rectangle.width = column > 0 && column + length < Ocean.WIDTH ? length + 2 : length + 1;
        } else {
            if (row + length > Ocean.HEIGHT) {
                return null;
            }
            rectangle.width = column > 0 && column < Ocean.WIDTH - 1 ? 3 : 2;
            rectangle.height = row > 0 && row + length < Ocean.HEIGHT ? length + 2 : length + 1;
        }
        return rectangle;
    }

    /**
     * Gets the rectangle, describing actual positions
     *
     * @return rectangle, describing actual positions
     */
    public Rectangle getShipWithFrameRect() {
        return getShipWithFrameRect(bowRow, bowColumn, horizontal);
    }

    public Rectangle getShipRect() {
        Rectangle rectangle = new Rectangle();
        rectangle.y = bowRow;
        rectangle.x = bowColumn;

        if (horizontal) {
            rectangle.width = length;
            rectangle.height = 1;
        } else {
            rectangle.width = 1;
            rectangle.height = length;
        }

        return rectangle;
    }

    /**
     * Places ship at given bow row and column, with given orientation, in given ocean
     *
     * @param row        ship bow row
     * @param column     ship bow column
     * @param horizontal ship orientation
     * @param ocean      ocean to place ship in
     */
    private void placeShipAt(int row, int column, boolean horizontal, Ocean ocean) {

        bowRow = row;
        bowColumn = column;
        this.horizontal = horizontal;

        Ship[][] ships = ocean.getShipArray();
        if (horizontal) {
            int newColumn = column;
            for (int i = 0; i < length; ++i) {
                ships[row][newColumn++] = this;
            }
        } else {
            int newRow = row;
            for (int i = 0; i < length; ++i) {
                ships[newRow++][column] = this;
            }
        }
    }

    /**
     * Checks whether the ship can be placed at given bow row and column, with given orientation, in given ocean.
     * If so, places it
     *
     * @param row        ship bow row
     * @param column     ship bow column
     * @param horizontal ship orientation
     * @param ocean      ocean to place ship in
     * @return true if ship can be placed, false otherwise
     */
    public boolean checkAndPlace(int row, int column, boolean horizontal, Ocean ocean) {
        return checkAndPlace(row, column, horizontal, ocean, false);
    }

    public boolean checkAndPlace(int row, int column, boolean horizontal, Ocean ocean, boolean removeSelf) {
        if (okToPlaceShipAt(row, column, horizontal, ocean, removeSelf)) {
            placeShipAt(row, column, horizontal, ocean);
            return true;
        }
        return false;
    }

    public void removeFromOcean(Ocean ocean) {
        if (bowRow < 0 || bowColumn < 0)
            return;

        int from = horizontal ? bowColumn : bowRow;
        int otherDimension = horizontal ? bowRow : bowColumn;

        for (int i = from; i < from + length; i++) {
            if (horizontal) {
                ocean.getShipArray()[otherDimension][i] = new EmptySea(otherDimension, i);
            } else {
                ocean.getShipArray()[i][otherDimension] = new EmptySea(i, otherDimension);
            }
        }
    }

    /**
     * Tries to perform a shoot at given row and column
     *
     * @param row    row to shoot
     * @param column column to shoot
     * @return false if ship is already sunken or coordinates don't refer to this ship, true otherwise
     */
    public boolean shootAt(int row, int column) {
        if (isSunk() || !isShipPart(row, column)) {
            return false;
        }

        if (horizontal) {
            hit[column - bowColumn] = true;
        } else {
            hit[row - bowRow] = true;
        }

        return true;
    }

    /**
     * Checks whether the ship is sunken
     *
     * @return true if every part of the ship was hit, false otherwise
     */
    public boolean isSunk() {
        for (boolean isHit : hit) {
            if (!isHit) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the coordinates refer to this ship
     *
     * @param row    row coordinate to check
     * @param column column coordinate to check
     * @return true if there is a ship part at given coordinates, false otherwise
     */
    private boolean isShipPart(int row, int column) {
        if (horizontal) {
            return row == bowRow && column >= bowColumn && column < bowColumn + length;
        } else {
            return column == bowColumn && row >= bowRow && row < bowRow + length;
        }
    }

    /**
     * Checks whether the ship part at given coordinates has been hit
     *
     * @param row    row coordinate to check
     * @param column column coordinate to check
     * @return true if there is a ship part at given coordinates and it has been hit, false otherwise
     */
    public boolean isHit(int row, int column) {
        if (horizontal) {
            return isShipPart(row, column) && hit[column - bowColumn];
        } else {
            return isShipPart(row, column) && hit[row - bowRow];
        }
    }

    /**
     * Creates string representation of the state of the ship
     *
     * @return "x" if ship is sunken, "S" if not
     */
    @Override
    public String toString() {
        return isSunk() ? "x" : "S";
    }
}

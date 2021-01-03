package battleship.ships;

public class EmptySea extends Ship {

    public EmptySea(int row, int column) {
        bowRow = row;
        bowColumn = column;
        length = 1;
        hit = new boolean[length];
    }

    /**
     * Creates string representation of the type of the {@link Ship}
     *
     * @return always false to indicate that nothing was really hit
     */
    @Override
    public boolean shootAt(int row, int column) {
        boolean res = row == bowRow && column == bowColumn;
        if (res) {
            hit[0] = true;
        }
        return false;
    }

    /**
     * Creates string representation of the type of the {@link Ship} inheritor
     *
     * @return "@"
     */
    @Override
    public String getShipType() {
        return "@";
    }

    /**
     * Creates string representation of the state of the {@link EmptySea}
     *
     * @return "-"
     */
    @Override
    public String toString() {
        return "-";
    }
}

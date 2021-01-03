package battleship.ships;

public class Cruiser extends Ship {

    public Cruiser() {
        length = 3;
        hit = new boolean[length];
    }

    /**
     * Creates string representation of the type of the {@link Ship} inheritor
     *
     * @return "cruiser"
     */
    @Override
    public String getShipType() {
        return "cruiser";
    }
}

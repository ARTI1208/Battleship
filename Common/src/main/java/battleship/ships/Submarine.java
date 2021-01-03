package battleship.ships;

public class Submarine extends Ship {

    public Submarine() {
        length = 1;
        hit = new boolean[length];
    }

    /**
     * Creates string representation of the type of the {@link Ship} inheritor
     *
     * @return "submarine"
     */
    @Override
    public String getShipType() {
        return "submarine";
    }
}

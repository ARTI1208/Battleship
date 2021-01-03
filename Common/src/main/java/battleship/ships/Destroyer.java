package battleship.ships;

public class Destroyer extends Ship {

    public Destroyer() {
        length = 2;
        hit = new boolean[length];
    }

    /**
     * Creates string representation of the type of the {@link Ship} inheritor
     *
     * @return "destroyer"
     */
    @Override
    public String getShipType() {
        return "destroyer";
    }
}

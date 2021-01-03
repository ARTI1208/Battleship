package battleship.ships;

public class Battleship extends Ship {

    public Battleship() {
        length = 4;
        hit = new boolean[length];
    }

    /**
     * Creates string representation of the type of the {@link Ship} inheritor
     *
     * @return "battleship"
     */
    @Override
    public String getShipType() {
        return "battleship";
    }
}

package battleship;

abstract public class AbstractOcean<T> {

    public abstract T shootAt(int row, int column);

    public abstract void placeAllShipsRandomly();
}

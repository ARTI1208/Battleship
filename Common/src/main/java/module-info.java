module Common {
    requires java.desktop;

    opens battleship;
    exports battleship;
    exports battleship.ships;
}
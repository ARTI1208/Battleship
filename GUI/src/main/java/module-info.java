module GUI {
    requires Common;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;

    opens battleship.ui;
    exports battleship.ui;

//    opens battleship.ui.controls;
    exports battleship.ui.controls;

//    opens battleship.net;
    exports battleship.net;
}
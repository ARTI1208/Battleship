package battleship.ui;

import battleship.ui.controls.GameField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ShipPositioningController {

    @FXML
    public GameField shipPositioningField;

    @FXML
    public VBox shipsHolder;

    @FXML
    public Button playButton;

    @FXML
    public Button rotateShipButton;

    @FXML
    public Button allocateShipsButton;

    @FXML
    public Button allocateRandomlyButton;
}

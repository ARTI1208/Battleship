package battleship.ui;

import battleship.ui.controls.ConsoleView;
import battleship.ui.controls.GameField;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GameController {
    public ScrollPane scrollPane;
    public HBox fieldParent;
    public GameField myGameField;
    public GameField opponentGameField;
    public ConsoleView consoleView;
    public Label playerName;
    public Text opponentName;
    public VBox container;
}

package battleship.ui.controls;

import battleship.ships.Ship;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class VisualShip {

    public final Ship ship;
    public final Shape shipShape;

    private static final int SIZE = 30;
    private static final int STROKE_WIDTH = 2;

    public static final DataFormat BATTLESHIP_DATA_FORMAT = new DataFormat("battleship");

    public VisualShip(Ship ship) {
        this.ship = ship;

        Shape combined = new Rectangle(SIZE * ship.getLength(), SIZE);

        int f = SIZE;
        for (int i = 1; i < ship.getLength(); i++) {
            Line l = new Line(f, 0, f, SIZE);
            combined = Shape.subtract(combined, l);
            f += SIZE;
        }

        double divisor = ship.getLength() == 1 ? 1 : 2;
        combined.setStrokeWidth(STROKE_WIDTH / divisor);
        combined.setFill(Color.WHITE);
        combined.setStroke(Color.BLACK);

        shipShape = combined;


        shipShape.setOnDragDetected(event -> {
            Dragboard db = shipShape.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.put(BATTLESHIP_DATA_FORMAT, ship);
            db.setContent(content);

            event.consume();
        });

        shipShape.setOnDragDone(event -> {
            if (event.isAccepted() && shipShape.getParent() instanceof Pane)
                ((Pane) shipShape.getParent()).getChildren().remove(shipShape);
        });

    }
}

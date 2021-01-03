package battleship.ui.controls;

import battleship.Ocean;
import battleship.ships.EmptySea;
import battleship.ships.Ship;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static battleship.ui.controls.VisualShip.BATTLESHIP_DATA_FORMAT;

@SuppressWarnings("unused")
public class FieldCell extends Button {

    private static final Color CELL_BACKGROUND_DEFAULT = Color.WHITE;
    private static final Color CELL_BACKGROUND_MISS = Color.YELLOW;
    private static final Color CELL_BACKGROUND_HIT = Color.GREEN;
    private static final Color CELL_BACKGROUND_INVALID_MOVE = Color.RED;
    private static final long ANIMATION_TIME = 2L;
    public static final String BUTTON_TEXT_DEFAULT = "";
    public static final String BUTTON_TEXT_MISS = "-";
    public static final String BUTTON_TEXT_HIT = "X";
    public static final String BUTTON_TEXT_SUNK = "S";

    ControlBackgroundAnimation animation;

    private static PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    private BooleanProperty selected = new BooleanPropertyBase(false) {
        public void invalidated() {
            pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, get());
        }

        @Override public Object getBean() {
            return FieldCell.this;
        }

        @Override public String getName() {
            return "selected";
        }
    };

    private final IntegerProperty rowProperty = new IntegerPropertyBase() {

        @Override
        public Object getBean() {
            return FieldCell.this;
        }

        @Override
        public String getName() {
            return "row";
        }
    };

    private final IntegerProperty columnProperty = new IntegerPropertyBase() {

        @Override
        public Object getBean() {
            return FieldCell.this;
        }

        @Override
        public String getName() {
            return "row";
        }
    };

    private ObjectProperty<GameField> gameFieldProperty = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return FieldCell.this;
        }

        @Override
        public String getName() {
            return "ocean";
        }
    };

    public void setGameField(GameField gameField) {
        gameFieldProperty.set(gameField);
    }

    public GameField getGameField() {
        return gameFieldProperty.get();
    }

    private Ocean getOcean() {
        return getGameField().getOcean();
    }

    public void setRow(int row) {
        rowProperty.set(row);
    }

    public int getRow() {
        return rowProperty.get();
    }

    public void setColumn(int column) {
        columnProperty.set(column);
    }

    public int getColumn() {
        return columnProperty.get();
    }

    /**
     * Adds or removes selected pseudo class for styling
     *
     * @param selected whether this cell should be marked as selected
     */
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    /**
     * @return whether this cell is marked as selected
     */
    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public enum ShootResult {
        HIT,
        SUNK,
        MISS,
        INVALID;


        public static ShootResult fromInt(int i) {
            if (i < 0 || i >= values().length)
                return INVALID;

            return values()[i];
        }
    }

    public FieldCell() {
        super();
        init();
    }

    public FieldCell(String text) {
        super(text);
        init();
    }

    public FieldCell(String text, Node graphic) {
        super(text, graphic);
        init();
    }

    public Ship getShip() {
        return getGameField().getOcean().getShipArray()[getRow()][getColumn()];
    }

    private void init() {
        getStyleClass().add("field-cell");
        setText(BUTTON_TEXT_DEFAULT);

        setOnDragOver(event -> {
            if (event.getDragboard().hasContent(BATTLESHIP_DATA_FORMAT)) {
                Ship s = (Ship) event.getDragboard().getContent(BATTLESHIP_DATA_FORMAT);

                if (s.okToPlaceShipAt(getRow(), getColumn(), true, getOcean())) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        });

        setOnDragDetected(event -> {
            Ship s = getOcean().getShipArray()[getRow()][getColumn()];
            if (s instanceof EmptySea)
                return;

            getGameField().removeShip(s);

            Dragboard db = startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.put(BATTLESHIP_DATA_FORMAT, s);
            db.setContent(content);

            event.consume();
        });

        setOnDragDone(event -> {
            Ship s = (Ship) event.getDragboard().getContent(BATTLESHIP_DATA_FORMAT);
            if (!event.isAccepted()) {
                getGameField().placeShip(getRow(), getColumn(),true, s);
            }
        });
    }

    /**
     * Marks this cell as being hit
     */
    public void markHit() {
        getStyleClass().remove("field-cell-no-hit");
        getStyleClass().add("field-cell-hit");
    }

    /**
     * Unmarks this cell as being hit
     */
    public void unmarkHit() {
        getStyleClass().add("field-cell-no-hit");
        getStyleClass().remove("field-cell-hit");
        setText(BUTTON_TEXT_DEFAULT);
    }

    /**
     * Applies graphic change of cell after shoot was done
     *
     * @param shootResult result of th shoot that was done
     */
    public void applyShootResult(ShootResult shootResult) {
        Runnable r;
        if (shootResult != ShootResult.INVALID) {
            getStyleClass().remove("field-cell-hit");
            getStyleClass().remove("field-cell-no-hit");
            r = this::markHit;
        } else
            r = () -> {};


        switch (shootResult) {
            case HIT:
                setText(BUTTON_TEXT_HIT);
                animate(Duration.seconds(ANIMATION_TIME), CELL_BACKGROUND_HIT, r);
                break;
            case SUNK:
                setText(BUTTON_TEXT_SUNK);
                animate(Duration.seconds(ANIMATION_TIME), CELL_BACKGROUND_HIT, r);
                break;
            case MISS:
                setText(BUTTON_TEXT_MISS);
                animate(Duration.seconds(ANIMATION_TIME), CELL_BACKGROUND_MISS, r);
                break;
            case INVALID:
                animate(Duration.seconds(ANIMATION_TIME), CELL_BACKGROUND_INVALID_MOVE, r);
                break;
        }
    }

    /**
     * Animates the cell on shot
     *
     * @param duration animation duration
     * @param color color of click splash
     * @param r runnable to run after animation ended
     */
    private void animate(Duration duration, Color color, Runnable r) {
        if (animation != null) {
            animation.stop();
            animation.getOnFinished().run();
        }

        animation = new ControlBackgroundAnimation(duration, this, color, CELL_BACKGROUND_DEFAULT);
        animation.setOnFinished(r);
        animation.play();
    }
}

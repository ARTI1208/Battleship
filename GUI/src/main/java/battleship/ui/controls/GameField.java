package battleship.ui.controls;

import battleship.Ocean;
import battleship.ships.EmptySea;
import battleship.ships.Ship;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.awt.*;

import static battleship.ui.controls.VisualShip.BATTLESHIP_DATA_FORMAT;

public class GameField extends GridPane {

    private static final double CELL_MIN_HEIGHT = 40;
    private static final double CELL_MIN_WIDTH = 40;

    public enum FieldMode {
        SETUP,
        MY_FIELD,
        OPPONENT_FIELD
    }

    private FieldMode mode = FieldMode.OPPONENT_FIELD;

    public FieldCell selectedCell;

    public ObjectProperty<FieldCell> selectedCellProperty = new ObjectPropertyBase<FieldCell>() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "selectedCell";
        }
    };

    private Ocean ocean = new Ocean(false);

//    public ObjectProperty<Ship> selectedShipProperty

    public GameField() {
        for (int i = 0; i < Ocean.HEIGHT + 1; i++) {
            for (int j = 0; j < Ocean.WIDTH + 1; j++) {

                Control child;

                if (i == 0 && j == 0)
                    child = new javafx.scene.control.Label();
                else if (i == 0)
                    child = new javafx.scene.control.Label(Integer.toString(j - 1));
                else if (j == 0)
                    child = new Label(Integer.toString(i - 1));
                else
                    child = createFieldButton(i - 1, j - 1);

                add(child, i, j);


                GridPane.setHalignment(child, HPos.CENTER);
                GridPane.setValignment(child, VPos.CENTER);
            }

            ColumnConstraints columnSize = new ColumnConstraints();
            columnSize.setHgrow(Priority.ALWAYS);
            columnSize.setMinWidth(CELL_MIN_WIDTH);
            getColumnConstraints().add(columnSize);
        }

        for (int i = 0; i < Ocean.WIDTH + 1; i++) {
            RowConstraints rowSize = new RowConstraints();
            rowSize.setVgrow(Priority.ALWAYS);
            rowSize.setMinHeight(CELL_MIN_HEIGHT);
            getRowConstraints().add(rowSize);
        }

        getStylesheets().add("styles.scss");
        setOnDragDropped(event -> {
            if (event.getTarget() instanceof FieldCell) {
                FieldCell cell = (FieldCell) event.getTarget();
                Ship s = (Ship) event.getDragboard().getContent(BATTLESHIP_DATA_FORMAT);

                placeShip(cell.getRow(), cell.getColumn(), true, s);

                event.consume();
            }
        });

    }

    public FieldMode getMode() {
        return mode;
    }

    public void setMode(FieldMode mode) {
        this.mode = mode;
        if (mode == FieldMode.MY_FIELD) {
            updateStyleForMyField();
        }
    }

    public Ocean getOcean() {
        return ocean;
    }

    public void setOcean(Ocean ocean) {
        this.ocean = ocean;

        if (mode == FieldMode.MY_FIELD)
            updateStyleForMyField();
    }

    private void updateStyleForMyField() {
        for (int i = 0; i < Ocean.HEIGHT; i++) {
            for (int j = 0; j < Ocean.WIDTH; j++) {
                FieldCell cell = getFieldButton(j, i);

                cell.getStyleClass().remove("field-cell-with-ship");
                if (!(ocean.getShipArray()[i][j] instanceof EmptySea)) {
                    cell.getStyleClass().add("field-cell-with-ship");
                }
            }
        }
    }

    public void placeAllShipsRandomly() {
        ocean.placeAllShipsRandomly();
        for (int i = 0; i < Ocean.HEIGHT; i++) {
            for (int j = 0; j < Ocean.WIDTH; j++) {
                FieldCell cell = getFieldButton(j, i);

                cell.getStyleClass().remove("field-cell-with-ship");
                if (!(ocean.getShipArray()[i][j] instanceof EmptySea)) {
                    cell.getStyleClass().add("field-cell-with-ship");
                }
            }
        }
    }

    public Ship getSelectedShip() {
        if (selectedCell == null) {
            return null;
        }

        return selectedCell.getShip();
    }

    public boolean placeShip(int row, int column, boolean horizontal, Ship s) {
        return placeShip(row, column, horizontal, s, false);
    }

    public boolean placeShip(int row, int column, boolean horizontal, Ship s, boolean removeSelf) {
        if (s.okToPlaceShipAt(row, column, horizontal, ocean, removeSelf)) {

            if (s.getBowRow() >= 0 && s.getBowColumn() >= 0)
                removeShip(s);

            s.checkAndPlace(row, column, horizontal, ocean, removeSelf);

            Rectangle shipRect = s.getShipRect();
            for (int i = shipRect.y; i < shipRect.y + shipRect.height; i++) {
                for (int j = shipRect.x; j < shipRect.x + shipRect.width; j++) {
                    getFieldButton(j, i).getStyleClass().add("field-cell-with-ship");
                }
            }

            return true;
        }

        return false;
    }

    public void removeShip(Ship s) {
        Rectangle shipRect = s.getShipRect();

        for (int i = shipRect.y; i < shipRect.y + shipRect.height; i++) {
            for (int j = shipRect.x; j < shipRect.x + shipRect.width; j++) {
                getFieldButton(j, i).getStyleClass().remove("field-cell-with-ship");
            }
        }

        s.removeFromOcean(ocean);
    }

    /**
     * Gets button corresponding to cell in the ocean
     *
     * @param column column index in the ocean
     * @param row    roq index in the ocean
     * @return button corresponding to cell in the ocean
     */
    public FieldCell getFieldButton(int column, int row) {
        return ((FieldCell) getChildren().get((column + 1) * (Ocean.HEIGHT + 1) + (row + 1)));
    }

    /**
     * Selects cell at given coordinates and deselects previously selected one
     *
     * @param column index of cell column in ocean
     * @param row    index of cell row in ocean
     */
    private void setSelectedCell(int column, int row) {
        FieldCell cell = getFieldButton(column, row);
        if (cell == selectedCell)
            return;

        if (selectedCell != null) {
            selectedCell.setSelected(false);
        }

        cell.setSelected(true);
        selectedCell = cell;

        selectedCellProperty.set(cell);

//        rowSpinner.getValueFactory().setValue(row);
//        columnSpinner.getValueFactory().setValue(column);
    }

    public void validateShipSunk(int row, int column) {
        FieldCell cell = getFieldButton(column, row);

        if (!cell.getText().equals(FieldCell.BUTTON_TEXT_SUNK)) {
            return;
        }

        int directionX = 0;
        int directionY = 0;

        if (row > 0 && getFieldButton(column, row - 1).getText().equals(FieldCell.BUTTON_TEXT_HIT)) {
            directionY = -1;
        }

        if (row < Ocean.HEIGHT - 1 && getFieldButton(column, row + 1).getText().equals(FieldCell.BUTTON_TEXT_HIT)) {
            directionY = 1;
        }

        if (column > 0 && getFieldButton(column - 1, row).getText().equals(FieldCell.BUTTON_TEXT_HIT)) {
            directionX = -1;
        }

        if (column < Ocean.WIDTH - 1 && getFieldButton(column + 1, row).getText().equals(FieldCell.BUTTON_TEXT_HIT)) {
            directionX = 1;
        }

        if (directionX == 0 && directionY == 0) {
            return;
        }

        do {
            row += directionY;
            column += directionX;

            FieldCell targetCell = getFieldButton(column, row);
            if (targetCell.getText().equals(FieldCell.BUTTON_TEXT_HIT)) {
                targetCell.applyShootResult(FieldCell.ShootResult.SUNK);
            } else {
                break;
            }


        } while (row >= 0 && row < Ocean.HEIGHT && column >= 0 && column < Ocean.WIDTH);

    }

    public boolean shootAt(int row, int column) {
        FieldCell button = getFieldButton(column, row);

        if (ocean.isGameOver()) {
            button.applyShootResult(FieldCell.ShootResult.INVALID);
            return false;
        }

        Ship associatedShip = ocean.getShipArray()[row][column];

        if (!associatedShip.isHit(row, column)) {
            boolean result = ocean.shootAt(row, column);
            if (!result) { //miss
                button.applyShootResult(FieldCell.ShootResult.MISS);
                return false;
            } else if (associatedShip.isSunk()) { //ship sunk
                Rectangle r = associatedShip.getShipRect();
                for (int i = r.y; i < r.y + r.height; i++) {
                    for (int j = r.x; j < r.x + r.width; j++) {
                        Ship shipAtPosition = ocean.getShipArray()[i][j];
                        FieldCell buttonAtPosition = getFieldButton(j, i);
                        buttonAtPosition.applyShootResult(shipAtPosition instanceof EmptySea ? FieldCell.ShootResult.MISS : FieldCell.ShootResult.SUNK);
                    }
                }
                return true;
            } else { // ship hit
                button.applyShootResult(FieldCell.ShootResult.HIT);
                return true;
            }
        } else {
            button.applyShootResult(FieldCell.ShootResult.INVALID);
            return false;
        }
    }

    /**
     * Creates and setups new cell of a field
     *
     * @param column index of cell column
     * @param row    index of cell row
     * @return newly created cell
     */
    private Button createFieldButton(int column, int row) {
        FieldCell button = new FieldCell();
        button.setRow(row);
        button.setColumn(column);
        button.setGameField(this);


        button.focusedProperty().addListener((observable, oldValue, newValue) -> setSelectedCell(column, row));


        button.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (mode == FieldMode.OPPONENT_FIELD)
                button.requestFocus();
        });

        button.setOnMouseMoved(ev -> {
            if (mode == FieldMode.OPPONENT_FIELD)
                button.requestFocus();
        });


        button.setMinSize(CELL_MIN_WIDTH, CELL_MIN_HEIGHT);

        try {
            button.prefHeightProperty().bind(heightProperty().divide(10));
            button.prefWidthProperty().bind(widthProperty().divide(10));
        } catch (Exception e) {
            e.printStackTrace();
        }

        button.setOnAction(event -> {
            if (ocean == null || mode != FieldMode.OPPONENT_FIELD)
                return;

            if (button.getText().equals(FieldCell.BUTTON_TEXT_DEFAULT)) {
                shootAt(row, column);
            } else {
                button.applyShootResult(FieldCell.ShootResult.INVALID);
            }

        });
        return button;
    }
}

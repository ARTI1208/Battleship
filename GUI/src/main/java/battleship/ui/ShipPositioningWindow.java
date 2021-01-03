package battleship.ui;

import battleship.Ocean;
import battleship.net.BattleshipSocketMediator;
import battleship.ships.*;
import battleship.ui.controls.FieldCell;
import battleship.ui.controls.GameField;
import battleship.ui.controls.VisualShip;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static battleship.ui.controls.VisualShip.BATTLESHIP_DATA_FORMAT;

public class ShipPositioningWindow extends Stage {

    private BattleshipSocketMediator socketMediator;

    private ShipPositioningController controller;

    public ShipPositioningWindow(BattleshipSocketMediator socketMediator) {
        this.socketMediator = socketMediator;

        FXMLLoader loader = new FXMLLoader(ShipPositioningWindow.class.getClassLoader().getResource(("positioning_window.fxml")));

        Parent p;
        try {
            p = loader.load();
        } catch (Exception e) {
            p = new Label("Loading error");
        }

        Scene primaryScene = new Scene(p, 720, 680);

        controller = loader.getController();

//        c.setScene(primaryScene);

        primaryScene.getStylesheets().add("styles.scss");

        setMinHeight(680);
        setMinWidth(720);
        setScene(primaryScene);
        setTitle("BattleshipGame: Positioning");
        setup();
    }

    private Map<Class<? extends Ship>, Shape> shipToShape = new HashMap<>();

    private void addShipsToSelector() {
        for (int i = 4; i > 0; i--) {
            int shipCount = 4 - i + 1;
            for (int j = 0; j < shipCount; j++) {
                Ship s = createShipOfSize(i);

                if (j == 0)
                    controller.shipsHolder.getChildren().add(new Label(s.getClass().getSimpleName()));

                VisualShip vs = new VisualShip(s);
                shipToShape.put(s.getClass(), vs.shipShape);

                controller.shipsHolder.getChildren().add(vs.shipShape);
            }
        }
    }

    private void setup() {
        controller.playButton.setDisable(true);
        controller.allocateRandomlyButton.setDisable(true);

        controller.shipsHolder.setMinWidth(200);

        controller.shipPositioningField.prefWidthProperty().bind(widthProperty().subtract(controller.shipsHolder.widthProperty()));

        controller.playButton.setOnAction(event -> {
            controller.shipPositioningField.setDisable(true);
            controller.allocateRandomlyButton.setDisable(true);
            controller.rotateShipButton.setDisable(true);
            controller.playButton.setDisable(true);


            Dialog<Void> dialog = new Dialog<>();
            DialogPane pane = new DialogPane();
            pane.setContent(new Label("Waiting for opponent.."));

            pane.getButtonTypes().add(ButtonType.CLOSE);

            dialog.setDialogPane(pane);

            dialog.setOnCloseRequest(event1 -> {
                socketMediator.sendStopSignal();
            });

            dialog.show();



            new Thread(() -> {
                boolean res = socketMediator.waitForGameStart(controller.shipPositioningField.getOcean());

                Platform.runLater(() -> {
                    dialog.close();

                    if (res) {
                        close();
                        new GameWindow(socketMediator).show();
                    } else {
                        controller.shipPositioningField.setDisable(false);
                        controller.playButton.setDisable(false);
                        controller.allocateRandomlyButton.setDisable(false);
                    }
                });
            }).start();
        });

        controller.allocateShipsButton.setOnAction(event -> {
            addShipsToSelector();
            controller.allocateShipsButton.setDisable(true);
            controller.allocateRandomlyButton.setDisable(false);
        });

        controller.allocateRandomlyButton.setOnAction(event -> {
            controller.shipPositioningField.placeAllShipsRandomly();
            controller.shipsHolder.getChildren().removeIf(node -> node instanceof Shape);
        });

        controller.shipsHolder.getChildren().addListener((ListChangeListener<Node>) c -> {
            controller.playButton.setDisable(!controller.shipsHolder.getChildren().filtered(node -> node instanceof Shape).isEmpty());
        });


        controller.shipsHolder.setOnDragOver(event -> {
            if (event.getDragboard().hasContent(BATTLESHIP_DATA_FORMAT)) {
                Ship s = (Ship) event.getDragboard().getContent(BATTLESHIP_DATA_FORMAT);

                if (s.getBowRow() >= 0 && s.getBowColumn() >= 0) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        });

        controller.shipsHolder.setOnDragDropped(event -> {
            Ship s = (Ship) event.getDragboard().getContent(BATTLESHIP_DATA_FORMAT);

            controller.shipsHolder.getChildren().add(findPlaceToPush(s, controller.shipsHolder.getChildren()), new VisualShip(s).shipShape);
        });

        controller.shipPositioningField.selectedCellProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getShip() == null)
                return;

            Ship s = newValue.getShip();

            controller.rotateShipButton.setDisable(s instanceof EmptySea || !s.canRotate(controller.shipPositioningField.getOcean()));
        });

        controller.rotateShipButton.setOnAction(event -> {
            rotateSelectedShip();
        });

        controller.shipPositioningField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (controller.shipPositioningField.getMode() != GameField.FieldMode.SETUP)
                return;

            Ship s = controller.shipPositioningField.getSelectedShip();

            if (s == null)
                return;

            int moveX = 0;
            int moveY = 0;

            switch (event.getCode()) {
                case R:
                    rotateSelectedShip();
                    return;
                case D:
                    if (s instanceof EmptySea)
                        return;

                    controller.shipPositioningField.removeShip(s);
                    controller.shipsHolder.getChildren().add(findPlaceToPush(s, controller.shipsHolder.getChildren()), new VisualShip(s).shipShape);
                    return;
                case UP:
                    moveY = -1;
                    break;
                case DOWN:
                    moveY = 1;
                    break;
                case LEFT:
                    moveX = -1;
                    break;
                case RIGHT:
                    moveX = 1;
                    break;
            }


            if (moveX == 0 && moveY == 0)
                return;

            int selectedCellRow = controller.shipPositioningField.selectedCell.getRow();
            int selectedCellColumn = controller.shipPositioningField.selectedCell.getColumn();

            boolean res = true;

            if (event.isShiftDown()) {
                res = controller.shipPositioningField.placeShip(
                        s.getBowRow() + moveY,
                        s.getBowColumn() + moveX,
                        s.isHorizontal(),
                        s,
                        true
                );
            } else {
                int newRow = selectedCellRow + moveY;
                int newColumn = selectedCellColumn + moveX;

                res = newRow >= 0 && newRow < Ocean.HEIGHT && newColumn >= 0 && newColumn < Ocean.WIDTH;
            }

            if (!res) {
                moveX = 0;
                moveY = 0;
            }

            controller.shipPositioningField.getFieldButton(selectedCellColumn + moveX, selectedCellRow + moveY).requestFocus();
            event.consume();

        });

    }

    private void rotateSelectedShip() {
        Ship s = controller.shipPositioningField.getSelectedShip();

        if (s == null)
            return;

        if (s.canRotate(controller.shipPositioningField.getOcean())) {
            controller.shipPositioningField.removeShip(s);
            controller.shipPositioningField.placeShip(s.getBowRow(), s.getBowColumn(), !s.isHorizontal(), s);
        }
        controller.shipPositioningField.getFieldButton(s.getBowColumn(), s.getBowRow()).requestFocus();
    }

    private int findPlaceToPush(Ship s, List<Node> children) {
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (child instanceof Label && ((Label) child).getText().equals(s.getClass().getSimpleName())) {
                return i + 1;
            }
        }

        return children.size();
    }

    private Ship createShipOfSize(int n) {
        switch (n) {
            case 1:
                return new Submarine();
            case 2:
                return new Destroyer();
            case 3:
                return new Cruiser();
            case 4:
                return new Battleship();
            default:
                throw new IllegalArgumentException("Size must be in [1..4], was " + n);
        }
    }
}

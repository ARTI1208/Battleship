package battleship.ui;

import battleship.net.BattleshipSocketMediator;
import battleship.net.GameResult;
import battleship.net.NetOcean;
import battleship.net.GameEventListener;
import battleship.ui.controls.FieldCell;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GameWindow extends Stage {

    private final BattleshipSocketMediator socketMediator;

    private GameController controller;

    public GameWindow(BattleshipSocketMediator socketMediator) {
        this.socketMediator = socketMediator;

        FXMLLoader loader = new FXMLLoader(ShipPositioningWindow.class.getClassLoader().getResource(("game_window.fxml")));

        Parent p;
        try {
            p = loader.load();
        } catch (Exception e) {
            p = new Label("Loading error");
        }

        Scene primaryScene = new Scene(p, 900, 680);

        controller = loader.getController();

//        c.setScene(primaryScene);

        primaryScene.getStylesheets().add("styles.scss");

        setMinHeight(680);
        setMinWidth(900);
        setScene(primaryScene);
        setTitle("BattleshipGame: Game");
        setup();
    }

    private void setup() {
        controller.myGameField.setOcean(socketMediator.getPlayerOcean());
        controller.opponentGameField.setOcean(new NetOcean(socketMediator));
        controller.playerName.setText(socketMediator.appType.name());

        controller.container.prefWidthProperty().bind(widthProperty());
        controller.container.minHeightProperty().bind(heightProperty().subtract(controller.consoleView.heightProperty()));

        controller.myGameField.prefHeightProperty().bind(controller.fieldParent.heightProperty().subtract(180));
        controller.opponentGameField.prefHeightProperty().bind(controller.fieldParent.heightProperty().subtract(180));

        controller.scrollPane.prefHeightProperty().bind(heightProperty().subtract(controller.consoleView.heightProperty()));

        if (socketMediator.appType != socketMediator.getTurnOwner()) {
            controller.opponentGameField.setDisable(true);
        }

        setOnCloseRequest(event -> {
            socketMediator.sendStopGameRequest();
            event.consume();
        });

        controller.playerName.setText(socketMediator.name + " (Me)");
        controller.opponentName.setText(socketMediator.opponentName);

        socketMediator.setGameEventListener(new GameEventListener() {

            final Dialog<Void> stopRequestDialog = new Dialog<>();

            {
                stopRequestDialog.initOwner(GameWindow.this);
            }

            @Override
            public void onMyShotDone(int row, int column, FieldCell.ShootResult result) {
                FieldCell cell = controller.opponentGameField.getFieldButton(column, row);

                if (result == FieldCell.ShootResult.MISS) {
                    controller.opponentGameField.setDisable(true);
                }

                cell.applyShootResult(result);

                if (result == FieldCell.ShootResult.SUNK) {
                    controller.opponentGameField.validateShipSunk(row, column);
                }

                if (result != FieldCell.ShootResult.INVALID)
                    controller.consoleView.getOutputStream().println(socketMediator.name + " (Me): shot at " + row + ":" + column + "=" + cell.getText());
            }

            @Override
            public void onOpponentShot(int row, int column, FieldCell.ShootResult result) {
                FieldCell cell = controller.myGameField.getFieldButton(column, row);

                if (result == FieldCell.ShootResult.MISS) {
                    controller.opponentGameField.setDisable(false);
                }

                cell.applyShootResult(result);

                if (result == FieldCell.ShootResult.SUNK) {
                    controller.myGameField.validateShipSunk(row, column);
                }

                if (result != FieldCell.ShootResult.INVALID)
                    controller.consoleView.getOutputStream().println(socketMediator.opponentName + ": shot at " + row + ":" + column + "=" + cell.getText());
            }

            @Override
            public void onStopRequest(String initiator) {
                DialogPane pane = new DialogPane();

                Text content = new Text(initiator + " would like to stop the game. By clicking ok you accept to end game and close the app. Proceed?");

                pane.setPadding(new Insets(10));

                pane.setContent(content);

                stopRequestDialog.setResultConverter(buttonType -> {
                    if (buttonType == ButtonType.OK) {
                        socketMediator.sendStopSignal();
                        stopRequestDialog.close();
                    }

                    return null;
                });

                pane.getButtonTypes().add(ButtonType.OK);

                stopRequestDialog.setDialogPane(pane);

                stopRequestDialog.showAndWait();
            }

            @Override
            public void onStop(String initiator) {
                stopRequestDialog.close();
                close();
            }

            @Override
            public void onGameOver(GameResult gameResult) {
                controller.opponentGameField.setDisable(true);

                Dialog<Void> dialog = new Dialog<>();
                dialog.initOwner(GameWindow.this);

                DialogPane pane = new DialogPane();

                VBox content = new VBox();
                content.setSpacing(10);

                HBox winnerBox = new HBox();
                HBox yourMovesBox = new HBox();
                HBox opponentMovesBox = new HBox();

                winnerBox.setSpacing(10);
                winnerBox.getChildren().addAll(new Text("Winner:"),
                        new Text(gameResult.isYouWon() ? (socketMediator.name + " (Me)") : socketMediator.opponentName));

                yourMovesBox.setSpacing(10);
                yourMovesBox.getChildren().addAll(new Text("Your moves count:"),
                        new Text(String.valueOf(gameResult.getYourMoveCount())));

                opponentMovesBox.setSpacing(10);
                opponentMovesBox.getChildren().addAll(new Text("Opponent moves count:"),
                        new Text(String.valueOf(gameResult.getOpponentMoveCount())));

                content.getChildren().addAll(winnerBox, yourMovesBox, opponentMovesBox);

                pane.setContent(content);
                pane.getButtonTypes().add(ButtonType.OK);

                dialog.setDialogPane(pane);

                dialog.showAndWait();
            }
        });
    }
}

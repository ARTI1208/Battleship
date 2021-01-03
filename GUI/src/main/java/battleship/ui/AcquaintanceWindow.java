package battleship.ui;

import battleship.net.BattleshipSocketMediator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AcquaintanceWindow extends Stage {

    public enum AppType {
        SERVER,
        CLIENT
    }

    private final AppType appType;

    private final AcquaintanceController controller;

    public AcquaintanceWindow(AppType appType) throws IOException {
        this.appType = appType;


        FXMLLoader loader = new FXMLLoader(AcquaintanceWindow.class.getClassLoader().getResource(("client_acquaintance.fxml")));


        Parent p = loader.load();

        Scene primaryScene = new Scene(p);

        controller = loader.getController();

//        c.setScene(primaryScene);

        primaryScene.getStylesheets().add("styles.scss");

        setMinHeight(200);
        setMinWidth(400);
        setResizable(false);
        setScene(primaryScene);
        setTitle("BattleshipGame: Acquaintance");


        setup();
    }

    private void setup() {
        if (appType == AppType.SERVER) {
            controller.hostParent.setVisible(false);
            controller.hostParent.setManaged(false);
            controller.startGameButton.setText("Create game");
        } else {
            controller.startGameButton.setText("Connect");
        }

        controller.nickTextField.setText(appType.name());

        controller.startGameButton.setOnAction(event -> {
            if (controller.nickTextField.getText().isEmpty())
                return;

            int port = Integer.parseInt(controller.portTextField.getText());

            close();

            BattleshipSocketMediator mediator = new BattleshipSocketMediator(
                    appType,
                    controller.hostUrlTextField.getText(),
                    port,
                    controller.nickTextField.getText()
            );

            new ShipPositioningWindow(mediator).show();
        });
    }
}

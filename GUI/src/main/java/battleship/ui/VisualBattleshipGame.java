package battleship.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VisualBattleshipGame extends Application {

    /**
     * Entry point of GUI
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws IOException {


        if (getParameters().getRaw().isEmpty()) {
            new AcquaintanceWindow(AcquaintanceWindow.AppType.SERVER).show();
        } else {
            String type = getParameters().getRaw().get(0);

            if (type.equals("Client")) {
                new AcquaintanceWindow(AcquaintanceWindow.AppType.CLIENT).show();
            } else {
                new AcquaintanceWindow(AcquaintanceWindow.AppType.SERVER).show();
            }

        }
//
//        FXMLLoader loader = new FXMLLoader(VisualBattleshipGame.class.getClassLoader().getResource(("main_window.fxml")));
//
//
//
//        Parent p = loader.load();
//
//        Scene primaryScene = new Scene(p, 720, 680);
//
//        MainWindowController c = loader.getController();
//        c.setScene(primaryScene);
//
//        primaryScene.getStylesheets().add("styles.scss");
//
//        primaryStage.setMinHeight(680);
//        primaryStage.setMinWidth(720);
//        primaryStage.setScene(primaryScene);
//        primaryStage.setTitle("BattleshipGame");
//        primaryStage.show();
    }
}

package eu.isawsm.accelerate.server;

import eu.isawsm.accelerate.server.UI.MainForm;
import eu.isawsm.accelerate.server.UI.SettingsView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Created by ofade on 15.08.2015.
 */
public class App extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new AxServer(primaryStage);
    }
}

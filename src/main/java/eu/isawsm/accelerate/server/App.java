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
        AxProperties axProperties;
        axProperties = new AxProperties();

        if(!axProperties.check()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Settings.fxml"));
            Parent root = loader.load();
            SettingsView settings = loader.getController();

            settings.setProperties(axProperties);

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Settings - Axelerate Server");
            primaryStage.getIcons().add(new Image("ic_launcher.png"));

            primaryStage.show();
        }



        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainForm.fxml"));
        Parent root = loader.load();
        MainForm mainForm = loader.getController();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(axProperties.club.getName() + " - Axelerate Server");
        primaryStage.getIcons().add(new Image("ic_launcher.png"));

        mainForm.setStatusText("Connected to " + axProperties.decoder.toString() + " via " + axProperties.COMPORT);
        mainForm.setPrimaryStage(primaryStage);
        mainForm.setProperties(axProperties);
        primaryStage.show();

        new AxServer(axProperties, mainForm);
    }
}

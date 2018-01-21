package ch.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*********************
 *
 * Aktualnie DO ZROBIENIA:
 * - pobieranie mapy klocków z serwera
 *
 * ***********************/

public class Main extends Application {
    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("view/root.fxml"));
        primaryStage.setTitle("Labirynth");
        primaryStage.setScene(new Scene(root, 600, 385));

        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

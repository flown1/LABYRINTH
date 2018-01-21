package ch.client.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class MainController {
    int portNumber = 4444;
    @FXML
    private Button startBtn;
    @FXML
    private GridPane boardGrid;
    @FXML
    private void initialize() {}
    @FXML
    public void handleStartBtnClick(ActionEvent event) throws IOException{
        goToGameScene(event);
    }

    public MainController(){}
    private void goToGameScene(ActionEvent event){
        BorderPane gameSceneParent;
        Scene gameScene = null;
        try {
            gameSceneParent = (BorderPane) FXMLLoader.load(getClass().getResource("gameScene.fxml"));           //tutaj gdzies wypierdala Connection Exception
            gameScene = new Scene(gameSceneParent);
        }catch(IOException e) {
            System.out.println("Error while creating scene");
            System.out.println(e);
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gameScene.fxml"));
            loader.setController(new BoardController());

        }catch(Exception e) {
            System.out.println("Failed to set controller");
        }
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if(gameScene != null) {
            stage.setScene(gameScene);
            stage.show();
        }

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
    }


}

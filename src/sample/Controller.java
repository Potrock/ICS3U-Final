package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class Controller {

    @FXML
    private Button startbtn;

    @FXML
    private Button exitbtn;

    @FXML
    private Button playbtn;

    @FXML
    void startOnAction(ActionEvent event) throws IOException {
        Main.setGameState(Main.gameState.ROUNDINPUT);
    }

    @FXML
    void playOnAction (ActionEvent event) throws IOException {
        Main.setGameState(Main.gameState.MAP1);
    }

    @FXML
    void exitOnAction(ActionEvent event) {
        Platform.exit();
    }

}
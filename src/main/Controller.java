package main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Controller {

    @FXML
    private Button startbtn;

    @FXML
    private Button exitbtn;

    @FXML
    private Button playbtn;

    @FXML
    public TextField inputField;

    @FXML
    private Button submitButton;

    @FXML
    private Button playAgain;

    @FXML
    private Button exitButton;

    @FXML
    void onPlayAgain(ActionEvent event) throws IOException {
        Main.setGameState(Main.gameState.ROUNDINPUT);
        System.out.println("working?");
    }

    @FXML
    void startOnAction(ActionEvent event) throws IOException {
        Main.setGameState(Main.gameState.ROUNDINPUT);
    }

    @FXML
    void playOnAction (ActionEvent event) throws IOException {
        Main.setGameState(Main.gameState.MAP1);
        Main.totalRounds = Integer.parseInt(inputField.getText());
        System.out.println(Main.totalRounds);
    }

    @FXML
    void exitOnAction(ActionEvent event) {
        Platform.exit();
    }

}
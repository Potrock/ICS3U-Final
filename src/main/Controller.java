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
    private Button exitButton;

    @FXML
    private Button leaderButton;

    @FXML
    void displayLeader(ActionEvent event) throws IOException {
        Main.setGameState(Main.gameState.LEADERBOARD);
        System.out.println("nice");
    }

    @FXML
    void startOnAction(ActionEvent event) throws IOException {
        Main.setGameState(Main.gameState.ROUNDINPUT);
    }

    @FXML
    void onSubmit(ActionEvent event) {
        Main.submitScore();
        System.out.println("nayce");
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
package com.example.simplegame;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LogInController implements Initializable {

    @FXML
    private PasswordField passWord;

    @FXML
    private TextField userName;

    @FXML
    private Button button_login;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Login button clicked");
                DBUtils.login(event, userName.getText(), passWord.getText());
            }
        });

    }
}

package com.example.simplegame;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffSaleSystem implements Initializable {

    private String username;

    @FXML
    private Button button_details;

    @FXML
    private Button button_logout;

    @FXML
    private Button button_sale;

    @FXML
    private Button button_schedule;

    @FXML
    private Label label_welcome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Trying to log out..");
                DBUtils.logOut(event, "login.fxml", "Login", null);
            }
        });

        button_details.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Trying to show the details..");
                DBUtils.details(event, "details.fxml", "Staff Sale System", username);
            }
        });

        button_schedule.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Trying to show the schedule..");
                DBUtils.goToWorkSchedule(event, "work_schedule.fxml", "Staff Sale System", username);
            }
        });

        button_sale.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Trying to show the Sale page..");
                DBUtils.goToSale(event, "sale.fxml", "Staff Sale System", username);
            }
        });

    }

    public void setLabel_welcome(String name, String username) {
        label_welcome.setText("Hello " + name + ",");
        this.username = username;
    }

}

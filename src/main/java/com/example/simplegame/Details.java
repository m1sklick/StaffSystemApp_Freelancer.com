package com.example.simplegame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Details implements Initializable {

    private String username;

    @FXML
    private Button button_back;

    @FXML
    private Label lb_currentpay;

    @FXML
    private Label lb_hoursworked;

    @FXML
    private Label lb_payrate;

    @FXML
    private Label lb_role;

    @FXML
    private Label lb_staffname;

    @FXML
    private Label lb_staffnumber;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Trying to go back to the main page..");
                DBUtils.changeScene(event, "staff_sale_system.fxml", "Staff Sale System", username);
            }
        });
    }

    public void setDetails(String staff_name, String staff_number, int pay_rate, String staff_role, int hours_worked, int current_pay, String username) {
        lb_currentpay.setText("$" + current_pay);
        lb_hoursworked.setText(hours_worked + " Hrs");
        lb_payrate.setText("$" + pay_rate + " /PH");
        lb_role.setText(staff_role);
        lb_staffname.setText(staff_name);
        lb_staffnumber.setText(staff_number);
        this.username = username;
    }
}

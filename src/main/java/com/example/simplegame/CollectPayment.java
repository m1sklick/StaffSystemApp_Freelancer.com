package com.example.simplegame;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class CollectPayment implements Initializable {

    private String username;
    private double total_amount;
    private Map<Integer, Integer> id_qty = new HashMap<>();

    @FXML
    private Button button_back;

    @FXML
    private Button button_submit;

    @FXML
    private Label label_total_amount;

    @FXML
    private TextField tr_card_number;

    @FXML
    private TextField tr_cvv;

    @FXML
    private TextField tr_email;

    @FXML
    private TextField tr_exp_date_month;

    @FXML
    private TextField tr_exp_date_year;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button_back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Trying to go back to the Sale page..");
                DBUtils.goToSale(event, "sale.fxml", "Staff Sale System", username);
            }
        });

        button_submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Trying to Submit..");
                submit();
            }
        });

    }

    public void setDetails(String username, double total_amount, Map<Integer, Integer> id_qty) {
        this.username = username;
        this.total_amount = total_amount;
        this.id_qty = id_qty;
        System.out.println(id_qty.toString());
        label_total_amount.setText("Total Amount: $" + total_amount);

    }

    public void submit() {
        // validation
        if(Objects.equals(tr_email.getText(), "")
                || Objects.equals(tr_cvv.getText(), "")
                || Objects.equals(tr_card_number.getText(), "")
                || Objects.equals(tr_exp_date_month.getText(), "")
                || Objects.equals(tr_exp_date_year.getText(), ""))empty_input_alert();
        else if(!contains16DigitsOnly(tr_card_number.getText()))incorrect_card_input_alert();
        else {
            System.out.println("Input is valid! submitting...");
            successful_submit();
        }
    }

    public static boolean contains16DigitsOnly(String input) {
        // Remove any non-digit characters from the string
        String digitsOnly = input.replaceAll("\\D", "");

        // Check if the resulting string has exactly 16 digits
        return digitsOnly.length() == 16;
    }

    public void empty_input_alert() {
        System.out.println("Input is incorrect!");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Some of the fields are empty!");
        alert.show();
    }

    public void incorrect_card_input_alert() {
        System.out.println("Input is incorrect!");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("The card number should consist of 16 digits!");
        alert.show();
    }

    public void successful_submit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Order Successful");
        int random_ref = (int)Math.floor(Math.random() * (9999999 - 1000000 + 1) + 1000000);
        System.out.println("Order Ref: " + random_ref);
        alert.setContentText("Order Ref: " + random_ref);
        alert.show();

        try {
            // Establish a connection to the MySQL database
            Connection connection = DriverManager.getConnection(new DataBaseInfo().getDataBaseConnectionURL());

            // Prepare the SQL statement to insert a new row into the "sales" table
            String sql = "INSERT INTO sales (staff_number, amount_sold) VALUES (?, ?)";
            String updateSql = "UPDATE stock SET quantity = quantity - ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);

            // Retrieve the staff_number based on the username from the "staff" table
            String staffNumberSql = "SELECT staff_number FROM staff WHERE username = ?";
            PreparedStatement staffNumberStatement = connection.prepareStatement(staffNumberSql);
            staffNumberStatement.setString(1, username);
            int staffNumber = -1;  // Initialize with a default value

            for (Map.Entry<Integer, Integer> entry : id_qty.entrySet()) {
                int productId = entry.getKey();
                int quantity = entry.getValue();

                // Set the values for the update statement
                updateStatement.setInt(1, quantity);
                updateStatement.setInt(2, productId);

                // Execute the update statement
                updateStatement.executeUpdate();
            }


            // Execute the query to retrieve the staff_number
            if (staffNumberStatement.execute()) {
                var resultSet = staffNumberStatement.getResultSet();
                if (resultSet.next()) {
                    staffNumber = resultSet.getInt("staff_number");
                }
            }

            // Set the values for the prepared statement
            statement.setInt(1, staffNumber);
            statement.setDouble(2, total_amount);

            // Execute the SQL statement to insert the new row into the "sales" table
            statement.executeUpdate();

            // Close the prepared statement and database connection
            updateStatement.close();
            statement.close();
            connection.close();

            System.out.println("New row inserted into 'sales' table.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

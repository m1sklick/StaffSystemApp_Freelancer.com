package com.example.simplegame;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.net.URL;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class WorkSchedule implements Initializable {

    private String username;

    @FXML
    private Button button_back;

    @FXML
    private GridPane gridPane_calendar;

    @FXML
    private Label label_month;

    @FXML
    private Label label_year;

    private LocalDate currentDate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        currentDate = LocalDate.now();

        button_back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Trying to go back to the main page..");
                DBUtils.changeScene(event, "staff_sale_system.fxml", "Staff Sale System", username);
            }
        });

    }

    public void setUsername(String username) {
        this.username = username;
        generateCalendar();
        showSchedule();
    }

    private void generateCalendar() {
        // Clear existing calendar
        gridPane_calendar.getChildren().clear();

        // Month and Year labels
        label_month.setText(currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        label_year.setText(Integer.toString(currentDate.getYear()));

        // Days of the Week labels
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            int columnIndex = dayOfWeek.getValue() - 1;
            Label dayLabel = new Label(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            gridPane_calendar.add(dayLabel, columnIndex, 0);
        }

        // Get the first day of the month
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);

        // Determine the day of the week for the first day of the month
        int startColumn = firstDayOfMonth.getDayOfWeek().getValue() - 1;

        // Populate calendar cells
        int row = 1;
        int column = startColumn;
        for (int day = 1; day <= firstDayOfMonth.lengthOfMonth(); day++) {
            String button_cell = day + "\n" + " ";
            Button dayButton = new Button(button_cell);
            dayButton.setFont(Font.font("Arial", 9));
            dayButton.setPrefWidth(43);
            dayButton.setStyle("-fx-background-color: grey;");
            gridPane_calendar.add(dayButton, column, row);

            column++;
            if (column == 7) {
                column = 0;
                row++;
            }
        }
    }

    private void showSchedule() {
        // Assuming you have a database connection setup
        Connection connection = null;
        PreparedStatement statement = null;

        ResultSet resultSet = null;

        try {
            // Establish a connection to the database
            connection = DriverManager.getConnection(new DataBaseInfo().getDataBaseConnectionURL());

            // Prepare the SQL statement to retrieve the schedule for the given user
            String sql = "SELECT shift_date, shift_hours FROM schedules WHERE staff_id = (SELECT id FROM staff WHERE username = ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            // Execute the query
            resultSet = statement.executeQuery();


            // Iterate over the result set and update the calendar buttons
            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("shift_date").toLocalDate();
                if(currentDate.getMonth() == date.getMonth()){
                    Button dayButton = (Button) gridPane_calendar.getChildren().get(date.getDayOfMonth() - 1 + 7);
                    // Update the background color
                    dayButton.setText(dayButton.getText() + resultSet.getInt("shift_hours") + " Hrs");
                    dayButton.setStyle("-fx-background-color: green;");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}

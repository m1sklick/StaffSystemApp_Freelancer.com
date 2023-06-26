package com.example.simplegame;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.lang.*;

public class DBUtils {
    private String username = null;


    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username){
        Parent root;
        System.out.println("Trying to change the scene..");
        if(username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                StaffSaleSystem staffSaleSystem = loader.getController();
                String staff_name = getDataOfUserFromDB(username)[0];
                staffSaleSystem.setLabel_welcome(staff_name, username);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle(title);
                stage.setScene(new Scene(root, 600, 400));
                stage.show();
                System.out.println("The scene was changed..");
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void logOut(ActionEvent event, String fxmlFile, String title, String username){
        Parent root;
        System.out.println("Trying to change the scene..");
        if(username == null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                LogInController logInController = loader.getController();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle(title);
                stage.setScene(new Scene(root, 600, 400));
                stage.show();
                System.out.println("The scene was changed..");
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static String[] getDataOfUserFromDB(String username) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String[] result = new String[5];
        result[0] = null; // retrievedStaffName
        result[1] = null; // retrievedStaffNumber
        result[2] = null; // retrievedStaffRole
        result[3] = null; // retrievedStaffPay
        result[4] = null; // retrievedStaffHoursWorked

        try {
            connection = DriverManager.getConnection(new DataBaseInfo().getDataBaseConnectionURL());
            preparedStatement = connection.prepareStatement("SELECT password, staff_name, staff_number, staff_role, staff_pay, hours_worked  FROM staff WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                result[0] = resultSet.getString("staff_name");
                result[1] = resultSet.getString("staff_number");
                result[2] = resultSet.getString("staff_role");
                result[3] = String.valueOf(resultSet.getInt("staff_pay"));
                result[4] = String.valueOf(resultSet.getInt("hours_worked"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static void login(ActionEvent event, String username, String password){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(new DataBaseInfo().getDataBaseConnectionURL());
            preparedStatement = connection.prepareStatement("SELECT password, staff_name, staff_number, staff_role, staff_pay, hours_worked  FROM staff WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if(!resultSet.isBeforeFirst()){
               System.out.println("User not found in databse!");
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setContentText("The providen credentials are incorrect!");
               alert.show();
            } else {
                while(resultSet.next()){
                    String retrievedPassword = resultSet.getString("password");
                    String retrievedStaffName = resultSet.getString("staff_name");
                    String retrievedStaffNumber = resultSet.getString("staff_number");
                    String retrievedStaffRole = resultSet.getString("staff_role");
                    int retrievedStaffPay = resultSet.getInt("staff_pay");
                    int retrievedStaffHoursWorked = resultSet.getInt("hours_worked");

                    if(retrievedPassword.equals(password)){ //successful login
                        changeScene(event, "staff_sale_system.fxml", "Staff Sale System", username);
                    } else {
                        System.out.println("passwods didn't match");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("The providen credentials are incorrect!");
                        alert.show();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void details(ActionEvent event, String fxmlFile, String title, String username) {
        Parent root;
        System.out.println("Trying to change the scene..");
        if(username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                Details details = loader.getController();

                String[] user_details = getDataOfUserFromDB(username);
                // 0 - staffname
                // 1 - staffnumber
                // 2 - staffrole
                // 3 - staffpay
                // 4 - staffhoursworked

                int staffpay = Integer.parseInt(user_details[3]);
                int staffhoursworked = Integer.parseInt(user_details[4]);
                int currentpay = staffpay * staffhoursworked;


                details.setDetails(user_details[0], user_details[1], staffpay, user_details[2], staffhoursworked, currentpay, username);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle(title);
                stage.setScene(new Scene(root, 600, 400));
                stage.show();
                System.out.println("The scene was changed..");
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void goToWorkSchedule(ActionEvent event, String fxmlFile, String title, String username) {
        Parent root;
        System.out.println("Trying to change the scene..");
        if(username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                WorkSchedule workSchedule = loader.getController();
                workSchedule.setUsername(username);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle(title);
                stage.setScene(new Scene(root, 600, 400));
                stage.show();
                System.out.println("The scene was changed..");
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void goToSale(ActionEvent event, String fxmlFile, String title, String username) {
        Parent root;
        System.out.println("Trying to change the scene..");
        if(username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                SaleController saleController = loader.getController();
                saleController.setUsername(username);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle(title);
                stage.setScene(new Scene(root, 600, 400));
                stage.show();
                System.out.println("The scene was changed..");
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void collectPayment(ActionEvent event, String fxmlFile, String title, String username, double total_amount) {
        Parent root;
        System.out.println("Trying to change the scene..");
        if(username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                CollectPayment collectPayment = loader.getController();


                collectPayment.setDetails(username, total_amount);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle(title);
                stage.setScene(new Scene(root, 600, 400));
                stage.show();
                System.out.println("The scene was changed..");
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}

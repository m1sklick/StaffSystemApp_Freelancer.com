package com.example.simplegame;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SaleController implements Initializable {
    private String username;
    private double total_amount;
    private Map<Integer, Integer> id_qty = new HashMap<>();

    @FXML
    private Button button_back;

    @FXML
    private Button button_collectpayment;

    @FXML
    private VBox productList;

    @FXML
    private VBox cartList;

    @FXML
    private TextField searchField;

    @FXML
    private Label totalLabel;

    private Map<Integer, Integer> cartItems;
    private Map<Integer, String> productNames;
    private Map<Integer, Double> productPrices;
    private Map<Integer, String> productImageUrls; // Added map for storing image URLs

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_back.setOnAction(event -> {
            System.out.println("Trying to go back to the main page..");
            DBUtils.changeScene(event, "staff_sale_system.fxml", "Staff Sale System", username);
        });
        button_collectpayment.setOnAction(event -> {
            System.out.println("Trying to go to the payment page..");
            if (total_amount != 0)
                DBUtils.collectPayment(event, "collect_payment.fxml", "Staff Sale System", username, total_amount, id_qty);
        });

        cartItems = new HashMap<>();
        productNames = new HashMap<>();
        productPrices = new HashMap<>();
        productImageUrls = new HashMap<>(); // Initialize the map for image URLs

        loadProducts();
        addSearchListener();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private void loadProducts() {
        try {
            // Connect to the MySQL database
            Connection connection = DriverManager.getConnection(new DataBaseInfo().getDataBaseConnectionURL());
            Statement statement = connection.createStatement();

            // Retrieve the products from the stock table
            ResultSet resultSet = statement.executeQuery("SELECT * FROM stock");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                String imageUrl = resultSet.getString("image_name");

                // Create an ImageView for the product image/icon
                ImageView imageView = new ImageView(new Image(imageUrl));
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);

                // Create a Label to display the product name and price
                Label label = new Label(name + " - $" + price);
                label.setGraphic(imageView);

                // Add the event handler to the Label
                label.setOnMouseClicked(event -> {
                    addToCart(id, name);
                });

                // Add the Label to the product list
                productList.getChildren().add(label);

                // Store the product name, price, and image URL for later retrieval
                productNames.put(id, name);
                productPrices.put(id, price);
                productImageUrls.put(id, imageUrl);
            }

            // Close the database connection
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addToCart(int productId, String productName) {
        if (cartItems.containsKey(productId)) {
            int quantity = cartItems.get(productId);
            cartItems.put(productId, quantity + 1);
        } else {
            cartItems.put(productId, 1);
        }

        updateCartView();
    }

    private void updateCartView() {
        cartList.getChildren().clear();
        double totalAmount = 0;

        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            double price = getProductPrice(productId);

            id_qty.put(productId, quantity);

            totalAmount += price * quantity;

            Label quantityLabel = new Label(String.valueOf(quantity));
            Label nameLabel = new Label(getProductName(productId));

            HBox cartItemBox = new HBox(10);
            cartItemBox.getChildren().addAll(quantityLabel, nameLabel);

            // Add the event handler to the cart item box
            cartItemBox.setOnMouseClicked(event -> {
                decreaseQuantity(productId);
            });

            cartList.getChildren().add(cartItemBox);
        }

        this.total_amount = totalAmount;
        totalLabel.setText("Total: $" + totalAmount);
    }

    private String getProductName(int productId) {
        // Fetch the product name from the database based on the product ID
        return productNames.getOrDefault(productId, "");
    }

    private double getProductPrice(int productId) {
        // Fetch the product price from the database based on the product ID
        return productPrices.getOrDefault(productId, 0.0);
    }

    private void addSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.toLowerCase();

            // Clear the product list
            productList.getChildren().clear();

            for (Map.Entry<Integer, String> entry : productNames.entrySet()) {
                int productId = entry.getKey();
                String name = entry.getValue().toLowerCase();

                if (name.contains(searchTerm)) {
                    double price = getProductPrice(productId);
                    String imageUrl = productImageUrls.get(productId); // Retrieve the image URL from the stored map

                    // Create an ImageView for the product image/icon
                    ImageView imageView = new ImageView(new Image(imageUrl));
                    imageView.setFitHeight(50);
                    imageView.setPreserveRatio(true);

                    // Create a Label to display the product name and price
                    Label label = new Label(name + " - $" + price);
                    label.setGraphic(imageView);

                    // Add the event handler to the Label
                    label.setOnMouseClicked(event -> {
                        addToCart(productId, name);
                    });

                    // Add the Label to the product list
                    productList.getChildren().add(label);
                }
            }
        });
    }

    private void decreaseQuantity(int productId) {
        if (cartItems.containsKey(productId)) {
            int quantity = cartItems.get(productId);

            if (quantity > 1) {
                cartItems.put(productId, quantity - 1);
            } else {
                cartItems.remove(productId);
            }

            updateCartView();
        }
    }
}

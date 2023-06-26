module com.example.simplegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.simplegame to javafx.fxml;
    exports com.example.simplegame;
}
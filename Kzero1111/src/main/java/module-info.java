module com.example.kzero1111 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.kzero1111 to javafx.fxml;
    exports com.example.kzero1111;
}
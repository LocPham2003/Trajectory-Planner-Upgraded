module com.example.trajectoryplanner {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.trajectoryplanner to javafx.fxml;
    exports com.example.trajectoryplanner;
}
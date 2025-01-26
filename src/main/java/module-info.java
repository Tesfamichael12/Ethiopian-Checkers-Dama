module com.demotestpackage.demotest {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.demotestpackage.demotest to javafx.fxml;
    exports com.demotestpackage.demotest;
}
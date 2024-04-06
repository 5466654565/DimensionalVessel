module com.o {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.logging;
    
    opens com.o to javafx.fxml;
    exports com.o;
}
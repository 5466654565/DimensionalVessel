package com.o;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.InputStream;
import java.util.logging.LogManager;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
        Scene scene = new Scene(root, 800, 800);
        scene.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
        primaryStage.setTitle("Application Title");
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("start");
    }

    public static void main(String[] args) {
        try (InputStream stream = MainApp.class.getClassLoader().getResourceAsStream("logger.properties")) {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (Exception e) {
            System.err.println("Could not load 'logger.properties'.");
            e.printStackTrace();
        }
        
        launch(args);
    }
}

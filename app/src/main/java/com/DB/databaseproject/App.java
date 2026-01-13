package com.DB.databaseproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Stone Sales Management System
 * Main Application Entry Point
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load FXML file from resources folder
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // Create scene without fixed size for full-screen experience
            Scene scene = new Scene(root);

            // Load CSS stylesheet from resources folder
            String css = getClass().getResource("/css/login.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Configure stage for maximized window mode (NOT full-screen)
            primaryStage.setTitle("Stone Sales – Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true); // Enable resizing
            
            // Set minimum window size
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(500);
            
            // Show the stage first
            primaryStage.show();
            
            // Force exact screen size using visual bounds (no gaps on sides)
            javafx.application.Platform.runLater(() -> {
                javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                primaryStage.setX(bounds.getMinX());
                primaryStage.setY(bounds.getMinY());
                primaryStage.setWidth(bounds.getWidth());
                primaryStage.setHeight(bounds.getHeight());
            });
            
            System.out.println("Application started at exact screen size with title bar");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading login view: " + e.getMessage());
        }
    }

    /**
     * Main method - Entry point of the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}


package com.DB.databaseproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Stone Sales Management System
 * Main Application Entry Point - Split Layout Version
 */
public class AppSplitLayout extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load FXML file for split layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login_split.fxml"));
            Parent root = loader.load();

            // Create scene without fixed size for full-screen experience
            Scene scene = new Scene(root);

            // Load CSS stylesheet for split layout
            String css = getClass().getResource("login_split.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Configure stage for maximized window mode (NOT full-screen)
            primaryStage.setTitle("Stone Sales – Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            
            // Show the stage first
            primaryStage.show();
            
            // Maximize AFTER scene is set
            javafx.application.Platform.runLater(() -> {
                primaryStage.setMaximized(true);
            });
            
            System.out.println("AppSplitLayout started in maximized mode with title bar");

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

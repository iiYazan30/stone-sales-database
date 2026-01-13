package com.DB.databaseproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * LoginView - Initializes and displays the login screen
 * Stone Sales Management System
 */
public class LoginView extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            // Create scene with screen dimensions
            Scene scene = new Scene(root);

            // Load CSS stylesheet
            String css = getClass().getResource("login.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Configure stage for maximized window mode (NOT full-screen)
            primaryStage.setTitle("Stone Sales – Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            
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
            
            System.out.println("Login window opened at exact screen size with title bar");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading login view: " + e.getMessage());
        }
    }

    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}

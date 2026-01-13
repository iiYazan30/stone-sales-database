package com.DB.databaseproject.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * LoginView - Manages the Login Page UI
 * Stone Sales Management System
 */
public class LoginView {
    
    private Stage stage;
    private Scene scene;
    
    /**
     * Constructor
     * @param stage Primary stage for the application
     */
    public LoginView(Stage stage) {
        this.stage = stage;
        initializeView();
    }
    
    /**
     * Initialize and setup the login view
     */
    private void initializeView() {
        try {
            // Load FXML file
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
            );
            Parent root = loader.load();
            
            // Create scene without fixed size
            scene = new Scene(root);
            
            // Apply CSS stylesheet
            String css = getClass().getResource("/css/login.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            // Configure stage for maximized window mode (NOT full-screen)
            stage.setTitle("Stone Sales – Login");
            stage.setScene(scene);
            stage.setResizable(true);
            
            // Maximize AFTER scene is set
            javafx.application.Platform.runLater(() -> {
                stage.setMaximized(true);
            });
            
            System.out.println("Login view initialized in maximized mode with title bar");
            
        } catch (IOException e) {
            System.err.println("Error loading login view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Show the login window
     */
    public void show() {
        stage.show();
    }
    
    /**
     * Get the stage
     * @return Stage object
     */
    public Stage getStage() {
        return stage;
    }
    
    /**
     * Get the scene
     * @return Scene object
     */
    public Scene getScene() {
        return scene;
    }
}

package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.User;
import com.DB.databaseproject.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the Login Page
 * Stone Sales Management System
 */
public class LoginController {
    
    @FXML
    private AnchorPane rootPane;
    
    @FXML
    private VBox loginCard;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    private final AuthenticationService authService = AuthenticationService.getInstance();
    
    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        // Hide error label initially
        errorLabel.setVisible(false);
        
        // Add Enter key listener to password field
        passwordField.setOnAction(event -> handleLogin());
    }
    
    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Hide previous error
        errorLabel.setVisible(false);
        
        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }
        
        // Authenticate using database
        User user = authService.login(username, password);
        
        if (user != null) {
            System.out.println("Login successful! User: " + user.getUserName() + " | Role: " + user.getRole());
            navigateToMainScreen(user);
        } else {
            showError("Invalid username or password");
        }
    }
    
    /**
     * Show error message to user
     * @param message Error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        
        // Add shake animation effect (optional enhancement)
        shakeCard();
    }
    
    /**
     * Shake animation for login card on error
     */
    private void shakeCard() {
        javafx.animation.TranslateTransition transition = 
            new javafx.animation.TranslateTransition(
                javafx.util.Duration.millis(100), loginCard);
        transition.setFromX(0);
        transition.setByX(10);
        transition.setCycleCount(4);
        transition.setAutoReverse(true);
        transition.play();
    }
    
    /**
     * Navigate to appropriate dashboard based on user role
     */
    private void navigateToMainScreen(User user) {
        try {
            String fxmlPath = "";
            String title = "";
            
            // Determine which dashboard to load based on user role
            switch (user.getRole().toLowerCase()) {
                case "admin":
                    fxmlPath = "/fxml/admin_dashboard.fxml";
                    title = "Stone Sales – Admin Dashboard";
                    break;
                case "employee":
                    fxmlPath = "/fxml/employee_dashboard.fxml";
                    title = "Stone Sales – Employee Dashboard";
                    break;
                case "customer":
                    fxmlPath = "/fxml/customer_dashboard.fxml";
                    title = "Stone Sales – Customer Dashboard";
                    break;
                default:
                    showError("Unknown user role: " + user.getRole());
                    return;
            }
            
            System.out.println("Loading dashboard: " + fxmlPath);
            
            // Load the appropriate dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // Create new scene
            Scene scene = new Scene(root);
            
            // Get the current stage
            Stage stage = (Stage) usernameField.getScene().getWindow();
            
            // Set new scene and title
            stage.setScene(scene);
            stage.setTitle(title);
            
            // Make sure window is maximized to exact screen size
            javafx.application.Platform.runLater(() -> {
                javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
            });
            
            System.out.println("✅ Successfully navigated to " + user.getRole() + " dashboard");
            
        } catch (Exception e) {
            System.err.println("❌ Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading dashboard. Please try again.");
        }
    }
    
    /**
     * Clear all fields
     */
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        errorLabel.setVisible(false);
    }
}

package com.DB.databaseproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for Employee Dashboard
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class EmployeeDashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox welcomeView;

    @FXML
    private Button ordersBtn;

    @FXML
    private Button profileBtn;

    @FXML
    private Button logoutBtn;

    private Button activeButton;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Employee Dashboard initialized");
        
        // Automatically load Assigned Orders view on startup
        // This ensures employees see their assigned orders immediately
        showAssignedOrders();
    }

    /**
     * Show Welcome View
     */
    private void showWelcome() {
        if (welcomeView != null) {
            welcomeView.setVisible(true);
        }
    }

    /**
     * Show Assigned Orders View
     * Renamed from showOrders() for clarity - displays orders assigned to logged-in employee
     */
    @FXML
    private void showOrders() {
        showAssignedOrders();
    }
    
    /**
     * Load Assigned Orders view and set active navigation state
     */
    private void showAssignedOrders() {
        setActiveButton(ordersBtn);
        loadView("/fxml/employee_orders_view.fxml");
    }

    /**
     * Show Profile View (Optional)
     */
    @FXML
    private void showProfile() {
        setActiveButton(profileBtn);
        
        // Load employee profile view
        System.out.println("Loading Profile view");
        loadView("/fxml/employee_profile_view.fxml");
    }

    /**
     * Handle Logout - Return to Login Screen
     */
    @FXML
    private void handleLogout() {
        try {
            System.out.println("Logging out from Employee Dashboard...");
            
            // Load login screen
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
            );
            Parent root = loader.load();
            
            // Get current stage
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            
            // Create new scene with login (no fixed size)
            Scene scene = new Scene(root);
            
            // Load login CSS
            String css = getClass().getResource("/css/login.css").toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);
            
            // Set scene
            stage.setScene(scene);
            stage.setTitle("Stone Sales – Login");
            stage.setResizable(true);
            
            // Force exact screen size using visual bounds (no gaps on sides)
            javafx.application.Platform.runLater(() -> {
                javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
            });
            
            System.out.println("Returned to login screen at exact screen size with title bar");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading login screen: " + e.getMessage());
        }
    }

    /**
     * Load a view into the content area
     * @param fxmlPath path to FXML file
     */
    private void loadView(String fxmlPath) {
        try {
            System.out.println("Loading view: " + fxmlPath);
            
            // Hide welcome view
            if (welcomeView != null) {
                welcomeView.setVisible(false);
            }
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Clear content area and add new view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            System.out.println("View loaded successfully: " + fxmlPath);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlPath);
            System.err.println("Error message: " + e.getMessage());
        }
    }

    /**
     * Set the active button and update styles
     * @param button the button to set as active
     */
    private void setActiveButton(Button button) {
        // Remove 'selected' class from previous active button
        if (activeButton != null) {
            activeButton.getStyleClass().remove("selected");
        }
        
        // Add 'selected' class to new active button
        if (button != null && !button.getStyleClass().contains("selected")) {
            button.getStyleClass().add("selected");
        }
        
        // Update active button reference
        activeButton = button;
    }
}

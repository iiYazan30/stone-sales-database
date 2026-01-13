package com.DB.databaseproject;

import com.DB.databaseproject.model.User;
import com.DB.databaseproject.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 * Controller for the Login Page
 * Stone Sales Management System
 */
public class LoginController {
    
    private final AuthenticationService authService = AuthenticationService.getInstance();

    @FXML
    private VBox loginCard;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Label signupLink;

    @FXML
    private StackPane imagePane;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private Rectangle overlayRectangle;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        // Hide error label initially
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        // Add enter key listener to password field
        passwordField.setOnAction(event -> handleLogin());

        // Bind image and rectangle sizes to parent pane for responsive resizing
        if (imagePane != null && backgroundImage != null) {
            backgroundImage.fitWidthProperty().bind(imagePane.widthProperty());
            backgroundImage.fitHeightProperty().bind(imagePane.heightProperty());
        }

        if (imagePane != null && overlayRectangle != null) {
            overlayRectangle.widthProperty().bind(imagePane.widthProperty());
            overlayRectangle.heightProperty().bind(imagePane.heightProperty());
        }
    }

    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        // Get input values and TRIM whitespace
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║           LOGIN ATTEMPT - GUI LAYER             ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println("📝 Username (trimmed): '" + username + "'");
        System.out.println("🔑 Password length: " + password.length() + " characters");

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("⚠️ Validation failed: Empty username or password");
            showError("Please enter both username and password.");
            return;
        }

        System.out.println("✓ Input validation passed");
        System.out.println("🔄 Calling authentication service...\n");

        // Authenticate user using database
        User user = authService.login(username, password);
        
        if (user != null) {
            hideError();
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║        ✅ LOGIN SUCCESSFUL - GUI LAYER          ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.println("👤 Username: " + user.getUserName());
            System.out.println("🎭 Role: " + user.getRole());
            System.out.println("🚀 Navigating to " + user.getRole() + " dashboard...\n");
            
            // Navigate based on role
            navigateToDashboard(user.getRole().toLowerCase(), username);
        } else {
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║        ❌ LOGIN FAILED - GUI LAYER              ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.println("⚠️ Authentication returned null");
            System.out.println("💡 Check the debug output above for details\n");
            showError("Invalid credentials. Please try again.");
        }
    }

    /**
     * Validate credentials using database (DEPRECATED - now using authService directly in handleLogin)
     * @param username the entered username
     * @param password the entered password
     * @return user role (admin/employee/customer) or null if invalid
     */
    @Deprecated
    private String validateCredentials(String username, String password) {
        User user = authService.login(username, password);
        return user != null ? user.getRole().toLowerCase() : null;
    }

    /**
     * Show error message
     * @param message the error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        
        // Add shake animation effect
        shakeCard();
    }

    /**
     * Hide error message
     */
    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    /**
     * Simple shake animation for the login card
     */
    private void shakeCard() {
        javafx.animation.TranslateTransition transition = 
            new javafx.animation.TranslateTransition(javafx.util.Duration.millis(100), loginCard);
        transition.setFromX(0);
        transition.setByX(10);
        transition.setCycleCount(4);
        transition.setAutoReverse(true);
        transition.play();
    }

    /**
     * Navigate to appropriate dashboard based on user role
     * @param role user role (admin/employee/customer)
     * @param username username for display
     */
    private void navigateToDashboard(String role, String username) {
        try {
            String fxmlPath;
            String dashboardTitle;
            
            // Determine which dashboard to load
            if (role.equals("admin")) {
                fxmlPath = "/fxml/admin_dashboard.fxml";
                dashboardTitle = "Stone Sales – Admin Dashboard";
                System.out.println("Navigating to Admin Dashboard...");
            } else if (role.equals("employee")) {
                fxmlPath = "/fxml/employee_dashboard.fxml";
                dashboardTitle = "Stone Sales – Employee Dashboard";
                System.out.println("Navigating to Employee Dashboard...");
            } else if (role.equals("customer")) {
                fxmlPath = "/fxml/customer_dashboard.fxml";
                dashboardTitle = "Stone Sales – Customer Portal";
                System.out.println("Navigating to Customer Dashboard...");
            } else {
                System.err.println("Unknown role: " + role);
                return;
            }
            
            // Load dashboard
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource(fxmlPath)
            );
            javafx.scene.Parent root = loader.load();
            
            // Get current stage (REUSE existing stage, don't create new one)
            javafx.stage.Stage stage = (javafx.stage.Stage) loginButton.getScene().getWindow();
            
            // Create new scene with dashboard (dynamic sizing)
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            
            // Load dashboard CSS
            String css = getClass().getResource("/css/dashboard.css").toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);
            
            // Set scene
            stage.setScene(scene);
            stage.setTitle(dashboardTitle);
            stage.setResizable(true);
            
            // Force exact screen size using visual bounds (no gaps on sides)
            javafx.application.Platform.runLater(() -> {
                javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
            });
            
            System.out.println(dashboardTitle + " loaded successfully at exact screen size with title bar");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading dashboard: " + e.getMessage());
            showError("Failed to load dashboard. Please try again.");
        }
    }

    /**
     * Handle signup link click - Navigate to sign-up page
     */
    @FXML
    private void handleSignupClick() {
        try {
            System.out.println("Navigating to sign-up page...");

            // Load sign-up screen
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/customer_signup_view.fxml")
            );
            javafx.scene.Parent root = loader.load();

            // Get current stage
            javafx.stage.Stage stage = (javafx.stage.Stage) signupLink.getScene().getWindow();

            // Create new scene with sign-up form
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            // Load login CSS (same styles used for signup)
            String css = getClass().getResource("/css/login.css").toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);

            // Set scene and maximize window (keeps title bar)
            stage.setScene(scene);
            stage.setTitle("Stone Sales – Create Account");
            stage.setResizable(true);
            
            // Force exact screen size using visual bounds (no gaps on sides)
            javafx.application.Platform.runLater(() -> {
                javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
            });

            System.out.println("Sign-up page loaded successfully at exact screen size with title bar");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading sign-up page: " + e.getMessage());
            showError("Failed to load sign-up page. Please try again.");
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

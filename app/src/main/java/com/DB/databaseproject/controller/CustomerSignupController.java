package com.DB.databaseproject.controller;

import com.DB.databaseproject.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.regex.Pattern;

/**
 * Controller for Customer Sign-Up Page
 * Stone Sales Management System
 */
public class CustomerSignupController {
    
    private final AuthenticationService authService = AuthenticationService.getInstance();

    @FXML
    private VBox signupCard;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField emailField;

    @FXML
    private Button registerButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    @FXML
    private Label backToLoginLink;

    // Email regex pattern for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        // Hide error and success labels initially
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        successLabel.setVisible(false);
        successLabel.setManaged(false);

        // Add Enter key listener to confirm password field
        confirmPasswordField.setOnAction(event -> handleRegister());

        System.out.println("Customer Sign-Up page initialized");
    }

    /**
     * Handle Register button click
     */
    @FXML
    private void handleRegister() {
        System.out.println("Register button clicked");

        // Hide previous messages
        hideMessages();

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // If validation passes, register the customer
        try {
            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String email = emailField.getText().trim();

            // Split full name into first, middle, and last name
            String[] nameParts = fullName.split("\\s+");
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String middleName = nameParts.length > 2 ? nameParts[1] : "";
            String lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";

            // Register customer using database service
            boolean success = authService.signupCustomer(
                username, password, email,
                firstName, middleName, lastName,
                phone, address
            );

            if (success) {
                System.out.println("===============================================");
                System.out.println("Customer registered successfully in database!");
                System.out.println("Full Name: " + fullName);
                System.out.println("Username: " + username);
                System.out.println("Phone: " + phone);
                System.out.println("Address: " + address);
                System.out.println("Email: " + (email.isEmpty() ? "Not provided" : email));
                System.out.println("===============================================");

                // Show success message
                showSuccess("Registration successful! Redirecting to login...");

                // Wait 2 seconds and redirect to login
                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(2000);
                        handleCancel(); // Redirect to login
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                showError("Registration failed. Username may already exist.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred during registration. Please try again.");
        }
    }

    /**
     * Handle Cancel button click - Return to login page
     */
    @FXML
    private void handleCancel() {
        try {
            System.out.println("Returning to login page...");

            // Load login screen
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
            );
            Parent root = loader.load();

            // Get current stage
            Stage stage = (Stage) cancelButton.getScene().getWindow();

            // Create new scene with login
            Scene scene = new Scene(root);

            // Load login CSS
            String css = getClass().getResource("/css/login.css").toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);

            // Set scene and maximize window (keeps title bar)
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
     * Validate all input fields
     * @return true if all validations pass, false otherwise
     */
    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        // Get field values
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String email = emailField.getText().trim();

        // 1. Check for empty required fields
        if (fullName.isEmpty()) {
            errors.append("• Full Name is required.\n");
        }

        if (username.isEmpty()) {
            errors.append("• Username is required.\n");
        }

        if (password.isEmpty()) {
            errors.append("• Password is required.\n");
        }

        if (confirmPassword.isEmpty()) {
            errors.append("• Confirm Password is required.\n");
        }

        if (phone.isEmpty()) {
            errors.append("• Phone Number is required.\n");
        }

        if (address.isEmpty()) {
            errors.append("• Address is required.\n");
        }

        // 2. Validate username (no spaces)
        if (!username.isEmpty() && username.contains(" ")) {
            errors.append("• Username cannot contain spaces.\n");
        }

        // 3. Validate password match
        if (!password.isEmpty() && !confirmPassword.isEmpty()) {
            if (!password.equals(confirmPassword)) {
                errors.append("• Passwords do not match.\n");
            }
        }

        // 4. Validate password length (minimum 6 characters)
        if (!password.isEmpty() && password.length() < 6) {
            errors.append("• Password must be at least 6 characters long.\n");
        }

        // 5. Validate phone (numeric only)
        if (!phone.isEmpty() && !phone.matches("\\d+")) {
            errors.append("• Phone Number must contain only digits.\n");
        }

        // 6. Validate phone length (at least 10 digits)
        if (!phone.isEmpty() && phone.matches("\\d+") && phone.length() < 10) {
            errors.append("• Phone Number must be at least 10 digits.\n");
        }

        // 7. Validate email format (if provided)
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("• Invalid email format.\n");
        }

        // If there are errors, show them
        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }

        return true;
    }

    /**
     * Show error message to user
     * @param message Error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        successLabel.setVisible(false);
        successLabel.setManaged(false);

        // Add shake animation effect
        shakeCard();
    }

    /**
     * Show success message to user
     * @param message Success message to display
     */
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        successLabel.setManaged(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    /**
     * Hide all messages
     */
    private void hideMessages() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }

    /**
     * Shake animation for signup card on error
     */
    private void shakeCard() {
        javafx.animation.TranslateTransition transition =
            new javafx.animation.TranslateTransition(
                javafx.util.Duration.millis(100), signupCard);
        transition.setFromX(0);
        transition.setByX(10);
        transition.setCycleCount(4);
        transition.setAutoReverse(true);
        transition.play();
    }

    /**
     * Clear all fields
     */
    @SuppressWarnings("unused")
    private void clearFields() {
        fullNameField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        phoneField.clear();
        addressField.clear();
        emailField.clear();
        hideMessages();
    }
}

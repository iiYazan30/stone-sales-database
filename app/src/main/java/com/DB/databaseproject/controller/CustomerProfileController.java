package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Customer;
import com.DB.databaseproject.model.User;
import com.DB.databaseproject.service.AuthenticationService;
import com.DB.databaseproject.util.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller for Customer Profile View (Editable)
 * Stone Sales Management System - Stone Premium Dark Theme
 * Linked with Database - Gets customer info from AuthenticationService
 */
public class CustomerProfileController {

    private final AuthenticationService authService = AuthenticationService.getInstance();
    
    // Store original values for cancel functionality
    private String originalFirstName;
    private String originalMiddleName;
    private String originalLastName;
    private String originalPhone;
    private String originalAddress;
    private String originalEmail;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Label nameLabel;

    @FXML
    private Label customerIdLabel;

    @FXML
    private Label customerIdValue;
    
    // Editable fields
    @FXML
    private TextField firstNameField;
    
    @FXML
    private TextField middleNameField;
    
    @FXML
    private TextField lastNameField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private TextField addressField;
    
    @FXML
    private TextField emailField;
    
    // Action buttons
    @FXML
    private Button editButton;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Customer Profile View initialized (EDITABLE)");

        // Load customer profile data from database
        loadCustomerProfile();

        // Load profile image
        loadProfileImage();
        
        System.out.println("Customer Profile View Loaded Successfully");
    }

    /**
     * Load customer profile data from database via AuthenticationService
     */
    private void loadCustomerProfile() {
        Customer currentCustomer = authService.getCurrentCustomer();
        User currentUser = authService.getCurrentUser();
        
        if (currentCustomer != null) {
            System.out.println("Loading customer profile from database...");
            System.out.println("Customer ID: " + currentCustomer.getCustomerId());
            
            // Load the three-name structure from database
            loadCustomerNamesFromDatabase(currentCustomer.getCustomerId());
            
            // Set read-only fields
            customerIdValue.setText(String.valueOf(currentCustomer.getCustomerId()));

            // Set labels in photo card
            updateNameLabel();
            customerIdLabel.setText("Customer ID: " + currentCustomer.getCustomerId());

            System.out.println("✅ Customer profile loaded from database (ID: " + currentCustomer.getCustomerId() + ")");
        } else {
            System.err.println("❌ No customer data found - user not logged in as customer!");
            
            // Show placeholder data if no customer is logged in
            firstNameField.setText("");
            middleNameField.setText("");
            lastNameField.setText("");
            phoneField.setText("");
            addressField.setText("");
            emailField.setText("");
            customerIdValue.setText("N/A");
            nameLabel.setText("No Customer Logged In");
            customerIdLabel.setText("Customer ID: N/A");
        }
    }
    
    /**
     * Load customer's three-name structure from database
     */
    private void loadCustomerNamesFromDatabase(int customerId) {
        String query = "SELECT c.\"First_Name\", c.\"Middle_Name\", c.\"Last_Name\", " +
                      "c.\"Phone_Number\", c.\"Address\", u.\"Email\" " +
                      "FROM \"Customer\" c " +
                      "LEFT JOIN \"User\" u ON c.\"User_ID\" = u.\"User_ID\" " +
                      "WHERE c.\"Customer_ID\" = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String firstName = rs.getString("First_Name");
                String middleName = rs.getString("Middle_Name");
                String lastName = rs.getString("Last_Name");
                String phone = rs.getString("Phone_Number");
                String address = rs.getString("Address");
                String email = rs.getString("Email");
                
                // Populate text fields with actual data
                firstNameField.setText(firstName != null ? firstName : "");
                middleNameField.setText(middleName != null ? middleName : "");
                lastNameField.setText(lastName != null ? lastName : "");
                phoneField.setText(phone != null ? phone : "");
                addressField.setText(address != null ? address : "");
                emailField.setText(email != null ? email : "");
                
                // Store original values for cancel functionality
                originalFirstName = firstNameField.getText();
                originalMiddleName = middleNameField.getText();
                originalLastName = lastNameField.getText();
                originalPhone = phoneField.getText();
                originalAddress = addressField.getText();
                originalEmail = emailField.getText();
                
                System.out.println("✅ Loaded name: " + firstName + " " + 
                    (middleName != null && !middleName.isEmpty() ? middleName + " " : "") + lastName);
                System.out.println("✅ Loaded phone: " + phone);
                System.out.println("✅ Loaded address: " + address);
                System.out.println("✅ Loaded email: " + email);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading customer names: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Update the name label in the photo card with current values
     */
    private void updateNameLabel() {
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        
        StringBuilder fullName = new StringBuilder();
        if (!firstName.isEmpty()) {
            fullName.append(firstName);
        }
        if (!middleName.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(middleName);
        }
        if (!lastName.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(lastName);
        }
        
        nameLabel.setText(fullName.length() > 0 ? fullName.toString() : "N/A");
    }

    /**
     * Load profile image
     */
    private void loadProfileImage() {
        try {
            // Try to load profile image from resources
            var imageStream = getClass().getResourceAsStream("/images/profile_placeholder.png");
            
            if (imageStream != null) {
                Image profileImage = new Image(imageStream);
                profileImageView.setImage(profileImage);
                System.out.println("Profile image loaded successfully");
            } else {
                System.out.println("Profile placeholder image not found. Using default.");
            }
        } catch (Exception e) {
            System.err.println("Error loading profile image: " + e.getMessage());
            // App continues to work even if image fails to load
        }
    }
    
    /**
     * Enable edit mode - makes fields editable
     */
    @FXML
    private void enableEditMode() {
        System.out.println("Enabling edit mode...");
        
        // Make text fields editable
        firstNameField.setEditable(true);
        middleNameField.setEditable(true);
        lastNameField.setEditable(true);
        phoneField.setEditable(true);
        addressField.setEditable(true);
        emailField.setEditable(true);
        
        // Update button visibility
        editButton.setVisible(false);
        editButton.setManaged(false);
        saveButton.setVisible(true);
        saveButton.setManaged(true);
        cancelButton.setVisible(true);
        cancelButton.setManaged(true);
        
        // Focus on first field
        firstNameField.requestFocus();
        
        System.out.println("✅ Edit mode enabled");
    }
    
    /**
     * Cancel edit - restore original values and disable edit mode
     */
    @FXML
    private void cancelEdit() {
        System.out.println("Canceling edit...");
        
        // Restore original values
        firstNameField.setText(originalFirstName);
        middleNameField.setText(originalMiddleName);
        lastNameField.setText(originalLastName);
        phoneField.setText(originalPhone);
        addressField.setText(originalAddress);
        emailField.setText(originalEmail);
        
        // Disable edit mode
        disableEditMode();
        
        System.out.println("✅ Edit canceled, original values restored");
    }
    
    /**
     * Disable edit mode - makes fields read-only
     */
    private void disableEditMode() {
        // Make text fields read-only
        firstNameField.setEditable(false);
        middleNameField.setEditable(false);
        lastNameField.setEditable(false);
        phoneField.setEditable(false);
        addressField.setEditable(false);
        emailField.setEditable(false);
        
        // Update button visibility
        editButton.setVisible(true);
        editButton.setManaged(true);
        saveButton.setVisible(false);
        saveButton.setManaged(false);
        cancelButton.setVisible(false);
        cancelButton.setManaged(false);
    }
    
    /**
     * Save profile - validate and update database
     */
    @FXML
    private void saveProfile() {
        System.out.println("Saving profile...");
        
        // Get current values
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String email = emailField.getText().trim();
        
        // Validate inputs
        if (firstName.isEmpty()) {
            showErrorAlert("Validation Error", "First Name is required.");
            firstNameField.requestFocus();
            return;
        }
        
        if (lastName.isEmpty()) {
            showErrorAlert("Validation Error", "Last Name is required.");
            lastNameField.requestFocus();
            return;
        }
        
        // Validate name length (max 15 characters each)
        if (firstName.length() > 15) {
            showErrorAlert("Validation Error", "First Name cannot exceed 15 characters.");
            firstNameField.requestFocus();
            return;
        }
        
        if (middleName.length() > 15) {
            showErrorAlert("Validation Error", "Middle Name cannot exceed 15 characters.");
            middleNameField.requestFocus();
            return;
        }
        
        if (lastName.length() > 15) {
            showErrorAlert("Validation Error", "Last Name cannot exceed 15 characters.");
            lastNameField.requestFocus();
            return;
        }
        
        // Validate phone (digits only, 9-12 characters)
        if (!phone.isEmpty() && !phone.matches("\\d{9,12}")) {
            showErrorAlert("Validation Error", "Phone must contain only digits and be 9-12 characters long.");
            phoneField.requestFocus();
            return;
        }
        
        // Validate address
        if (address.isEmpty()) {
            showErrorAlert("Validation Error", "Address is required.");
            addressField.requestFocus();
            return;
        }
        
        // Validate email (simple pattern)
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showErrorAlert("Validation Error", "Please enter a valid email address.");
            emailField.requestFocus();
            return;
        }
        
        // Update database
        Customer currentCustomer = authService.getCurrentCustomer();
        User currentUser = authService.getCurrentUser();
        
        if (currentCustomer == null) {
            showErrorAlert("Error", "No customer logged in!");
            return;
        }
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Update Customer table
            String updateCustomerQuery = "UPDATE \"Customer\" SET \"First_Name\" = ?, \"Middle_Name\" = ?, " +
                                        "\"Last_Name\" = ?, \"Phone_Number\" = ?, \"Address\" = ? " +
                                        "WHERE \"Customer_ID\" = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateCustomerQuery)) {
                stmt.setString(1, firstName);
                stmt.setString(2, middleName.isEmpty() ? null : middleName);
                stmt.setString(3, lastName);
                stmt.setString(4, phone);
                stmt.setString(5, address);
                stmt.setInt(6, currentCustomer.getCustomerId());
                
                int rowsUpdated = stmt.executeUpdate();
                
                if (rowsUpdated > 0) {
                    System.out.println("✅ Customer table updated successfully");
                } else {
                    conn.rollback();
                    showErrorAlert("Error", "Failed to update customer profile.");
                    return;
                }
            }
            
            // Update User table (email)
            if (currentUser != null && !email.isEmpty()) {
                String updateUserQuery = "UPDATE \"User\" SET \"Email\" = ? WHERE \"User_ID\" = ?";
                
                try (PreparedStatement stmt = conn.prepareStatement(updateUserQuery)) {
                    stmt.setString(1, email);
                    stmt.setInt(2, currentUser.getUserId());
                    
                    stmt.executeUpdate();
                    System.out.println("✅ User table updated successfully");
                }
            }
            
            conn.commit(); // Commit transaction
            System.out.println("✅ Profile updated successfully in database");
            
            // Update original values
            originalFirstName = firstName;
            originalMiddleName = middleName;
            originalLastName = lastName;
            originalPhone = phone;
            originalAddress = address;
            originalEmail = email;
            
            // Update name label
            updateNameLabel();
            
            // Disable edit mode
            disableEditMode();
            
            // Show success message
            showSuccessAlert("Success", "Your profile has been updated successfully!");
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating profile: " + e.getMessage());
            e.printStackTrace();
            
            // Rollback on error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            showErrorAlert("Database Error", "An error occurred while saving your profile: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Show error alert dialog
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show success alert dialog
     */
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

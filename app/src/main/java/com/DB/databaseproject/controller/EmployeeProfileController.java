package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Employee;
import com.DB.databaseproject.service.AuthenticationService;
import com.DB.databaseproject.util.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller for Employee Profile View (Editable)
 * Stone Sales Management System - Stone Premium Dark Theme
 * Linked with Database - Gets employee info from AuthenticationService
 */
public class EmployeeProfileController {

    private final AuthenticationService authService = AuthenticationService.getInstance();
    
    // Store original values for cancel functionality
    private String originalFirstName;
    private String originalMiddleName;
    private String originalLastName;
    private String originalPhone;
    private String originalAddress;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Label nameLabel;

    @FXML
    private Label employeeIdLabel;
    
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
    private TextArea addressArea;

    // Read-only fields
    @FXML
    private Label employeeIdValue;

    @FXML
    private Label dateHiredValue;

    @FXML
    private Label departmentValue;

    @FXML
    private Label salaryValue;
    
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
        System.out.println("Employee Profile View initialized (READ-ONLY)");

        // Load employee profile data from database
        loadEmployeeProfile();

        // Load profile image
        loadProfileImage();
        
        // Confirm successful loading
        System.out.println("Employee Profile View Loaded Successfully");
    }

    /**
     * Load employee profile data from database via AuthenticationService
     */
    private void loadEmployeeProfile() {
        Employee currentEmployee = authService.getCurrentEmployee();
        
        if (currentEmployee != null) {
            System.out.println("Loading employee profile from database...");
            System.out.println("Employee ID: " + currentEmployee.getEmployeeId());
            
            // Load the three-name structure from database
            loadEmployeeNamesFromDatabase(currentEmployee.getEmployeeId());
            
            // Set read-only fields
            employeeIdValue.setText(String.valueOf(currentEmployee.getEmployeeId()));
            
            // Date Hired - format properly
            if (currentEmployee.getDateHired() != null) {
                dateHiredValue.setText(currentEmployee.getDateHired().toString());
            } else {
                dateHiredValue.setText("N/A");
            }
            
            // Department/Position
            departmentValue.setText(currentEmployee.getPosition() != null && !currentEmployee.getPosition().isEmpty() 
                ? currentEmployee.getPosition() 
                : "N/A");
            
            // Salary - format as currency
            salaryValue.setText(String.format("$%.2f", currentEmployee.getSalary()));

            // Set labels in photo card
            updateNameLabel();
            employeeIdLabel.setText("Employee ID: " + currentEmployee.getEmployeeId());

            System.out.println("✅ Employee profile loaded from database (ID: " + currentEmployee.getEmployeeId() + ")");
        } else {
            System.err.println("❌ No employee data found - user not logged in as employee!");
            
            // Show placeholder data if no employee is logged in
            firstNameField.setText("");
            middleNameField.setText("");
            lastNameField.setText("");
            phoneField.setText("");
            addressArea.setText("");
            employeeIdValue.setText("N/A");
            dateHiredValue.setText("N/A");
            departmentValue.setText("N/A");
            salaryValue.setText("N/A");
            nameLabel.setText("No Employee Logged In");
            employeeIdLabel.setText("Employee ID: N/A");
        }
    }
    
    /**
     * Load employee's three-name structure from database
     */
    private void loadEmployeeNamesFromDatabase(int employeeId) {
        String query = "SELECT \"First_Name\", \"Middle_Name\", \"Last_Name\", \"Phone_Number\", \"Address\" " +
                      "FROM \"Employee\" WHERE \"Employee_ID\" = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String firstName = rs.getString("First_Name");
                String middleName = rs.getString("Middle_Name");
                String lastName = rs.getString("Last_Name");
                String phone = rs.getString("Phone_Number");
                String address = rs.getString("Address");
                
                // Populate text fields with actual data (NOT promptText)
                firstNameField.setText(firstName != null ? firstName : "");
                middleNameField.setText(middleName != null ? middleName : "");
                lastNameField.setText(lastName != null ? lastName : "");
                phoneField.setText(phone != null ? phone : "");
                addressArea.setText(address != null ? address : "");
                
                // Store original values for cancel functionality
                originalFirstName = firstNameField.getText();
                originalMiddleName = middleNameField.getText();
                originalLastName = lastNameField.getText();
                originalPhone = phoneField.getText();
                originalAddress = addressArea.getText();
                
                System.out.println("✅ Loaded name: " + firstName + " " + 
                    (middleName != null && !middleName.isEmpty() ? middleName + " " : "") + lastName);
                System.out.println("✅ Loaded phone: " + phone);
                System.out.println("✅ Loaded address: " + address);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading employee names: " + e.getMessage());
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
                // Could set a default image here if needed
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
        addressArea.setEditable(true);
        
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
        addressArea.setText(originalAddress);
        
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
        addressArea.setEditable(false);
        
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
        String address = addressArea.getText().trim();
        
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
            addressArea.requestFocus();
            return;
        }
        
        // Update database
        Employee currentEmployee = authService.getCurrentEmployee();
        if (currentEmployee == null) {
            showErrorAlert("Error", "No employee logged in!");
            return;
        }
        
        String updateQuery = "UPDATE \"Employee\" SET \"First_Name\" = ?, \"Middle_Name\" = ?, " +
                           "\"Last_Name\" = ?, \"Phone_Number\" = ?, \"Address\" = ? WHERE \"Employee_ID\" = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            
            stmt.setString(1, firstName);
            stmt.setString(2, middleName.isEmpty() ? null : middleName);
            stmt.setString(3, lastName);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setInt(6, currentEmployee.getEmployeeId());
            
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("✅ Profile updated successfully in database");
                
                // Update original values
                originalFirstName = firstName;
                originalMiddleName = middleName;
                originalLastName = lastName;
                originalPhone = phone;
                originalAddress = address;
                
                // Update name label
                updateNameLabel();
                
                // Disable edit mode
                disableEditMode();
                
                // Show success message
                showSuccessAlert("Success", "Your profile has been updated successfully!");
            } else {
                showErrorAlert("Error", "Failed to update profile. Please try again.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating profile: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Database Error", "An error occurred while saving your profile: " + e.getMessage());
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

package com.DB.databaseproject.controller;

import com.DB.databaseproject.dao.EmployeeDAO;
import com.DB.databaseproject.dao.UserDAO;
import com.DB.databaseproject.model.Employee;
import com.DB.databaseproject.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Controller for Employee Form (Add/Edit)
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class EmployeeFormController {

    @FXML private Label formTitle;
    
    // User Account Fields
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField visibleConfirmPasswordField;
    @FXML private Button togglePasswordButton;
    @FXML private Button toggleConfirmPasswordButton;
    @FXML private CheckBox changePasswordCheckBox;
    
    // Personal Information Fields
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField addressField;
    
    // Employment Information Fields
    @FXML private TextField salaryField;
    @FXML private DatePicker dateHiredPicker;
    
    // Action Buttons
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    // DAOs
    private final UserDAO userDAO = new UserDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    
    // Edit mode tracking
    private boolean isEditMode = false;
    private int editEmployeeId = -1;
    private int editUserId = -1;
    
    // Callback to refresh parent table
    private Consumer<Boolean> onSaveCallback;

    /**
     * Initialize method - called after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Employee Form initialized");
        
        // Set default date to today
        dateHiredPicker.setValue(LocalDate.now());
        
        // Add input validation listeners
        setupValidationListeners();
        
        // Sync password fields for toggle functionality
        setupPasswordFieldSync();
    }

    /**
     * Setup password field synchronization for visibility toggle
     */
    private void setupPasswordFieldSync() {
        // Sync password field with visible password field
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordField.isVisible()) {
                visiblePasswordField.setText(newValue);
            }
        });
        
        visiblePasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (visiblePasswordField.isVisible()) {
                passwordField.setText(newValue);
            }
        });
        
        // Sync confirm password field with visible confirm password field
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (confirmPasswordField.isVisible()) {
                visibleConfirmPasswordField.setText(newValue);
            }
        });
        
        visibleConfirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (visibleConfirmPasswordField.isVisible()) {
                confirmPasswordField.setText(newValue);
            }
        });
    }

    /**
     * Toggle password field visibility
     */
    @FXML
    private void togglePasswordVisibility() {
        if (passwordField.isVisible()) {
            // Switch to visible mode
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.positionCaret(passwordField.getCaretPosition());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            togglePasswordButton.setText("🙈"); // Eye closed
        } else {
            // Switch to hidden mode
            passwordField.setText(visiblePasswordField.getText());
            passwordField.positionCaret(visiblePasswordField.getCaretPosition());
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            togglePasswordButton.setText("👁"); // Eye open
        }
    }

    /**
     * Toggle confirm password field visibility
     */
    @FXML
    private void toggleConfirmPasswordVisibility() {
        if (confirmPasswordField.isVisible()) {
            // Switch to visible mode
            visibleConfirmPasswordField.setText(confirmPasswordField.getText());
            visibleConfirmPasswordField.positionCaret(confirmPasswordField.getCaretPosition());
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            visibleConfirmPasswordField.setVisible(true);
            visibleConfirmPasswordField.setManaged(true);
            toggleConfirmPasswordButton.setText("🙈"); // Eye closed
        } else {
            // Switch to hidden mode
            confirmPasswordField.setText(visibleConfirmPasswordField.getText());
            confirmPasswordField.positionCaret(visibleConfirmPasswordField.getCaretPosition());
            visibleConfirmPasswordField.setVisible(false);
            visibleConfirmPasswordField.setManaged(false);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            toggleConfirmPasswordButton.setText("👁"); // Eye open
        }
    }

    /**
     * Set up real-time validation listeners
     */
    private void setupValidationListeners() {
        // Phone number - only digits
        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                phoneNumberField.setText(oldValue);
            }
            if (newValue.length() > 10) {
                phoneNumberField.setText(newValue.substring(0, 10));
            }
        });
        
        // Salary - only numbers and decimal point
        salaryField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                salaryField.setText(oldValue);
            }
        });
    }

    /**
     * Set callback for when save is successful
     */
    public void setOnSaveCallback(Consumer<Boolean> callback) {
        this.onSaveCallback = callback;
    }

    /**
     * Configure form for ADD mode
     */
    public void setAddMode() {
        isEditMode = false;
        formTitle.setText("Add New Employee");
        passwordField.setDisable(false);
        confirmPasswordField.setDisable(false);
        visiblePasswordField.setDisable(false);
        visibleConfirmPasswordField.setDisable(false);
        usernameField.setDisable(false);
        togglePasswordButton.setDisable(false);
        toggleConfirmPasswordButton.setDisable(false);
        
        // Hide checkbox in add mode
        if (changePasswordCheckBox != null) {
            changePasswordCheckBox.setVisible(false);
            changePasswordCheckBox.setManaged(false);
        }
        
        System.out.println("Form configured for ADD mode");
    }

    /**
     * Configure form for EDIT mode
     */
    public void setEditMode(Employee employee, int userId) {
        isEditMode = true;
        editEmployeeId = employee.getEmployeeId();
        editUserId = userId;
        
        formTitle.setText("Edit Employee");
        
        System.out.println("Form configured for EDIT mode");
        System.out.println("  Employee ID: " + editEmployeeId);
        System.out.println("  User ID: " + editUserId);
        
        // Load employee data (including password)
        loadEmployeeData(employee, userId);
        
        // Disable username (cannot be changed)
        usernameField.setDisable(true);
        
        // Hide checkbox in edit mode - password is now always editable
        if (changePasswordCheckBox != null) {
            changePasswordCheckBox.setVisible(false);
            changePasswordCheckBox.setManaged(false);
        }
        
        // Enable password fields for editing
        passwordField.setDisable(false);
        confirmPasswordField.setDisable(false);
        visiblePasswordField.setDisable(false);
        visibleConfirmPasswordField.setDisable(false);
        togglePasswordButton.setDisable(false);
        toggleConfirmPasswordButton.setDisable(false);
        
        // Set prompt text for password fields
        passwordField.setPromptText("Enter password");
        confirmPasswordField.setPromptText("Confirm password");
    }

    /**
     * Load employee data into form fields
     */
    private void loadEmployeeData(Employee employee, int userId) {
        try {
            // Get user data
            User user = userDAO.getById(userId);
            
            if (user != null) {
                // User account fields
                usernameField.setText(user.getUserName());
                
                // Load password from database and populate fields
                String currentPassword = user.getPassword();
                if (currentPassword != null && !currentPassword.isEmpty()) {
                    // Set password in hidden field (PasswordField)
                    passwordField.setText(currentPassword);
                    confirmPasswordField.setText(currentPassword);
                    
                    // Also sync to visible fields (but they're hidden initially)
                    visiblePasswordField.setText(currentPassword);
                    visibleConfirmPasswordField.setText(currentPassword);
                    
                    System.out.println("✅ Password loaded from database");
                }
                
                // Personal information
                firstNameField.setText(user.getFirstName());
                middleNameField.setText(user.getMiddleName());
                lastNameField.setText(user.getLastName());
                emailField.setText(user.getEmail());
                phoneNumberField.setText(user.getPhoneNumber());
                addressField.setText(user.getAddress());
            }
            
            // Employment information
            salaryField.setText(String.valueOf(employee.getSalary()));
            dateHiredPicker.setValue(employee.getDateHired());
            
            System.out.println("✅ Employee data loaded into form");
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading employee data: " + e.getMessage());
            showError("Failed to load employee data", e.getMessage());
        }
    }

    /**
     * Handle Save button click
     */
    @FXML
    private void handleSave() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         EMPLOYEE FORM - SAVE OPERATION                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Mode: " + (isEditMode ? "EDIT" : "ADD"));
        
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        if (isEditMode) {
            updateEmployee();
        } else {
            addNewEmployee();
        }
    }

    /**
     * Validate all input fields
     */
    private boolean validateInput() {
        System.out.println("🔍 Validating input...");
        
        StringBuilder errors = new StringBuilder();
        
        // Username validation (only for add mode)
        if (!isEditMode && usernameField.getText().trim().isEmpty()) {
            errors.append("• Username is required\n");
        }
        
        // Password validation (for both add and edit modes)
        String password = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();
        String confirmPassword = confirmPasswordField.isVisible() ? confirmPasswordField.getText() : visibleConfirmPasswordField.getText();
        
        if (password.isEmpty()) {
            errors.append("• Password is required\n");
        } else {
            if (password.length() < 6) {
                errors.append("• Password must be at least 6 characters\n");
            }
            if (password.contains(" ")) {
                errors.append("• Password cannot contain spaces\n");
            }
            if (!password.equals(confirmPassword)) {
                errors.append("• Passwords do not match\n");
            }
        }
        
        // Required fields
        if (firstNameField.getText().trim().isEmpty()) {
            errors.append("• First Name is required\n");
        }
        if (lastNameField.getText().trim().isEmpty()) {
            errors.append("• Last Name is required\n");
        }
        if (emailField.getText().trim().isEmpty()) {
            errors.append("• Email is required\n");
        } else if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.append("• Email format is invalid\n");
        }
        
        // Phone number validation
        String phone = phoneNumberField.getText().trim();
        if (phone.isEmpty()) {
            errors.append("• Phone Number is required\n");
        } else if (phone.length() != 10) {
            errors.append("• Phone Number must be exactly 10 digits\n");
        }
        
        // Salary validation
        String salaryText = salaryField.getText().trim();
        if (salaryText.isEmpty()) {
            errors.append("• Salary is required\n");
        } else {
            try {
                double salary = Double.parseDouble(salaryText);
                if (salary < 0) {
                    errors.append("• Salary must be a positive number\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Salary must be a valid number\n");
            }
        }
        
        // Date validation
        if (dateHiredPicker.getValue() == null) {
            errors.append("• Date Hired is required\n");
        } else if (dateHiredPicker.getValue().isAfter(LocalDate.now())) {
            errors.append("• Date Hired cannot be in the future\n");
        }
        
        if (errors.length() > 0) {
            System.err.println("❌ Validation failed:");
            System.err.println(errors.toString());
            showError("Validation Error", errors.toString());
            return false;
        }
        
        System.out.println("✅ Validation passed");
        return true;
    }

    /**
     * Add new employee (INSERT mode)
     */
    private void addNewEmployee() {
        System.out.println("\n📋 ADD NEW EMPLOYEE - Starting...");
        
        try {
            // Step 1: Create User object
            User user = new User();
            user.setUserName(usernameField.getText().trim());
            user.setPassword(passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText());
            user.setPhoneNumber(phoneNumberField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setFirstName(firstNameField.getText().trim());
            user.setMiddleName(middleNameField.getText().trim());
            user.setLastName(lastNameField.getText().trim());
            user.setRole("Employee");
            user.setAddress(addressField.getText().trim());
            
            System.out.println("📝 User object created:");
            System.out.println("   Username: " + user.getUserName());
            System.out.println("   Role: " + user.getRole());
            System.out.println("   Role: " + user.getRole());
            System.out.println("   Name: " + user.getFirstName() + " " + user.getLastName());
            
            // Step 2: Insert into User table
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║  📋 STEP 1: Inserting into User table                      ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            int userId = userDAO.insert(user);
            
            if (userId <= 0) {
                System.err.println("\n❌ ❌ ❌ EMPLOYEE ADD ERROR - STEP 1 FAILED ❌ ❌ ❌");
                System.err.println("   Failed to insert user - no User_ID returned");
                System.err.println("╚════════════════════════════════════════════════════════════╝\n");
                showError("Error", "Failed to create user account. Please try again.");
                return;
            }
            
            System.out.println("\n✅ ✅ STEP 1 SUCCESSFUL! ✅ ✅");
            System.out.println("   User created with ID: " + userId);
            
            // Step 3: Create Employee object
            String fullName = firstNameField.getText().trim() + " " +
                            (middleNameField.getText().trim().isEmpty() ? "" : middleNameField.getText().trim() + " ") +
                            lastNameField.getText().trim();
            
            Employee employee = new Employee();
            employee.setFullName(fullName);
            employee.setPhone(phoneNumberField.getText().trim());
            employee.setAddress(addressField.getText().trim());
            employee.setSalary(Double.parseDouble(salaryField.getText().trim()));
            employee.setDateHired(dateHiredPicker.getValue());
            
            System.out.println("\n📝 Employee object created:");
            System.out.println("   Full Name: " + fullName);
            System.out.println("   Salary: " + employee.getSalary());
            System.out.println("   Date Hired: " + employee.getDateHired());
            
            // Step 4: Insert into Employee table
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║  📋 STEP 2: Inserting into Employee table                  ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            int employeeId = employeeDAO.insert(employee, userId);
            
            if (employeeId <= 0) {
                System.err.println("\n❌ ❌ ❌ EMPLOYEE ADD ERROR - STEP 2 FAILED ❌ ❌ ❌");
                System.err.println("   Failed to insert employee");
                System.err.println("   User account created (User_ID: " + userId + ") but employee record failed");
                System.err.println("╚════════════════════════════════════════════════════════════╝\n");
                showError("Error", "Failed to create employee record. User account created but employee record failed.");
                return;
            }
            
            System.out.println("\n✅ ✅ STEP 2 SUCCESSFUL! ✅ ✅");
            System.out.println("   Employee created with ID: " + employeeId);
            System.out.println("✅ Employee created successfully with ID: " + employeeId);
            System.out.println("\n✅ ✅ ✅ ADD EMPLOYEE SUCCESSFUL! ✅ ✅ ✅");
            System.out.println("   User_ID: " + userId);
            System.out.println("   Employee_ID: " + employeeId);
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            
            // Show success message
            showSuccess("Employee Added", 
                       "Employee " + fullName + " has been successfully added to the system.");
            
            // Callback to refresh parent table
            if (onSaveCallback != null) {
                onSaveCallback.accept(true);
            }
            
            // Close the form
            closeForm();
            
        } catch (SQLException e) {
            System.err.println("\n❌ ❌ ❌ DATABASE ERROR! ❌ ❌ ❌");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("╚════════════════════════════════════════════════════════════╝\n");
            e.printStackTrace();
            showError("Database Error", "Failed to add employee:\n" + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n❌ ❌ ❌ UNEXPECTED ERROR! ❌ ❌ ❌");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("╚════════════════════════════════════════════════════════════╝\n");
            e.printStackTrace();
            showError("Error", "An unexpected error occurred:\n" + e.getMessage());
        }
    }

    /**
     * Update existing employee (EDIT mode)
     */
    private void updateEmployee() {
        System.out.println("\n📋 UPDATE EMPLOYEE - Starting...");
        System.out.println("   Employee_ID: " + editEmployeeId);
        System.out.println("   User_ID: " + editUserId);
        
        try {
            // Step 1: Update User table
            User user = new User();
            user.setUserId(editUserId);
            user.setUserName(usernameField.getText().trim()); // Won't actually update (field is disabled)
            
            // Get password from visible or hidden field
            String password = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();
            user.setPassword(password);
            System.out.println("🔐 Password will be updated");
            
            user.setPhoneNumber(phoneNumberField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setFirstName(firstNameField.getText().trim());
            user.setMiddleName(middleNameField.getText().trim());
            user.setLastName(lastNameField.getText().trim());
            user.setRole("Employee");
            user.setAddress(addressField.getText().trim());
            
            System.out.println("\n📋 Step 1: Updating User table...");
            boolean userUpdated = userDAO.update(user, true); // Always update password
            
            if (!userUpdated) {
                System.err.println("❌ Failed to update user");
                showError("Error", "Failed to update user information.");
                return;
            }
            
            System.out.println("✅ User updated successfully");
            
            // Step 2: Update Employee table
            String fullName = firstNameField.getText().trim() + " " +
                            (middleNameField.getText().trim().isEmpty() ? "" : middleNameField.getText().trim() + " ") +
                            lastNameField.getText().trim();
            
            Employee employee = new Employee();
            employee.setEmployeeId(editEmployeeId);
            employee.setFullName(fullName);
            employee.setPhone(phoneNumberField.getText().trim());
            employee.setAddress(addressField.getText().trim());
            employee.setSalary(Double.parseDouble(salaryField.getText().trim()));
            employee.setDateHired(dateHiredPicker.getValue());
            
            System.out.println("\n📋 Step 2: Updating Employee table...");
            boolean employeeUpdated = employeeDAO.update(employee);
            
            if (!employeeUpdated) {
                System.err.println("❌ Failed to update employee");
                showError("Error", "Failed to update employee information.");
                return;
            }
            
            System.out.println("✅ Employee updated successfully");
            System.out.println("\n✅ ✅ ✅ UPDATE EMPLOYEE SUCCESSFUL! ✅ ✅ ✅");
            System.out.println("   Employee_ID: " + editEmployeeId);
            System.out.println("   User_ID: " + editUserId);
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            
            // Show success message
            showSuccess("Employee Updated", 
                       "Employee " + fullName + " has been successfully updated.");
            
            // Callback to refresh parent table
            if (onSaveCallback != null) {
                onSaveCallback.accept(true);
            }
            
            // Close the form
            closeForm();
            
        } catch (SQLException e) {
            System.err.println("\n❌ ❌ ❌ DATABASE ERROR! ❌ ❌ ❌");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("╚════════════════════════════════════════════════════════════╝\n");
            e.printStackTrace();
            showError("Database Error", "Failed to update employee:\n" + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n❌ ❌ ❌ UNEXPECTED ERROR! ❌ ❌ ❌");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("╚════════════════════════════════════════════════════════════╝\n");
            e.printStackTrace();
            showError("Error", "An unexpected error occurred:\n" + e.getMessage());
        }
    }

    /**
     * Handle Cancel button click
     */
    @FXML
    private void handleCancel() {
        System.out.println("❌ User cancelled the form");
        closeForm();
    }

    /**
     * Close the form window
     */
    private void closeForm() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Show error alert with custom styling
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2b2b2b;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #ffffff;");
        
        alert.showAndWait();
    }

    /**
     * Show success alert with custom styling
     */
    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("✅ Success");
        alert.setContentText(content);
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2b2b2b;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #ffffff;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #d4c59e;");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: #2b2b2b;");
        
        alert.showAndWait();
    }
}

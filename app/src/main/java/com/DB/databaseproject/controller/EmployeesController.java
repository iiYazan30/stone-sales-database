package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Employee;
import com.DB.databaseproject.service.EmployeeService;
import com.DB.databaseproject.util.CustomDialogs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Controller for Employees Management View
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class EmployeesController {
    
    private final EmployeeService employeeService = EmployeeService.getInstance();

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Employee> employeesTable;

    @FXML
    private TableColumn<Employee, Integer> idColumn;

    @FXML
    private TableColumn<Employee, String> nameColumn;

    @FXML
    private TableColumn<Employee, String> phoneColumn;

    @FXML
    private TableColumn<Employee, String> addressColumn;

    @FXML
    private TableColumn<Employee, Double> salaryColumn;

    @FXML
    private TableColumn<Employee, LocalDate> dateHiredColumn;

    @FXML
    private TableColumn<Employee, Void> actionsColumn;

    private ObservableList<Employee> employeesList;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Employees Management View initialized");

        // Initialize the employees list
        employeesList = FXCollections.observableArrayList();

        // Configure table columns (including Actions column with Edit/Delete buttons)
        setupTableColumns();

        // Load sample data
        loadSampleData();

        // Set the data to the table ONCE - we never change this reference
        employeesTable.setItems(employeesList);

        // Placeholder message when table is empty
        employeesTable.setPlaceholder(new Label("No employees found"));

        // Make table responsive - columns auto-resize to fill available width
        employeesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        employeesTable.setPrefWidth(Double.MAX_VALUE);
        
        // Setup search field listener for real-time filtering
        setupSearchListener();
    }
    
    /**
     * Setup search field listener for real-time filtering
     * This ensures Edit/Delete buttons remain visible during search
     */
    private void setupSearchListener() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                // Automatically trigger search when text changes
                onSearchEmployee();
            });
        }
    }

    /**
     * Setup table columns with cell value factories
     */
    private void setupTableColumns() {
        // Employee ID Column
        idColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        // Full Name Column
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        // Phone Column
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Address Column
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Salary Column
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salaryColumn.setCellFactory(column -> new TableCell<Employee, Double>() {
            @Override
            protected void updateItem(Double salary, boolean empty) {
                super.updateItem(salary, empty);
                if (empty || salary == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", salary));
                }
            }
        });

        // Date Hired Column
        dateHiredColumn.setCellValueFactory(new PropertyValueFactory<>("dateHired"));

        // Actions Column (Edit and Delete buttons)
        setupActionsColumn();
    }

    /**
     * Setup the Actions column with Edit and Delete buttons
     */
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<Employee, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox actionButtons = new HBox(8, editButton, deleteButton);

            {
                // Style the buttons
                editButton.getStyleClass().add("btn-edit");
                deleteButton.getStyleClass().add("btn-delete");
                
                // Set minimum widths to prevent text truncation
                editButton.setMinWidth(70);
                deleteButton.setMinWidth(70);

                // Center the buttons
                actionButtons.setAlignment(Pos.CENTER);

                // Set button actions
                editButton.setOnAction(event -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    onEditEmployee(employee);
                });

                deleteButton.setOnAction(event -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    onDeleteEmployee(employee);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });
    }

    /**
     * Load sample employee data (temporary for UI preview)
     * Later this will be replaced with PostgreSQL data
     */
    private void loadSampleData() {
        // Load employees from database
        employeesList.clear();
        employeesList.addAll(employeeService.getAllEmployees());
        
        System.out.println("Employees loaded from database: " + employeesList.size());
    }

    /**
     * Handle Add Employee button click
     */
    @FXML
    private void onAddEmployee() {
        System.out.println("Add Employee button clicked");
        openEmployeeForm(null);
    }

    /**
     * Handle Edit Employee button click
     * @param employee The employee to edit
     */
    private void onEditEmployee(Employee employee) {
        System.out.println("Edit Employee: " + employee.getFullName());
        openEmployeeForm(employee);
    }

    /**
     * Handle Delete Employee button click
     * Deletes both Employee and User records from database
     * @param employee The employee to delete
     */
    private void onDeleteEmployee(Employee employee) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         DELETE EMPLOYEE - CONTROLLER LAYER                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("🗑️  Delete Employee Request: " + employee.getFullName());
        System.out.println("📝 Employee ID: " + employee.getEmployeeId());
        
        // Show custom themed confirmation dialog
        Stage ownerStage = (Stage) employeesTable.getScene().getWindow();
        boolean confirmed = CustomDialogs.showDeleteConfirmation(
            ownerStage, 
            employee.getFullName(), 
            employee.getEmployeeId()
        );
        
        if (confirmed) {
            System.out.println("✅ User confirmed deletion");
            System.out.println("🔄 Calling EmployeeService.deleteEmployee()...\n");
            
            try {
                // Call service to delete employee and user
                boolean success = employeeService.deleteEmployee(employee.getEmployeeId());
                
                    if (success) {
                        // Success - Remove from table
                        employeesList.remove(employee);
                        
                        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                        System.out.println("║         ✅ DELETION SUCCESSFUL - UI UPDATED                 ║");
                        System.out.println("╚════════════════════════════════════════════════════════════╝");
                        System.out.println("✅ Employee removed from table view");
                        System.out.println("✅ " + employee.getFullName() + " has been deleted");
                        
                        // Show custom themed success dialog
                        CustomDialogs.showDeleteSuccess(ownerStage, employee.getFullName(), employee.getEmployeeId());
                        
                        // Refresh the table to ensure consistency
                        refreshEmployeeTable();
                        
                    } else {
                    // Failure
                    System.err.println("\n╔════════════════════════════════════════════════════════════╗");
                    System.err.println("║         ❌ DELETION FAILED                                  ║");
                    System.err.println("╚════════════════════════════════════════════════════════════╝");
                    System.err.println("❌ EmployeeService.deleteEmployee() returned false");
                    System.err.println("   Employee: " + employee.getFullName());
                    System.err.println("   Employee ID: " + employee.getEmployeeId());
                    
                    // Show error alert
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.setHeaderText("❌ Failed to Delete Employee");
                    errorAlert.setContentText(
                        "Could not delete employee: " + employee.getFullName() + "\n\n" +
                        "Possible reasons:\n" +
                        "• Employee ID not found in database\n" +
                        "• User account deletion failed\n" +
                        "• Database connection issue\n" +
                        "• Foreign key constraints\n\n" +
                        "Please check the console for detailed error messages."
                    );
                    errorAlert.showAndWait();
                }
                
            } catch (Exception e) {
                // Exception occurred
                System.err.println("\n╔════════════════════════════════════════════════════════════╗");
                System.err.println("║         ❌ EXCEPTION DURING DELETION                        ║");
                System.err.println("╚════════════════════════════════════════════════════════════╝");
                System.err.println("❌ Exception: " + e.getClass().getSimpleName());
                System.err.println("❌ Message: " + e.getMessage());
                e.printStackTrace();
                
                // Show exception alert
                Alert exceptionAlert = new Alert(Alert.AlertType.ERROR);
                exceptionAlert.setTitle("Deletion Error");
                exceptionAlert.setHeaderText("❌ Unexpected Error During Deletion");
                exceptionAlert.setContentText(
                    "An unexpected error occurred while deleting the employee.\n\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "Please contact your system administrator.\n" +
                    "Check the console for full stack trace."
                );
                exceptionAlert.showAndWait();
            }
        } else {
            System.out.println("❌ User cancelled deletion");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        }
    }

    /**
     * Refresh the employee table by reloading data from database
     */
    private void refreshEmployeeTable() {
        System.out.println("\n🔄 Refreshing employee table from database...");
        employeesList.clear();
        employeesList.addAll(employeeService.getAllEmployees());
        System.out.println("✅ Table refreshed: " + employeesList.size() + " employees loaded\n");
        
        // Re-apply search filter if search field has text
        if (searchField != null && !searchField.getText().trim().isEmpty()) {
            onSearchEmployee();
        }
    }

    /**
     * Handle Search Employee button click
     * FIXED: Only filters the data, does NOT change table columns or cellFactory
     */
    @FXML
    private void onSearchEmployee() {
        String searchText = searchField.getText().trim();
        System.out.println("Search Employee: " + searchText);
        
        if (searchText.isEmpty()) {
            // Clear search - reload all employees from database and show them
            System.out.println("🔄 Search cleared - reloading all employees from database...");
            employeesList.clear();
            employeesList.addAll(employeeService.getAllEmployees());
            System.out.println("✅ Showing all employees: " + employeesList.size());
        } else {
            // Filter employees from the current list
            ObservableList<Employee> allEmployees = FXCollections.observableArrayList(employeeService.getAllEmployees());
            employeesList.clear();
            
            for (Employee employee : allEmployees) {
                if (employee.getFullName().toLowerCase().contains(searchText.toLowerCase()) ||
                    employee.getPhone().contains(searchText) ||
                    employee.getAddress().toLowerCase().contains(searchText.toLowerCase()) ||
                    String.valueOf(employee.getEmployeeId()).contains(searchText)) {
                    employeesList.add(employee);
                }
            }
            
            System.out.println("✅ Found " + employeesList.size() + " employees matching: " + searchText);
        }
        
        // Force table refresh to ensure buttons appear
        employeesTable.refresh();
    }

    /**
     * Get the employees list
     * @return Observable list of employees
     */
    public ObservableList<Employee> getEmployeesList() {
        return employeesList;
    }

    /**
     * Open employee form dialog for adding or editing
     * @param employee Employee to edit (null for add mode)
     */
    private void openEmployeeForm(Employee employee) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║         OPENING EMPLOYEE FORM DIALOG                        ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println("Mode: " + (employee == null ? "ADD" : "EDIT"));
            
            // Load FXML
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/employee_form.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Get controller
            EmployeeFormController formController = loader.getController();
            
            // Set callback to refresh table when saved
            formController.setOnSaveCallback(success -> {
                if (success) {
                    System.out.println("✅ Employee saved - refreshing table...");
                    refreshEmployeeTable();
                }
            });
            
            // Configure for ADD or EDIT mode
            if (employee == null) {
                // ADD mode
                formController.setAddMode();
            } else {
                // EDIT mode - need to get User_ID first
                try {
                    int userId = employeeService.getUserIdByEmployeeId(employee.getEmployeeId());
                    if (userId > 0) {
                        formController.setEditMode(employee, userId);
                    } else {
                        System.err.println("❌ Could not find User_ID for employee");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Failed to load employee");
                        alert.setContentText("Could not find user account for this employee.");
                        alert.showAndWait();
                        return;
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error getting User_ID: " + e.getMessage());
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to load employee");
                    alert.setContentText("Error: " + e.getMessage());
                    alert.showAndWait();
                    return;
                }
            }
            
            // Create and show dialog
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(employee == null ? "Add New Employee" : "Edit Employee");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(employeesTable.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(root, 600, 750));
            dialogStage.setResizable(false);
            
            System.out.println("✅ Employee form dialog opened");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            
            dialogStage.showAndWait();
            
        } catch (java.io.IOException e) {
            System.err.println("❌ Failed to load employee form FXML");
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open employee form");
            alert.setContentText("Could not load the employee form.\n\nError: " + e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error opening form");
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unexpected Error");
            alert.setContentText("An error occurred:\n" + e.getMessage());
            alert.showAndWait();
        }
    }
}


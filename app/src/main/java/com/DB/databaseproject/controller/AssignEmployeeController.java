package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Employee;
import com.DB.databaseproject.model.Order;
import com.DB.databaseproject.service.OrderService;
import com.DB.databaseproject.util.CustomDialogs;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Consumer;

/**
 * Controller for Assign Employee Popup
 * Stone Sales Management System
 */
public class AssignEmployeeController {

    @FXML private Label titleLabel;
    @FXML private Label orderInfoLabel;
    @FXML private Label currentEmployeeLabel;
    @FXML private ComboBox<Employee> employeeComboBox;
    @FXML private Button assignButton;
    @FXML private Button cancelButton;

    private final OrderService orderService = OrderService.getInstance();
    private Order currentOrder;
    private Consumer<Boolean> onAssignCallback;

    /**
     * Initialize method - called after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Assign Employee Popup initialized");
        
        // Configure ComboBox to display employee names
        employeeComboBox.setConverter(new StringConverter<Employee>() {
            @Override
            public String toString(Employee employee) {
                if (employee == null) {
                    return "";
                }
                return employee.getFirstName() + " " + employee.getLastName();
            }

            @Override
            public Employee fromString(String string) {
                return null;
            }
        });
        
        // Load employees
        loadEmployees();
    }

    /**
     * Load all employees into ComboBox
     */
    private void loadEmployees() {
        List<Employee> employees = orderService.getAllEmployees();
        employeeComboBox.getItems().clear();
        employeeComboBox.getItems().addAll(employees);
        
        System.out.println("✅ Loaded " + employees.size() + " employees");
    }

    /**
     * Set the order to assign employee to
     */
    public void setOrder(Order order) {
        this.currentOrder = order;
        
        // Update UI
        if (order.hasEmployee()) {
            titleLabel.setText("Change Employee for Order");
        } else {
            titleLabel.setText("Assign Employee to Order");
        }
        
        orderInfoLabel.setText("Order #" + order.getOrderId() + " - " + order.getCustomerName());
        currentEmployeeLabel.setText("Current: " + order.getEmployeeName());
        
        // Pre-select current employee if assigned
        if (order.hasEmployee()) {
            for (Employee emp : employeeComboBox.getItems()) {
                if (emp.getEmployeeId() == order.getEmployeeId()) {
                    employeeComboBox.setValue(emp);
                    break;
                }
            }
        }
    }

    /**
     * Set callback for when assignment is successful
     */
    public void setOnAssignCallback(Consumer<Boolean> callback) {
        this.onAssignCallback = callback;
    }

    /**
     * Handle Assign button click
     */
    @FXML
    private void handleAssign() {
        Employee selectedEmployee = employeeComboBox.getValue();
        
        if (selectedEmployee == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Employee Selected");
            alert.setHeaderText("Please Select an Employee");
            alert.setContentText("You must select an employee to assign to this order.");
            alert.showAndWait();
            return;
        }
        
        // BUSINESS RULE: Cannot assign employee to completed orders
        if ("Completed".equalsIgnoreCase(currentOrder.getStatus())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Order is Read-Only");
            alert.setHeaderText("Cannot Assign Employee");
            alert.setContentText("This order is already Completed and cannot be modified.\n\nCompleted orders are read-only.");
            alert.showAndWait();
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         ASSIGN EMPLOYEE TO ORDER                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Order ID: " + currentOrder.getOrderId());
        System.out.println("Employee ID: " + selectedEmployee.getEmployeeId());
        System.out.println("Employee Name: " + selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName());

        // Assign employee
        boolean success = orderService.assignEmployeeToOrder(
            currentOrder.getOrderId(), 
            selectedEmployee.getEmployeeId()
        );

        if (success) {
            System.out.println("✅ Employee assigned successfully!");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            
            // Update order object
            currentOrder.setEmployeeId(selectedEmployee.getEmployeeId());
            currentOrder.setEmployeeName(selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName());
            
            // Show success dialog
            Stage ownerStage = (Stage) assignButton.getScene().getWindow();
            CustomDialogs.showEmployeeAssignmentSuccess(
                ownerStage,
                currentOrder.getOrderId(),
                selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName()
            );
            
            // Callback to refresh parent table
            if (onAssignCallback != null) {
                onAssignCallback.accept(true);
            }
            
            // Close dialog
            closeDialog();
        } else {
            System.err.println("❌ Failed to assign employee");
            System.err.println("╚════════════════════════════════════════════════════════════╝\n");
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Assignment Failed");
            alert.setHeaderText("Failed to Assign Employee");
            alert.setContentText("Could not assign employee to order.\n\nThis may be because the order is already Completed (read-only).");
            alert.showAndWait();
        }
    }

    /**
     * Handle Cancel button click
     */
    @FXML
    private void handleCancel() {
        System.out.println("Employee assignment cancelled");
        closeDialog();
    }

    /**
     * Close the dialog
     */
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}

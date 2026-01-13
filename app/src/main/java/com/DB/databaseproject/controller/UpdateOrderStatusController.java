package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Order;
import com.DB.databaseproject.service.OrderService;
import com.DB.databaseproject.util.CustomDialogs;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.function.Consumer;

/**
 * Controller for Update Order Status Dialog
 * Stone Sales Management System - Dark Theme
 */
public class UpdateOrderStatusController {

    @FXML private Label orderInfoLabel;
    @FXML private Label currentStatusLabel;
    @FXML private Label employeeLabel;
    @FXML private Label orderDateLabel;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;

    private final OrderService orderService = OrderService.getInstance();
    private Order currentOrder;
    private Consumer<Boolean> onUpdateCallback;

    /**
     * Initialize method - called after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Update Order Status Dialog initialized");
        
        // Configure status options
        statusComboBox.getItems().addAll("Pending", "Processing", "Completed", "Cancelled");
        
        // Style the ComboBox cells
        statusComboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #ffffff; -fx-background-color: #3d3d3d; -fx-padding: 8 12;");
                }
            }
        });
        
        // Style the selected item display
        statusComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #ffffff;");
                }
            }
        });
    }

    /**
     * Set the order to update
     */
    public void setOrder(Order order) {
        this.currentOrder = order;
        
        // Update UI
        orderInfoLabel.setText("Order #" + order.getOrderId() + " - " + order.getCustomerName());
        currentStatusLabel.setText("Current Status: " + order.getStatus());
        
        // Apply color to current status
        String statusStyle = getStatusStyle(order.getStatus());
        currentStatusLabel.setStyle(statusStyle + " -fx-font-size: 14px; -fx-font-weight: bold;");
        
        employeeLabel.setText("Assigned To: " + order.getEmployeeName());
        orderDateLabel.setText("Order Date: " + order.getOrderDate().toString());
        
        // Pre-select current status
        statusComboBox.setValue(order.getStatus());
    }

    /**
     * Get color style for status
     */
    private String getStatusStyle(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return "-fx-text-fill: #E6A23C;";
            case "processing":
                return "-fx-text-fill: #409EFF;";
            case "completed":
                return "-fx-text-fill: #67C23A;";
            case "cancelled":
                return "-fx-text-fill: #F56C6C;";
            default:
                return "-fx-text-fill: #FFFFFF;";
        }
    }

    /**
     * Set callback for when update is successful
     */
    public void setOnUpdateCallback(Consumer<Boolean> callback) {
        this.onUpdateCallback = callback;
    }

    /**
     * Handle Update button click
     */
    @FXML
    private void handleUpdate() {
        String newStatus = statusComboBox.getValue();
        
        if (newStatus == null || newStatus.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Status Selected");
            alert.setHeaderText("Please Select a Status");
            alert.setContentText("You must select a new status for this order.");
            
            // Apply dark theme to alert
            applyDarkThemeToAlert(alert);
            
            alert.showAndWait();
            return;
        }

        // Check if status actually changed
        if (newStatus.equals(currentOrder.getStatus())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Change");
            alert.setHeaderText("Status Unchanged");
            alert.setContentText("The selected status is the same as the current status.");
            
            applyDarkThemeToAlert(alert);
            
            alert.showAndWait();
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         UPDATE ORDER STATUS                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Order ID: " + currentOrder.getOrderId());
        System.out.println("Old Status: " + currentOrder.getStatus());
        System.out.println("New Status: " + newStatus);

        // Check if order is already completed (read-only)
        if ("Completed".equalsIgnoreCase(currentOrder.getStatus())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Order is Read-Only");
            alert.setHeaderText("Cannot Update Completed Order");
            alert.setContentText("This order is already Completed and cannot be modified.\n\nCompleted orders are read-only.");
            applyDarkThemeToAlert(alert);
            alert.showAndWait();
            return;
        }

        // Update status in database
        String oldStatus = currentOrder.getStatus();
        boolean success = orderService.updateOrderStatus(currentOrder.getOrderId(), newStatus);

        if (success) {
            System.out.println("✅ Order status updated successfully!");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            
            // Update order object
            currentOrder.setStatus(newStatus);
            
            // Show success dialog
            Stage ownerStage = (Stage) updateButton.getScene().getWindow();
            CustomDialogs.showStatusUpdateSuccess(
                ownerStage,
                currentOrder.getOrderId(),
                oldStatus,
                newStatus
            );
            
            // Callback to refresh parent table
            if (onUpdateCallback != null) {
                onUpdateCallback.accept(true);
            }
            
            // Close dialog
            handleCancel();
            
        } else {
            System.err.println("❌ Failed to update order status");
            System.err.println("╚════════════════════════════════════════════════════════════╝\n");
            
            // Show error with valid transitions
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Failed");
            alert.setHeaderText("Invalid Status Transition");
            alert.setContentText(
                "Cannot change status from '" + oldStatus + "' to '" + newStatus + "'.\n\n" +
                "Valid transitions:\n" +
                "• Pending → Processing → Completed\n" +
                "• Any status → Cancelled (admin override)\n\n" +
                "Note: Completed orders are read-only."
            );
            
            applyDarkThemeToAlert(alert);
            
            alert.showAndWait();
        }
    }

    /**
     * Handle Cancel button click
     */
    @FXML
    private void handleCancel() {
        System.out.println("Update Order Status dialog cancelled");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Apply dark theme styling to JavaFX Alert dialogs
     */
    private void applyDarkThemeToAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
            "-fx-background-color: #2b2b2b; " +
            "-fx-border-color: #C2B280; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );
        
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #1e1e1e;");
        dialogPane.lookup(".content").setStyle("-fx-background-color: #2b2b2b;");
        dialogPane.lookup(".label").setStyle("-fx-text-fill: #ffffff;");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: #d4c59e; -fx-font-weight: bold;");
    }
}

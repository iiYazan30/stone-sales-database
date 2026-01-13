package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Order;
import com.DB.databaseproject.service.AuthenticationService;
import com.DB.databaseproject.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

/**
 * Controller for Customer Orders View
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class CustomerOrdersController {
    
    private final OrderService orderService = OrderService.getInstance();
    private final AuthenticationService authService = AuthenticationService.getInstance();

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;

    @FXML
    private TableColumn<Order, String> stoneNameColumn;

    @FXML
    private TableColumn<Order, Integer> quantityColumn;

    @FXML
    private TableColumn<Order, LocalDate> orderDateColumn;

    @FXML
    private TableColumn<Order, Double> totalAmountColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    private TableColumn<Order, Void> actionsColumn;

    private ObservableList<Order> ordersList;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Customer Orders View initialized");

        // Initialize the orders list
        ordersList = FXCollections.observableArrayList();

        // Configure table columns
        setupTableColumns();

        // Load sample data
        loadSampleData();

        // Set the data to the table
        ordersTable.setItems(ordersList);

        // Placeholder message when table is empty
        ordersTable.setPlaceholder(new Label("No orders yet"));

        // Make table responsive - columns auto-resize to fill available width
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        ordersTable.setPrefWidth(Double.MAX_VALUE);
    }

    /**
     * Setup table columns with cell value factories
     */
    private void setupTableColumns() {
        // Order ID Column
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        // Stone Name Column (using customerName field as stoneName)
        stoneNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        // Quantity Column (using a custom property - for now use orderId as placeholder)
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        quantityColumn.setCellFactory(column -> new TableCell<Order, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText("3"); // Placeholder quantity
                }
            }
        });

        // Order Date Column
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        // Total Amount Column
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalAmountColumn.setCellFactory(column -> new TableCell<Order, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                }
            }
        });

        // Status Column with color coding
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    // Apply color based on status (text color only, no bold)
                    switch (status.toLowerCase()) {
                        case "canceled":
                        case "cancelled":
                            setStyle("-fx-text-fill: #D9534F;"); // Soft red
                            break;
                        case "completed":
                            setStyle("-fx-text-fill: #6FCF97;"); // Soft green
                            break;
                        case "pending":
                            setStyle("-fx-text-fill: #C9B06B;"); // Gold/beige
                            break;
                        case "processing":
                            setStyle("-fx-text-fill: #409EFF;"); // Blue
                            break;
                        case "assigned":
                            setStyle("-fx-text-fill: #8A8A8A;"); // Soft gray
                            break;
                        default:
                            setStyle("-fx-text-fill: #FFFFFF;"); // White for unknown statuses
                    }
                }
            }
        });

        // Actions Column (Cancel Order button)
        setupActionsColumn();
    }

    /**
     * Setup the Actions column with Cancel Order button
     * BUSINESS RULE: Cancel button only visible for Pending orders
     */
    private void setupActionsColumn() {
        actionsColumn.setPrefWidth(150);
        actionsColumn.setMinWidth(150);
        actionsColumn.setResizable(true);
        
        actionsColumn.setCellFactory(column -> new TableCell<Order, Void>() {
            private final Button cancelButton = new Button("Cancel Order");

            {
                // Style the button with soft gray color
                cancelButton.getStyleClass().add("btn-cancel-customer");
                cancelButton.setMinWidth(130);
                cancelButton.setPrefWidth(130);

                // Set button action
                cancelButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    handleCancelOrder(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    // BUSINESS RULE: Only show Cancel button for Pending orders
                    if (order != null && "Pending".equalsIgnoreCase(order.getStatus())) {
                        setGraphic(cancelButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    /**
     * Handle Cancel Order button click
     * Shows confirmation dialog and processes cancellation
     */
    private void handleCancelOrder(Order order) {
        System.out.println("Cancel Order requested for Order ID: " + order.getOrderId());
        
        // Show confirmation dialog
        boolean confirmed = showCancelConfirmationDialog(order);
        
        if (confirmed) {
            // Get customer ID from authentication service
            int customerId = authService.getCurrentCustomer().getCustomerId();
            
            // Call service to cancel order
            boolean success = orderService.cancelOrder(order.getOrderId(), customerId);
            
            if (success) {
                // Show success message
                showSuccessDialog("Order canceled successfully");
                
                // Refresh the orders table
                loadSampleData();
            } else {
                // Show error message
                showErrorDialog("Failed to cancel order", 
                    "This order cannot be canceled. It may have already been processed or assigned to an employee.");
            }
        }
    }

    /**
     * Show confirmation dialog for order cancellation
     * Uses custom styled dialog matching app theme
     */
    private boolean showCancelConfirmationDialog(Order order) {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initOwner(ordersTable.getScene().getWindow());
        dialogStage.setTitle("Cancel Order");
        dialogStage.setResizable(false);

        // Create content
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(20);
        content.setPadding(new javafx.geometry.Insets(30));
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setStyle("-fx-background-color: #2A2A2A;");

        // Icon and message
        Label icon = new Label("⚠");
        icon.setStyle("-fx-font-size: 48px; -fx-text-fill: #FFA500;");

        Label titleLabel = new Label("Cancel Order #" + order.getOrderId() + "?");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        Label messageLabel = new Label("Are you sure you want to cancel this order?");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #CCCCCC;");

        Label warningLabel = new Label("This action cannot be undone.");
        warningLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #FFA500; -fx-font-style: italic;");

        // Buttons
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(15);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        final boolean[] confirmed = {false};

        Button confirmButton = new Button("Cancel Order");
        confirmButton.setStyle(
            "-fx-background-color: #8A8A8A; " +
            "-fx-text-fill: #151515; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 25px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        confirmButton.setOnMouseEntered(e -> confirmButton.setStyle(
            "-fx-background-color: #A0A0A0; " +
            "-fx-text-fill: #151515; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 25px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        ));
        confirmButton.setOnMouseExited(e -> confirmButton.setStyle(
            "-fx-background-color: #8A8A8A; " +
            "-fx-text-fill: #151515; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 25px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        ));
        confirmButton.setOnAction(e -> {
            confirmed[0] = true;
            dialogStage.close();
        });

        Button backButton = new Button("Back");
        backButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #C2B280; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 25px; " +
            "-fx-background-radius: 6px; " +
            "-fx-border-color: #C2B280; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: rgba(194, 178, 128, 0.1); " +
            "-fx-text-fill: #C2B280; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 25px; " +
            "-fx-background-radius: 6px; " +
            "-fx-border-color: #C2B280; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 6px; " +
            "-fx-cursor: hand;"
        ));
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #C2B280; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 25px; " +
            "-fx-background-radius: 6px; " +
            "-fx-border-color: #C2B280; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 6px; " +
            "-fx-cursor: hand;"
        ));
        backButton.setOnAction(e -> dialogStage.close());

        buttonBox.getChildren().addAll(confirmButton, backButton);

        content.getChildren().addAll(icon, titleLabel, messageLabel, warningLabel, buttonBox);

        javafx.scene.Scene scene = new javafx.scene.Scene(content, 400, 300);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();

        return confirmed[0];
    }

    /**
     * Show success dialog
     */
    private void showSuccessDialog(String message) {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initOwner(ordersTable.getScene().getWindow());
        dialogStage.setTitle("Success");
        dialogStage.setResizable(false);

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(20);
        content.setPadding(new javafx.geometry.Insets(30));
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setStyle("-fx-background-color: #2A2A2A;");

        Label icon = new Label("✓");
        icon.setStyle("-fx-font-size: 48px; -fx-text-fill: #4CAF50;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");

        Button okButton = new Button("OK");
        okButton.setStyle(
            "-fx-background-color: #C2B280; " +
            "-fx-text-fill: #1F1F1F; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 30px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        okButton.setOnAction(e -> dialogStage.close());

        content.getChildren().addAll(icon, messageLabel, okButton);

        javafx.scene.Scene scene = new javafx.scene.Scene(content, 350, 220);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    /**
     * Show error dialog
     */
    private void showErrorDialog(String title, String message) {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initOwner(ordersTable.getScene().getWindow());
        dialogStage.setTitle(title);
        dialogStage.setResizable(false);

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(20);
        content.setPadding(new javafx.geometry.Insets(30));
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setStyle("-fx-background-color: #2A2A2A;");

        Label icon = new Label("✕");
        icon.setStyle("-fx-font-size: 48px; -fx-text-fill: #F56C6C;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #CCCCCC;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        messageLabel.setAlignment(javafx.geometry.Pos.CENTER);

        Button okButton = new Button("OK");
        okButton.setStyle(
            "-fx-background-color: #C2B280; " +
            "-fx-text-fill: #1F1F1F; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 30px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        okButton.setOnAction(e -> dialogStage.close());

        content.getChildren().addAll(icon, titleLabel, messageLabel, okButton);

        javafx.scene.Scene scene = new javafx.scene.Scene(content, 400, 280);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    /**
     * Load sample order data
     */
    private void loadSampleData() {
        // Load orders from database for current customer
        ordersList.clear();
        
        if (authService.getCurrentCustomer() != null) {
            int customerId = authService.getCurrentCustomer().getCustomerId();
            ordersList.addAll(orderService.getOrdersByCustomer(customerId));
            System.out.println("Customer orders loaded from database: " + ordersList.size());
        } else {
            System.err.println("⚠️ No customer logged in");
        }
    }
}

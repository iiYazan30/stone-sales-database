package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Order;
import com.DB.databaseproject.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller for Orders Management View
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class OrdersController {
    
    private final OrderService orderService = OrderService.getInstance();

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> idColumn;

    @FXML
    private TableColumn<Order, String> customerColumn;

    @FXML
    private TableColumn<Order, String> employeeColumn;

    @FXML
    private TableColumn<Order, LocalDate> dateColumn;

    @FXML
    private TableColumn<Order, Double> totalColumn;

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
        System.out.println("Orders Management View initialized");

        // Initialize the orders list
        ordersList = FXCollections.observableArrayList();

        // Configure table columns
        setupTableColumns();

        // Load sample data
        loadSampleData();

        // Set the data to the table ONCE - we never change this reference
        ordersTable.setItems(ordersList);

        // Placeholder message when table is empty
        ordersTable.setPlaceholder(new Label("No orders found"));

        // Make table responsive - columns auto-resize to fill available width
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        ordersTable.setPrefWidth(Double.MAX_VALUE);
        
        // Setup search field listener for real-time filtering
        setupSearchListener();
    }
    
    /**
     * Setup search field listener for real-time filtering
     * This ensures action buttons remain visible during search
     */
    private void setupSearchListener() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                // Automatically trigger search when text changes
                onSearchOrder();
            });
        }
    }

    /**
     * Setup table columns with cell value factories
     */
    private void setupTableColumns() {
        // Order ID Column
        idColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        // Customer Name Column
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        // Employee Name Column
        employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        // Order Date Column
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        // Total Amount Column
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalColumn.setCellFactory(column -> new TableCell<Order, Double>() {
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

        // Status Column with styling based on status
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
                    // Apply color based on status - exact colors per requirements
                    switch (status.toLowerCase()) {
                        case "pending":
                            setStyle("-fx-text-fill: #E6A23C; -fx-font-weight: bold;"); // Orange
                            break;
                        case "processing":
                            setStyle("-fx-text-fill: #409EFF; -fx-font-weight: bold;"); // Blue
                            break;
                        case "completed":
                            setStyle("-fx-text-fill: #67C23A; -fx-font-weight: bold;"); // Green
                            break;
                        case "cancelled":
                            setStyle("-fx-text-fill: #F56C6C; -fx-font-weight: bold;"); // Red
                            break;
                        default:
                            setStyle("-fx-text-fill: #FFFFFF;");
                    }
                }
            }
        });

        // Actions Column (View Details and Update Status buttons)
        setupActionsColumn();
    }

    /**
     * Setup the Actions column with Assign Employee and Update Status buttons
     * FIXED: Proper button sizing with USE_PREF_SIZE to prevent truncation
     */
    private void setupActionsColumn() {
        // FIXED: Increased column width to accommodate buttons without truncation
        actionsColumn.setPrefWidth(380);
        actionsColumn.setMinWidth(380);
        actionsColumn.setResizable(true);
        
        actionsColumn.setCellFactory(column -> new TableCell<Order, Void>() {
            private final Button assignEmployeeButton = new Button();
            private final Button updateStatusButton = new Button("Update Status");
            private final HBox actionButtons = new HBox(12, assignEmployeeButton, updateStatusButton);

            {
                // FIXED: Configure button sizes with USE_PREF_SIZE to prevent truncation
                assignEmployeeButton.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
                assignEmployeeButton.setPrefWidth(170);
                assignEmployeeButton.setMaxWidth(Double.MAX_VALUE);
                assignEmployeeButton.setWrapText(false);
                
                updateStatusButton.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
                updateStatusButton.setPrefWidth(170);
                updateStatusButton.setMaxWidth(Double.MAX_VALUE);
                updateStatusButton.setWrapText(false);
                
                // Allow buttons to grow in HBox
                javafx.scene.layout.HBox.setHgrow(assignEmployeeButton, javafx.scene.layout.Priority.ALWAYS);
                javafx.scene.layout.HBox.setHgrow(updateStatusButton, javafx.scene.layout.Priority.ALWAYS);
                
                // FIXED: Style Update Status button with gold theme and proper padding
                updateStatusButton.setStyle(
                    "-fx-background-color: #d2bb72; " +
                    "-fx-text-fill: #1e1e1e; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 15; " +
                    "-fx-padding: 6px 14px; " +
                    "-fx-font-size: 13px; " +
                    "-fx-cursor: hand;"
                );
                updateStatusButton.setOnMouseEntered(e -> updateStatusButton.setStyle(
                    "-fx-background-color: #e6d4a5; " +
                    "-fx-text-fill: #1e1e1e; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 15; " +
                    "-fx-padding: 6px 14px; " +
                    "-fx-font-size: 13px; " +
                    "-fx-cursor: hand;"
                ));
                updateStatusButton.setOnMouseExited(e -> updateStatusButton.setStyle(
                    "-fx-background-color: #d2bb72; " +
                    "-fx-text-fill: #1e1e1e; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 15; " +
                    "-fx-padding: 6px 14px; " +
                    "-fx-font-size: 13px; " +
                    "-fx-cursor: hand;"
                ));
                
                // FIXED: Style Assign Employee button with proper sizing and padding
                assignEmployeeButton.setStyle(
                    "-fx-background-color: #409EFF; " +
                    "-fx-text-fill: #FFFFFF; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 15; " +
                    "-fx-padding: 6px 14px; " +
                    "-fx-font-size: 13px; " +
                    "-fx-cursor: hand;"
                );
                assignEmployeeButton.setOnMouseEntered(e -> assignEmployeeButton.setStyle(
                    "-fx-background-color: #66B1FF; " +
                    "-fx-text-fill: #FFFFFF; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 15; " +
                    "-fx-padding: 6px 14px; " +
                    "-fx-font-size: 13px; " +
                    "-fx-cursor: hand;"
                ));
                assignEmployeeButton.setOnMouseExited(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    if (order != null && order.hasEmployee()) {
                        assignEmployeeButton.setStyle(
                            "-fx-background-color: #E6A23C; " +
                            "-fx-text-fill: #FFFFFF; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 15; " +
                            "-fx-padding: 6px 14px; " +
                            "-fx-font-size: 13px; " +
                            "-fx-cursor: hand;"
                        );
                    } else {
                        assignEmployeeButton.setStyle(
                            "-fx-background-color: #409EFF; " +
                            "-fx-text-fill: #FFFFFF; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 15; " +
                            "-fx-padding: 6px 14px; " +
                            "-fx-font-size: 13px; " +
                            "-fx-cursor: hand;"
                        );
                    }
                });

                // Center the buttons
                actionButtons.setAlignment(Pos.CENTER);

                // Set button actions
                assignEmployeeButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    onAssignEmployee(order);
                });

                updateStatusButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    onUpdateStatus(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    if (order != null) {
                        // BUSINESS RULE: Once order is Completed, it becomes read-only
                        // Hide both buttons for completed orders
                        boolean isCompleted = "Completed".equalsIgnoreCase(order.getStatus());
                        
                        if (isCompleted) {
                            // Hide both buttons for completed orders (read-only)
                            assignEmployeeButton.setVisible(false);
                            assignEmployeeButton.setManaged(false);
                            updateStatusButton.setVisible(false);
                            updateStatusButton.setManaged(false);
                        } else {
                            // Show buttons for Pending and Processing orders
                            assignEmployeeButton.setVisible(true);
                            assignEmployeeButton.setManaged(true);
                            updateStatusButton.setVisible(true);
                            updateStatusButton.setManaged(true);
                            
                            // Update button text based on employee assignment with proper styling
                            if (order.hasEmployee()) {
                                assignEmployeeButton.setText("Change Employee");
                                assignEmployeeButton.setStyle(
                                    "-fx-background-color: #E6A23C; " +
                                    "-fx-text-fill: #FFFFFF; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-background-radius: 15; " +
                                    "-fx-padding: 6px 14px; " +
                                    "-fx-font-size: 13px; " +
                                    "-fx-cursor: hand;"
                                );
                            } else {
                                assignEmployeeButton.setText("Assign Employee");
                                assignEmployeeButton.setStyle(
                                    "-fx-background-color: #409EFF; " +
                                    "-fx-text-fill: #FFFFFF; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-background-radius: 15; " +
                                    "-fx-padding: 6px 14px; " +
                                    "-fx-font-size: 13px; " +
                                    "-fx-cursor: hand;"
                                );
                            }
                        }
                    }
                    setGraphic(actionButtons);
                }
            }
        });
    }

    /**
     * Load sample order data (temporary for UI preview)
     * Later this will be replaced with PostgreSQL data
     */
    private void loadSampleData() {
        // Load only active orders from database (Pending, Assigned, Processing)
        // Completed and Canceled orders are in Archive
        ordersList.clear();
        ordersList.addAll(orderService.getActiveOrders());
        
        System.out.println("Active orders loaded from database: " + ordersList.size());
    }
    
    /**
     * Refresh the order table by reloading data from database
     */
    private void refreshOrderTable() {
        System.out.println("\n🔄 Refreshing active orders table from database...");
        ordersList.clear();
        ordersList.addAll(orderService.getActiveOrders());
        System.out.println("✅ Table refreshed: " + ordersList.size() + " active orders loaded\n");
        
        // Re-apply search filter if search field has text
        if (searchField != null && !searchField.getText().trim().isEmpty()) {
            onSearchOrder();
        }
    }

    /**
     * Handle Search Order button click
     * FIXED: Only filters the data, does NOT change table columns or cellFactory
     */
    @FXML
    private void onSearchOrder() {
        String searchText = searchField.getText().trim();
        System.out.println("Search Order: " + searchText);
        
        if (searchText.isEmpty()) {
            // Clear search - reload all orders from database and show them
            System.out.println("🔄 Search cleared - reloading all orders from database...");
            ordersList.clear();
            ordersList.addAll(orderService.getAllOrders());
            System.out.println("✅ Showing all orders: " + ordersList.size());
        } else {
            // Filter orders from the current list
            ObservableList<Order> allOrders = FXCollections.observableArrayList(orderService.getAllOrders());
            ordersList.clear();
            
            for (Order order : allOrders) {
                if (order.getCustomerName().toLowerCase().contains(searchText.toLowerCase()) ||
                    order.getEmployeeName().toLowerCase().contains(searchText.toLowerCase()) ||
                    order.getStatus().toLowerCase().contains(searchText.toLowerCase()) ||
                    String.valueOf(order.getOrderId()).contains(searchText)) {
                    ordersList.add(order);
                }
            }
            
            System.out.println("✅ Found " + ordersList.size() + " orders matching: " + searchText);
        }
        
        // Force table refresh to ensure buttons appear
        ordersTable.refresh();
    }

    /**
     * Handle Assign Employee button click
     * @param order The order to assign employee to
     */
    private void onAssignEmployee(Order order) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         ASSIGN EMPLOYEE TO ORDER - UI                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Customer: " + order.getCustomerName());
        System.out.println("Current Employee: " + order.getEmployeeName());
        
        try {
            // Load the assign employee popup FXML
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/assign_employee_popup.fxml"));
            javafx.scene.Parent root = loader.load();
            
            // Get the controller and set the order
            AssignEmployeeController controller = loader.getController();
            controller.setOrder(order);
            
            // Set callback to refresh table when assignment is successful
            controller.setOnAssignCallback(success -> {
                if (success) {
                    // Reload all orders from database to show updated employee
                    refreshOrderTable();
                    System.out.println("✅ Orders table reloaded after employee assignment");
                }
            });
            
            // Create and show the popup dialog
            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialog.initOwner(ordersTable.getScene().getWindow());
            dialog.initStyle(javafx.stage.StageStyle.UNDECORATED);
            dialog.setTitle("Assign Employee");
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            
            // Load dialog CSS
            scene.getStylesheets().add(getClass().getResource("/css/dialogs.css").toExternalForm());
            
            dialog.setScene(scene);
            dialog.showAndWait();
            
        } catch (java.io.IOException e) {
            System.err.println("❌ Error loading assign employee dialog");
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Open Dialog");
            alert.setContentText("Could not load the employee assignment dialog.");
            alert.showAndWait();
        }
    }

    /**
     * Handle Update Status button click
     * @param order The order to update
     */
    private void onUpdateStatus(Order order) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         UPDATE ORDER STATUS - UI                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Customer: " + order.getCustomerName());
        System.out.println("Current Status: " + order.getStatus());
        
        try {
            // Load the update status dialog FXML
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/update_order_status_dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            
            // Get the controller and set the order
            UpdateOrderStatusController controller = loader.getController();
            controller.setOrder(order);
            
            // Set callback to refresh table when update is successful
            controller.setOnUpdateCallback(success -> {
                if (success) {
                    // Reload all orders from database to show updated status
                    refreshOrderTable();
                    System.out.println("✅ Orders table reloaded after status update");
                }
            });
            
            // Create and show the popup dialog
            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialog.initOwner(ordersTable.getScene().getWindow());
            dialog.initStyle(javafx.stage.StageStyle.UNDECORATED);
            dialog.setTitle("Update Order Status");
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            
            // Load dialog CSS
            scene.getStylesheets().add(getClass().getResource("/css/dialogs.css").toExternalForm());
            
            dialog.setScene(scene);
            dialog.showAndWait();
            
        } catch (java.io.IOException e) {
            System.err.println("❌ Error loading update status dialog");
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Open Dialog");
            alert.setContentText("Could not load the status update dialog.");
            alert.showAndWait();
        }
    }

    /**
     * Get the orders list
     * @return Observable list of orders
     */
    public ObservableList<Order> getOrdersList() {
        return ordersList;
    }
}

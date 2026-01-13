package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Order;
import com.DB.databaseproject.service.AuthenticationService;
import com.DB.databaseproject.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller for Employee Orders View
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class EmployeeOrdersController {
    
    private final OrderService orderService = OrderService.getInstance();
    private final AuthenticationService authService = AuthenticationService.getInstance();

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> idColumn;

    @FXML
    private TableColumn<Order, String> customerColumn;

    @FXML
    private TableColumn<Order, LocalDate> dateColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    private TableColumn<Order, Double> totalColumn;

    @FXML
    private TableColumn<Order, Void> actionsColumn;

    private ObservableList<Order> ordersList;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Employee Orders View initialized");

        // Initialize the orders list
        ordersList = FXCollections.observableArrayList();

        // Configure table columns
        setupTableColumns();

        // Load sample data (orders assigned to this employee)
        loadSampleData();

        // Set the data to the table
        ordersTable.setItems(ordersList);

        // Placeholder message when table is empty
        ordersTable.setPlaceholder(new Label("No assigned orders yet"));

        // Make table responsive - columns auto-resize to fill available width
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        ordersTable.setPrefWidth(Double.MAX_VALUE);
    }

    /**
     * Setup table columns with cell value factories
     */
    private void setupTableColumns() {
        // Order ID Column
        idColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        // Customer Name Column
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));

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
                    // Apply color based on status
                    switch (status.toLowerCase()) {
                        case "pending":
                            setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold;"); // Orange
                            break;
                        case "processing":
                            setStyle("-fx-text-fill: #409EFF; -fx-font-weight: bold;"); // Blue
                            break;
                        case "in progress":
                            setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;"); // Blue
                            break;
                        case "completed":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;"); // Green
                            break;
                        case "cancelled":
                            setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;"); // Red
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
     * Setup the Actions column with View Details and Update Status buttons
     */
    private void setupActionsColumn() {
        // Set preferred width for actions column to accommodate buttons
        actionsColumn.setPrefWidth(290);
        actionsColumn.setMinWidth(290);
        actionsColumn.setResizable(true);
        
        actionsColumn.setCellFactory(column -> new TableCell<Order, Void>() {
            private final Button viewDetailsButton = new Button("View Details");
            private final Button updateStatusButton = new Button("Update Status");
            private final HBox actionButtons = new HBox(8, viewDetailsButton, updateStatusButton);

            {
                // Configure button sizes - ensure full text is visible
                viewDetailsButton.setMinWidth(135);
                viewDetailsButton.setPrefWidth(135);
                viewDetailsButton.setMaxWidth(Double.MAX_VALUE);
                viewDetailsButton.setWrapText(true);
                
                updateStatusButton.setMinWidth(135);
                updateStatusButton.setPrefWidth(135);
                updateStatusButton.setMaxWidth(Double.MAX_VALUE);
                updateStatusButton.setWrapText(true);
                
                // Allow buttons to grow in HBox
                javafx.scene.layout.HBox.setHgrow(viewDetailsButton, javafx.scene.layout.Priority.ALWAYS);
                javafx.scene.layout.HBox.setHgrow(updateStatusButton, javafx.scene.layout.Priority.ALWAYS);
                
                // Style the buttons
                viewDetailsButton.getStyleClass().add("btn-view-details");
                updateStatusButton.getStyleClass().add("btn-update-status");

                // Center the buttons
                actionButtons.setAlignment(Pos.CENTER);

                // Set button actions
                viewDetailsButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    onViewDetails(order);
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
                    setGraphic(actionButtons);
                }
            }
        });
    }

    /**
     * Load sample order data (orders assigned to employee Mahmoud Salameh)
     */
    private void loadSampleData() {
        // Load orders from database for current employee
        ordersList.clear();
        
        if (authService.getCurrentEmployee() != null) {
            int employeeId = authService.getCurrentEmployee().getEmployeeId();
            ordersList.addAll(orderService.getOrdersByEmployee(employeeId));
            System.out.println("Employee orders loaded from database: " + ordersList.size());
        } else {
            System.err.println("⚠️ No employee logged in");
        }
    }

    /**
     * Handle View Details button click
     * @param order The order to view
     */
    private void onViewDetails(Order order) {
        System.out.println("View Details for Order ID: " + order.getOrderId());
        
        // Create order details dialog
        showOrderDetailsDialog(order);
    }

    /**
     * Show Order Details Dialog (Popup)
     * @param order The order to display
     */
    private void showOrderDetailsDialog(Order order) {
        // Create dialog stage
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Order Details - #" + order.getOrderId());
        dialogStage.setResizable(false);

        // Create content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1F1F1F;");

        // Title
        Label titleLabel = new Label("Order #" + order.getOrderId());
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #C2B280;");

        // Order Information
        VBox orderInfo = new VBox(10);
        orderInfo.getChildren().addAll(
            createInfoLabel("Customer:", order.getCustomerName()),
            createInfoLabel("Employee:", order.getEmployeeName()),
            createInfoLabel("Order Date:", order.getOrderDate().toString()),
            createInfoLabel("Status:", order.getStatus()),
            createInfoLabel("Total Amount:", String.format("$%.2f", order.getTotalAmount()))
        );

        // Separator
        Label itemsLabel = new Label("Order Items (Sample):");
        itemsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #C2B280;");

        // Sample items table (placeholder)
        VBox itemsList = new VBox(5);
        itemsList.getChildren().addAll(
            createItemRow("Jerusalem Stone (40x40)", "4 units", "$55.00", "$220.00"),
            createItemRow("Marble Tile (30x30)", "2 units", "$50.00", "$100.00")
        );

        // Separator line
        Label separator = new Label("─".repeat(50));
        separator.setStyle("-fx-text-fill: #C2B280;");

        // Total
        Label totalLabel = new Label(String.format("Total: $%.2f", order.getTotalAmount()));
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #C2B280;");

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #C2B280; -fx-text-fill: #1F1F1F; " +
                            "-fx-font-weight: bold; -fx-padding: 10 30; " +
                            "-fx-background-radius: 5; -fx-cursor: hand;");
        closeButton.setOnAction(e -> dialogStage.close());

        // Add all to content
        content.getChildren().addAll(
            titleLabel,
            orderInfo,
            itemsLabel,
            itemsList,
            separator,
            totalLabel,
            closeButton
        );

        // Create scene and show
        Scene scene = new Scene(content, 500, 500);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    /**
     * Create info label row
     */
    private HBox createInfoLabel(String label, String value) {
        HBox hbox = new HBox(10);
        Label labelField = new Label(label);
        labelField.setStyle("-fx-text-fill: #888888; -fx-font-weight: bold; -fx-min-width: 120;");
        Label valueField = new Label(value);
        valueField.setStyle("-fx-text-fill: #FFFFFF;");
        hbox.getChildren().addAll(labelField, valueField);
        return hbox;
    }

    /**
     * Create item row for order items
     */
    private HBox createItemRow(String name, String quantity, String unitPrice, String subtotal) {
        HBox hbox = new HBox(10);
        hbox.setStyle("-fx-padding: 5; -fx-background-color: #2A2A2A; -fx-background-radius: 5;");
        
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-min-width: 200;");
        
        Label quantityLabel = new Label(quantity);
        quantityLabel.setStyle("-fx-text-fill: #C2B280; -fx-min-width: 60;");
        
        Label unitPriceLabel = new Label(unitPrice);
        unitPriceLabel.setStyle("-fx-text-fill: #888888; -fx-min-width: 60;");
        
        Label subtotalLabel = new Label(subtotal);
        subtotalLabel.setStyle("-fx-text-fill: #C2B280; -fx-font-weight: bold;");
        
        hbox.getChildren().addAll(nameLabel, quantityLabel, unitPriceLabel, subtotalLabel);
        return hbox;
    }

    /**
     * Handle Update Status button click
     * @param order The order to update
     */
    private void onUpdateStatus(Order order) {
        System.out.println("Update Status for Order ID: " + order.getOrderId());
        
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
                    loadSampleData();
                    System.out.println("✅ Orders table reloaded after status update");
                }
            });
            
            // Create and show the popup dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ordersTable.getScene().getWindow());
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setTitle("Update Order Status");
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            
            // Load dialog CSS
            scene.getStylesheets().add(getClass().getResource("/css/dialogs.css").toExternalForm());
            
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            
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

package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Order;
import com.DB.databaseproject.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

/**
 * Controller for Archive View (READ-ONLY)
 * Displays Completed and Canceled orders
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class ArchiveController {
    
    private final OrderService orderService = OrderService.getInstance();

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Order> archiveTable;

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

    private ObservableList<Order> archiveList;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Archive View initialized");

        // Initialize the archive list
        archiveList = FXCollections.observableArrayList();

        // Configure table columns
        setupTableColumns();

        // Load archived orders
        loadArchivedOrders();

        // Set the data to the table
        archiveTable.setItems(archiveList);

        // Placeholder message when table is empty
        archiveTable.setPlaceholder(new Label("No archived orders"));

        // Make table responsive
        archiveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        archiveTable.setPrefWidth(Double.MAX_VALUE);
        
        // Setup search field listener
        setupSearchListener();
    }
    
    /**
     * Setup search field listener for real-time filtering
     */
    private void setupSearchListener() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
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

        // Status Column with color coding (READ-ONLY)
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
                        case "completed":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;"); // Green
                            break;
                        case "canceled":
                        case "cancelled":
                            setStyle("-fx-text-fill: #F56C6C; -fx-font-weight: bold;"); // Red
                            break;
                        default:
                            setStyle("-fx-text-fill: #FFFFFF;");
                    }
                }
            }
        });
    }

    /**
     * Load archived orders (Completed, Canceled)
     */
    private void loadArchivedOrders() {
        archiveList.clear();
        archiveList.addAll(orderService.getArchivedOrders());
        System.out.println("Archived orders loaded from database: " + archiveList.size());
    }

    /**
     * Handle Search Order button click
     */
    @FXML
    private void onSearchOrder() {
        String searchText = searchField.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            // If search is empty, show all archived orders
            loadArchivedOrders();
            return;
        }
        
        System.out.println("Searching archived orders for: " + searchText);
        
        // Get all archived orders
        var allArchivedOrders = orderService.getArchivedOrders();
        
        // Filter orders based on search text
        var filteredOrders = allArchivedOrders.stream()
            .filter(order -> 
                String.valueOf(order.getOrderId()).contains(searchText) ||
                order.getCustomerName().toLowerCase().contains(searchText) ||
                order.getEmployeeName().toLowerCase().contains(searchText) ||
                order.getStatus().toLowerCase().contains(searchText)
            )
            .toList();
        
        // Update table
        archiveList.clear();
        archiveList.addAll(filteredOrders);
        
        System.out.println("Search results: " + filteredOrders.size() + " archived orders found");
    }
}

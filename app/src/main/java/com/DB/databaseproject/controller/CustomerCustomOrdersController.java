package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.CustomOrder;
import com.DB.databaseproject.service.AuthenticationService;
import com.DB.databaseproject.service.CustomOrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerCustomOrdersController {
    
    @FXML
    private TableView<CustomOrder> customOrdersTable;
    
    @FXML
    private TableColumn<CustomOrder, Integer> idColumn;
    
    @FXML
    private TableColumn<CustomOrder, String> stoneTypeColumn;
    
    @FXML
    private TableColumn<CustomOrder, String> sizeColumn;
    
    @FXML
    private TableColumn<CustomOrder, Integer> quantityColumn;
    
    @FXML
    private TableColumn<CustomOrder, String> descriptionColumn;
    
    @FXML
    private TableColumn<CustomOrder, String> statusColumn;
    
    @FXML
    private TableColumn<CustomOrder, LocalDateTime> createdAtColumn;
    
    private final CustomOrderService customOrderService = CustomOrderService.getInstance();
    private final AuthenticationService authService = AuthenticationService.getInstance();
    private ObservableList<CustomOrder> customOrdersList;
    
    @FXML
    public void initialize() {
        customOrdersList = FXCollections.observableArrayList();
        
        setupTableColumns();
        loadCustomOrders();
        
        customOrdersTable.setItems(customOrdersList);
        customOrdersTable.setPlaceholder(new Label("No custom orders yet"));
        customOrdersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customOrderId"));
        stoneTypeColumn.setCellValueFactory(new PropertyValueFactory<>("stoneType"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("stoneDescription"));
        
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<CustomOrder, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status.toLowerCase()) {
                        case "pending":
                            setStyle("-fx-text-fill: #C9B06B;");
                            break;
                        case "approved":
                            setStyle("-fx-text-fill: #6FCF97;");
                            break;
                        case "rejected":
                            setStyle("-fx-text-fill: #D9534F;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #FFFFFF;");
                    }
                }
            }
        });
        
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtColumn.setCellFactory(column -> new TableCell<CustomOrder, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
            
            @Override
            protected void updateItem(LocalDateTime dateTime, boolean empty) {
                super.updateItem(dateTime, empty);
                if (empty || dateTime == null) {
                    setText(null);
                } else {
                    setText(formatter.format(dateTime));
                }
            }
        });
    }
    
    private void loadCustomOrders() {
        customOrdersList.clear();
        if (authService.getCurrentCustomer() != null) {
            int customerId = authService.getCurrentCustomer().getCustomerId();
            customOrdersList.addAll(customOrderService.getCustomOrdersByCustomer(customerId));
        }
    }
    
    public void refresh() {
        loadCustomOrders();
    }
}

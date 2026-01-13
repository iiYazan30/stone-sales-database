package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Customer;
import com.DB.databaseproject.service.CustomerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for Customers Viewer (Read-Only)
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class CustomersController {
    
    private final CustomerService customerService = CustomerService.getInstance();

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Customer> customersTable;

    @FXML
    private TableColumn<Customer, Integer> idColumn;

    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    private TableColumn<Customer, String> phoneColumn;

    @FXML
    private TableColumn<Customer, String> addressColumn;

    private ObservableList<Customer> customersList;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Customers Viewer initialized (Read-Only)");

        // Initialize the customers list
        customersList = FXCollections.observableArrayList();

        // Configure table columns
        setupTableColumns();

        // Load sample data
        loadSampleData();

        // Set the data to the table ONCE - we never change this reference
        customersTable.setItems(customersList);

        // Make table read-only
        customersTable.setEditable(false);

        // Placeholder message when table is empty
        customersTable.setPlaceholder(new Label("No customers found"));

        // Make table responsive - columns auto-resize to fill available width
        customersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        customersTable.setPrefWidth(Double.MAX_VALUE);
        
        // Setup search field listener for real-time filtering
        setupSearchListener();
    }
    
    /**
     * Setup search field listener for real-time filtering
     */
    private void setupSearchListener() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                // Automatically trigger search when text changes
                onSearchCustomer();
            });
        }
    }

    /**
     * Setup table columns with cell value factories
     */
    private void setupTableColumns() {
        // Customer ID Column
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        // Full Name Column
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        // Phone Number Column
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // Address Column
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    /**
     * Load sample customer data (temporary for UI preview)
     * Later this will be replaced with PostgreSQL data
     */
    private void loadSampleData() {
        // Load customers from database
        customersList.clear();
        customersList.addAll(customerService.getAllCustomers());
        
        System.out.println("Customers loaded from database: " + customersList.size());
    }

    /**
     * Handle Search Customer button click
     * FIXED: Only filters the data, does NOT change table columns
     */
    @FXML
    private void onSearchCustomer() {
        String searchText = searchField.getText().trim();
        System.out.println("Search Customer: " + searchText);
        
        if (searchText.isEmpty()) {
            // Clear search - reload all customers from database and show them
            System.out.println("🔄 Search cleared - reloading all customers from database...");
            customersList.clear();
            customersList.addAll(customerService.getAllCustomers());
            System.out.println("✅ Showing all customers: " + customersList.size());
        } else {
            // Filter customers from the current list
            ObservableList<Customer> allCustomers = FXCollections.observableArrayList(customerService.getAllCustomers());
            customersList.clear();
            
            for (Customer customer : allCustomers) {
                if (customer.getFullName().toLowerCase().contains(searchText.toLowerCase()) ||
                    customer.getPhoneNumber().contains(searchText) ||
                    customer.getAddress().toLowerCase().contains(searchText.toLowerCase()) ||
                    String.valueOf(customer.getCustomerId()).contains(searchText)) {
                    customersList.add(customer);
                }
            }
            
            System.out.println("✅ Found " + customersList.size() + " customers matching: " + searchText);
        }
        
        // Force table refresh
        customersTable.refresh();
    }

    /**
     * Get the customers list
     * @return Observable list of customers
     */
    public ObservableList<Customer> getCustomersList() {
        return customersList;
    }
}

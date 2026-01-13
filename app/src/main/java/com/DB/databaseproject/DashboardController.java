package com.DB.databaseproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDate;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import com.DB.databaseproject.util.DBConnection;

/**
 * Controller for the Admin Dashboard
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class DashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox welcomeView;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button employeesBtn;

    @FXML
    private Button customersBtn;

    @FXML
    private Button stonesBtn;

    @FXML
    private Button ordersBtn;

    @FXML
    private Button customOrdersBtn;

    @FXML
    private Button archiveBtn;

    @FXML
    private Button generateReportBtn;

    @FXML
    private Button logoutBtn;
    
    // Quick Lookup UI components
    @FXML
    private ComboBox<String> searchModeCombo;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchBtn;
    
    @FXML
    private Button clearBtn;
    
    @FXML
    private VBox resultCard;
    
    @FXML
    private TableView<Object> resultsTable;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        // Dashboard button is already selected by default in FXML
        System.out.println("✅ AdminDashboardController loaded successfully (com.DB.databaseproject.DashboardController)");
        System.out.println("✅ Generate Report button initialized: " + (generateReportBtn != null));
        
        // Initialize Quick Lookup
        initializeQuickLookup();
    }
    
    /**
     * Initialize Quick Lookup components
     */
    private void initializeQuickLookup() {
        if (searchModeCombo != null) {
            // Setup search mode combo box
            searchModeCombo.setItems(FXCollections.observableArrayList("Order", "Employee", "Stone"));
            
            // Add listener to change prompt text based on mode
            searchModeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    setModeUI(newVal);
                }
            });
            
            System.out.println("✅ Quick Lookup initialized");
        }
    }

    /**
     * Show Dashboard (Welcome Screen with Quick Lookup)
     */
    @FXML
    private void showDashboard() {
        setActiveButton(dashboardBtn);
        
        // Bring welcomeView to front (contains Quick Lookup)
        if (welcomeView != null) {
            welcomeView.toFront();
            welcomeView.setVisible(true);
            welcomeView.setManaged(true);
        }
        
        System.out.println("✅ Dashboard view displayed (Quick Lookup preserved)");
    }

    /**
     * Show Employees Management View
     */
    @FXML
    private void showEmployees() {
        setActiveButton(employeesBtn);
        loadView("/fxml/employees_view.fxml");
    }

    /**
     * Show Customers Viewer (Read Only)
     */
    @FXML
    private void showCustomers() {
        setActiveButton(customersBtn);
        loadView("/fxml/customers_view.fxml");
    }

    /**
     * Show Stones Catalog Management View
     */
    @FXML
    private void showStones() {
        setActiveButton(stonesBtn);
        loadView("/fxml/stones_view.fxml");
    }

    /**
     * Show Orders Management View
     */
    @FXML
    private void showOrders() {
        setActiveButton(ordersBtn);
        loadView("/fxml/orders_view.fxml");
    }

    /**
     * Show Custom Orders Management View
     */
    @FXML
    private void showCustomOrders() {
        setActiveButton(customOrdersBtn);
        loadView("/fxml/custom_orders_view.fxml");
    }

    /**
     * Show Archive View (READ-ONLY)
     * Displays Completed and Canceled orders
     */
    @FXML
    private void showArchive() {
        setActiveButton(archiveBtn);
        loadView("/fxml/archive_view.fxml");
    }

    /**
     * Handle Generate Report - Generate and display JasperReport
     */
    @FXML
    private void handleGenerateReport() {
        System.out.println("🔷 Generate Report button clicked!");
        
        // Disable button to prevent multiple clicks
        generateReportBtn.setDisable(true);
        generateReportBtn.setText("Generating...");
        
        // Create background task to avoid freezing UI
        Task<JasperPrint> reportTask = new Task<JasperPrint>() {
            @Override
            protected JasperPrint call() throws Exception {
                Connection conn = null;
                InputStream reportStream = null;
                
                try {
                    System.out.println("🔷 Starting report generation...");
                    
                    // 1. Get database connection
                    conn = DBConnection.getConnection();
                    System.out.println("✅ Database connection established");
                    
                    // 2. Load JRXML file (try resources first, then file system)
                    reportStream = getClass().getResourceAsStream("/FinalReport.jrxml");
                    
                    if (reportStream == null) {
                        System.out.println("⚠️ Report not found in resources, trying file system...");
                        File reportFile = new File("FinalReport.jrxml");
                        if (reportFile.exists()) {
                            reportStream = new FileInputStream(reportFile);
                            System.out.println("✅ Report loaded from file system");
                            System.out.println("📄 JRXML Absolute Path: " + reportFile.getAbsolutePath());
                        } else {
                            throw new IOException("FinalReport.jrxml not found in resources or project root!");
                        }
                    } else {
                        System.out.println("✅ Report loaded from resources");
                        // Print the resource URL to show where it's being loaded from
                        java.net.URL resourceUrl = getClass().getResource("/FinalReport.jrxml");
                        if (resourceUrl != null) {
                            System.out.println("📄 JRXML Resource URL: " + resourceUrl);
                            // Try to get the actual file path if it's a file URL
                            try {
                                File resourceFile = new File(resourceUrl.toURI());
                                System.out.println("📄 JRXML Absolute Path: " + resourceFile.getAbsolutePath());
                            } catch (Exception e) {
                                System.out.println("📄 JRXML is loaded from JAR/classpath resource");
                            }
                        }
                    }
                    
                    // 3. Compile the report
                    System.out.println("🔷 Compiling report...");
                    JasperDesign design = JRXmlLoader.load(reportStream);
                    JasperReport report = JasperCompileManager.compileReport(design);
                    System.out.println("✅ Report compiled successfully");
                    
                    // 4. Fill the report with data
                    System.out.println("🔷 Filling report with data...");
                    Map<String, Object> parameters = new HashMap<>();
                    JasperPrint print = JasperFillManager.fillReport(report, parameters, conn);
                    System.out.println("✅ Report filled successfully");
                    
                    return print;
                    
                } finally {
                    // Close resources
                    if (reportStream != null) {
                        try {
                            reportStream.close();
                        } catch (IOException e) {
                            System.err.println("⚠️ Error closing report stream: " + e.getMessage());
                        }
                    }
                    if (conn != null) {
                        DBConnection.closeConnection(conn);
                    }
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    try {
                        JasperPrint print = getValue();
                        
                        // Show report in viewer window
                        System.out.println("🔷 Opening report viewer...");
                        JasperViewer.viewReport(print, false);
                        System.out.println("✅ Report viewer opened successfully");
                        
                        // Show success alert
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Report Generated");
                        alert.setHeaderText("Success!");
                        alert.setContentText("Admin Dashboard Report has been generated successfully.");
                        alert.showAndWait();
                        
                    } catch (Exception e) {
                        System.err.println("❌ Error displaying report: " + e.getMessage());
                        e.printStackTrace();
                        showErrorAlert("Error displaying report", e.getMessage());
                    } finally {
                        // Re-enable button
                        generateReportBtn.setDisable(false);
                        generateReportBtn.setText("Generate Report");
                    }
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable e = getException();
                    System.err.println("❌ Report generation failed: " + e.getMessage());
                    e.printStackTrace();
                    
                    showErrorAlert("Report Generation Failed", e.getMessage());
                    
                    // Re-enable button
                    generateReportBtn.setDisable(false);
                    generateReportBtn.setText("Generate Report");
                });
            }
        };
        
        // Start the background task
        Thread reportThread = new Thread(reportTask);
        reportThread.setDaemon(true);
        reportThread.start();
    }
    
    /**
     * Show error alert dialog
     */
    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handle Logout - Return to Login Screen
     */
    @FXML
    private void handleLogout() {
        try {
            System.out.println("Logging out...");
            
            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // Get current stage
            Stage stage = (Stage) logoutBtn.getScene().getWindow();

            // Create new scene with login (no fixed size)
            Scene scene = new Scene(root);
            
            // Load login CSS
            String css = getClass().getResource("/css/login.css").toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);

            // Set scene
            stage.setScene(scene);
            stage.setTitle("Stone Sales – Login");
            
            // Maximize window (keeps title bar and window decorations)
            stage.setResizable(true);
            
            // Maximize AFTER scene is set
            javafx.application.Platform.runLater(() -> {
                stage.setMaximized(true);
            });

            System.out.println("Logged out successfully. Returned to login screen in maximized mode with title bar.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading login screen: " + e.getMessage());
        }
    }

    /**
     * Load a view into the content area (StackPane approach)
     * @param fxmlPath Path to the FXML file
     */
    private void loadView(String fxmlPath) {
        try {
            // Load the FXML view
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Hide welcomeView (dashboard with Quick Lookup)
            if (welcomeView != null) {
                welcomeView.setVisible(false);
                welcomeView.setManaged(false);
            }
            
            // Remove any previously loaded views (except welcomeView)
            contentArea.getChildren().removeIf(node -> node != welcomeView);
            
            // Add new view to StackPane
            contentArea.getChildren().add(view);

            System.out.println("✅ Loaded view: " + fxmlPath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error loading view: " + fxmlPath);
            
            // Show error message in content area
            VBox errorBox = new VBox(10);
            errorBox.setAlignment(javafx.geometry.Pos.CENTER);
            
            Label errorLabel = new Label("Error loading view");
            errorLabel.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 18px; -fx-font-weight: bold;");
            
            Label detailLabel = new Label(fxmlPath);
            detailLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");
            
            errorBox.getChildren().addAll(errorLabel, detailLabel);
            
            // Hide welcomeView and show error
            if (welcomeView != null) {
                welcomeView.setVisible(false);
                welcomeView.setManaged(false);
            }
            
            contentArea.getChildren().removeIf(node -> node != welcomeView);
            contentArea.getChildren().add(errorBox);
        }
    }

    /**
     * Set the active button style
     * @param activeButton The button to mark as active
     */
    private void setActiveButton(Button activeButton) {
        // Remove selected class from all buttons
        dashboardBtn.getStyleClass().remove("selected");
        employeesBtn.getStyleClass().remove("selected");
        customersBtn.getStyleClass().remove("selected");
        stonesBtn.getStyleClass().remove("selected");
        ordersBtn.getStyleClass().remove("selected");
        customOrdersBtn.getStyleClass().remove("selected");
        archiveBtn.getStyleClass().remove("selected");
        // generateReportBtn is not a navigation button, so don't reset it

        // Add selected class to active button
        if (!activeButton.getStyleClass().contains("selected")) {
            activeButton.getStyleClass().add("selected");
        }
    }
    
    // ========================================
    // QUICK LOOKUP METHODS
    // ========================================
    
    /**
     * Set UI mode based on selected search type
     */
    private void setModeUI(String mode) {
        if (mode == null) return;
        
        switch (mode) {
            case "Order":
                searchField.setPromptText("Enter Order ID...");
                break;
            case "Employee":
                searchField.setPromptText("Enter Employee ID or Name...");
                break;
            case "Stone":
                searchField.setPromptText("Enter Stone ID or Stone Name...");
                break;
        }
        
        // Clear previous results
        clearResults();
    }
    
    /**
     * Handle Search button click
     */
    @FXML
    private void handleSearch() {
        String mode = searchModeCombo.getValue();
        String input = searchField.getText();
        
        // Validate input
        if (mode == null || mode.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Select Mode", "Please select a search mode (Order, Employee, or Stone)");
            return;
        }
        
        if (input == null || input.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Enter Search Term", "Please enter a search term");
            return;
        }
        
        // Disable buttons during search
        searchBtn.setDisable(true);
        clearBtn.setDisable(true);
        
        // Run search based on mode
        switch (mode) {
            case "Order":
                try {
                    int orderId = Integer.parseInt(input.trim());
                    runOrderLookup(orderId);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Order ID", "Order ID must be a number");
                    searchBtn.setDisable(false);
                    clearBtn.setDisable(false);
                }
                break;
            case "Employee":
                runEmployeeLookup(input.trim());
                break;
            case "Stone":
                runStoneLookup(input.trim());
                break;
        }
    }
    
    /**
     * Handle Clear button click
     */
    @FXML
    private void handleClear() {
        searchField.clear();
        searchModeCombo.setValue(null);
        clearResults();
    }
    
    /**
     * Clear all results
     */
    private void clearResults() {
        if (resultCard != null) {
            resultCard.getChildren().clear();
            resultCard.setVisible(false);
            resultCard.setManaged(false);
        }
        
        if (resultsTable != null) {
            resultsTable.getColumns().clear();
            resultsTable.getItems().clear();
            resultsTable.setVisible(false);
            resultsTable.setManaged(false);
        }
    }
    
    /**
     * Run Order Lookup by Order ID
     */
    private void runOrderLookup(int orderId) {
        Task<OrderLookupResult> task = new Task<OrderLookupResult>() {
            @Override
            protected OrderLookupResult call() throws Exception {
                Connection conn = null;
                try {
                    conn = DBConnection.getConnection();
                    OrderLookupResult result = new OrderLookupResult();
                    
                    // Query main order info
                    String orderQuery = "SELECT o.\"Order_ID\" AS order_id, o.\"Order_Date\" AS order_date, " +
                            "o.\"Order_Status\" AS order_status, o.\"Total_Amount\" AS total_amount, " +
                            "c.\"Customer_ID\" AS customer_id, (c.\"First_Name\" || ' ' || c.\"Last_Name\") AS customer_name, " +
                            "c.\"Phone_Number\" AS customer_phone, c.\"Address\" AS customer_address, " +
                            "e.\"Employee_ID\" AS employee_id, (e.\"First_Name\" || ' ' || e.\"Last_Name\") AS employee_name " +
                            "FROM \"Orders\" o " +
                            "JOIN \"Customer\" c ON o.\"Customer_ID\" = c.\"Customer_ID\" " +
                            "LEFT JOIN \"Employee\" e ON o.\"Employee_ID\" = e.\"Employee_ID\" " +
                            "WHERE o.\"Order_ID\" = ?";
                    
                    try (PreparedStatement pstmt = conn.prepareStatement(orderQuery)) {
                        pstmt.setInt(1, orderId);
                        ResultSet rs = pstmt.executeQuery();
                        
                        if (rs.next()) {
                            result.orderId = rs.getInt("order_id");
                            result.orderDate = rs.getDate("order_date") != null ? rs.getDate("order_date").toLocalDate() : null;
                            result.orderStatus = rs.getString("order_status");
                            result.totalAmount = rs.getBigDecimal("total_amount");
                            result.customerId = rs.getInt("customer_id");
                            result.customerName = rs.getString("customer_name");
                            result.customerPhone = rs.getString("customer_phone");
                            result.customerAddress = rs.getString("customer_address");
                            result.employeeId = rs.getInt("employee_id");
                            result.employeeName = rs.getString("employee_name");
                        } else {
                            return null; // Order not found
                        }
                    }
                    
                    // Query order items
                    String itemsQuery = "SELECT od.\"Stone_ID\" AS stone_id, s.\"Name\" AS stone_name, " +
                            "od.\"Quantity\" AS quantity, od.\"Unit_Price\" AS unit_price, " +
                            "(od.\"Quantity\" * od.\"Unit_Price\") AS subtotal " +
                            "FROM \"Order_Details\" od " +
                            "JOIN \"Stone\" s ON od.\"Stone_ID\" = s.\"Stone_ID\" " +
                            "WHERE od.\"Order_ID\" = ? " +
                            "ORDER BY od.\"Order_Detail_ID\"";
                    
                    try (PreparedStatement pstmt = conn.prepareStatement(itemsQuery)) {
                        pstmt.setInt(1, orderId);
                        ResultSet rs = pstmt.executeQuery();
                        
                        while (rs.next()) {
                            OrderItemRow item = new OrderItemRow(
                                rs.getInt("stone_id"),
                                rs.getString("stone_name"),
                                rs.getInt("quantity"),
                                rs.getBigDecimal("unit_price"),
                                rs.getBigDecimal("subtotal")
                            );
                            result.items.add(item);
                        }
                    }
                    
                    return result;
                    
                } finally {
                    if (conn != null) DBConnection.closeConnection(conn);
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    OrderLookupResult result = getValue();
                    if (result == null) {
                        showAlert(Alert.AlertType.INFORMATION, "No Results", "Order not found");
                    } else {
                        displayOrderResult(result);
                    }
                    searchBtn.setDisable(false);
                    clearBtn.setDisable(false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Search Failed", getException().getMessage());
                    searchBtn.setDisable(false);
                    clearBtn.setDisable(false);
                });
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Display Order Lookup Result
     */
    private void displayOrderResult(OrderLookupResult result) {
        // Clear previous results
        clearResults();
        
        // Build result card
        resultCard.getChildren().clear();
        
        Label titleLabel = new Label("Order Details");
        titleLabel.getStyleClass().add("result-card-title");
        
        // Order info
        Label orderInfo = new Label(String.format("Order ID: %d  |  Date: %s  |  Status: %s  |  Total: $%.2f",
                result.orderId, result.orderDate, result.orderStatus, result.totalAmount));
        orderInfo.getStyleClass().add("result-card-label");
        
        // Customer info
        Label customerTitle = new Label("Customer Information");
        customerTitle.getStyleClass().add("result-section-title");
        
        Label customerInfo = new Label(String.format("ID: %d  |  Name: %s\nPhone: %s\nAddress: %s",
                result.customerId, result.customerName, result.customerPhone, result.customerAddress));
        customerInfo.getStyleClass().add("result-card-label");
        
        // Employee info
        Label employeeTitle = new Label("Assigned Employee");
        employeeTitle.getStyleClass().add("result-section-title");
        
        String empInfo = result.employeeName != null && !result.employeeName.isEmpty() 
                ? String.format("ID: %d  |  Name: %s", result.employeeId, result.employeeName)
                : "Not Assigned";
        Label employeeInfo = new Label(empInfo);
        employeeInfo.getStyleClass().add("result-card-label");
        
        resultCard.getChildren().addAll(titleLabel, orderInfo, customerTitle, customerInfo, employeeTitle, employeeInfo);
        resultCard.setVisible(true);
        resultCard.setManaged(true);
        
        // Setup table for order items
        resultsTable.getColumns().clear();
        
        TableColumn<Object, Integer> stoneIdCol = new TableColumn<>("Stone ID");
        stoneIdCol.setCellValueFactory(new PropertyValueFactory<>("stoneId"));
        stoneIdCol.setPrefWidth(100);
        
        TableColumn<Object, String> stoneNameCol = new TableColumn<>("Stone Name");
        stoneNameCol.setCellValueFactory(new PropertyValueFactory<>("stoneName"));
        stoneNameCol.setPrefWidth(200);
        
        TableColumn<Object, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(100);
        
        TableColumn<Object, BigDecimal> unitPriceCol = new TableColumn<>("Unit Price");
        unitPriceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        unitPriceCol.setPrefWidth(120);
        
        TableColumn<Object, BigDecimal> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        subtotalCol.setPrefWidth(120);
        
        resultsTable.getColumns().addAll(stoneIdCol, stoneNameCol, quantityCol, unitPriceCol, subtotalCol);
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        resultsTable.setItems(FXCollections.observableArrayList(result.items));
        resultsTable.setVisible(true);
        resultsTable.setManaged(true);
    }
    
    /**
     * Run Employee Lookup by ID or Name
     */
    private void runEmployeeLookup(String input) {
        Task<EmployeeLookupResult> task = new Task<EmployeeLookupResult>() {
            @Override
            protected EmployeeLookupResult call() throws Exception {
                Connection conn = null;
                try {
                    conn = DBConnection.getConnection();
                    EmployeeLookupResult result = new EmployeeLookupResult();
                    
                    // Check if input is numeric (Employee ID)
                    boolean isNumeric = input.matches("\\d+");
                    
                    if (isNumeric) {
                        // Search by Employee ID
                        int employeeId = Integer.parseInt(input);
                        String query = "SELECT e.\"Employee_ID\", e.\"First_Name\", e.\"Middle_Name\", e.\"Last_Name\", " +
                                "e.\"Phone_Number\", e.\"Address\" " +
                                "FROM \"Employee\" e WHERE e.\"Employee_ID\" = ?";
                        
                        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                            pstmt.setInt(1, employeeId);
                            ResultSet rs = pstmt.executeQuery();
                            
                            if (rs.next()) {
                                result.employeeId = rs.getInt("Employee_ID");
                                result.firstName = rs.getString("First_Name");
                                result.middleName = rs.getString("Middle_Name");
                                result.lastName = rs.getString("Last_Name");
                                result.phone = rs.getString("Phone_Number");
                                result.address = rs.getString("Address");
                                result.singleResult = true;
                            } else {
                                return null;
                            }
                        }
                    } else {
                        // Search by name using ILIKE
                        String namePattern = "%" + input + "%";
                        String query = "SELECT e.\"Employee_ID\", " +
                                "(e.\"First_Name\" || ' ' || COALESCE(e.\"Middle_Name\", '') || ' ' || e.\"Last_Name\") AS full_name, " +
                                "e.\"Phone_Number\", e.\"Address\" " +
                                "FROM \"Employee\" e " +
                                "WHERE (e.\"First_Name\" ILIKE ? OR COALESCE(e.\"Middle_Name\", '') ILIKE ? OR e.\"Last_Name\" ILIKE ?) " +
                                "ORDER BY e.\"Employee_ID\" LIMIT 20";
                        
                        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                            pstmt.setString(1, namePattern);
                            pstmt.setString(2, namePattern);
                            pstmt.setString(3, namePattern);
                            ResultSet rs = pstmt.executeQuery();
                            
                            while (rs.next()) {
                                EmployeeSearchRow emp = new EmployeeSearchRow(
                                    rs.getInt("Employee_ID"),
                                    rs.getString("full_name"),
                                    rs.getString("Phone_Number"),
                                    rs.getString("Address")
                                );
                                result.employees.add(emp);
                            }
                            
                            if (result.employees.isEmpty()) {
                                return null;
                            }
                            
                            if (result.employees.size() == 1) {
                                // Auto-select single result
                                result.singleResult = true;
                                result.employeeId = result.employees.get(0).getEmployeeId();
                                result.firstName = result.employees.get(0).getFullName();
                            } else {
                                result.singleResult = false;
                            }
                        }
                    }
                    
                    // If single employee, get assigned orders
                    if (result.singleResult && result.employeeId > 0) {
                        String ordersQuery = "SELECT o.\"Order_ID\" AS order_id, " +
                                "(c.\"First_Name\" || ' ' || c.\"Last_Name\") AS customer_name, " +
                                "o.\"Order_Status\" AS status, o.\"Order_Date\" AS order_date, " +
                                "o.\"Total_Amount\" AS total_amount " +
                                "FROM \"Orders\" o " +
                                "JOIN \"Customer\" c ON o.\"Customer_ID\" = c.\"Customer_ID\" " +
                                "WHERE o.\"Employee_ID\" = ? " +
                                "ORDER BY o.\"Order_Date\" DESC";
                        
                        try (PreparedStatement pstmt = conn.prepareStatement(ordersQuery)) {
                            pstmt.setInt(1, result.employeeId);
                            ResultSet rs = pstmt.executeQuery();
                            
                            while (rs.next()) {
                                AssignedOrderRow order = new AssignedOrderRow(
                                    rs.getInt("order_id"),
                                    rs.getString("customer_name"),
                                    rs.getString("status"),
                                    rs.getDate("order_date") != null ? rs.getDate("order_date").toLocalDate() : null,
                                    rs.getBigDecimal("total_amount")
                                );
                                result.assignedOrders.add(order);
                            }
                        }
                    }
                    
                    return result;
                    
                } finally {
                    if (conn != null) DBConnection.closeConnection(conn);
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    EmployeeLookupResult result = getValue();
                    if (result == null) {
                        showAlert(Alert.AlertType.INFORMATION, "No Results", "Employee not found");
                    } else {
                        displayEmployeeResult(result);
                    }
                    searchBtn.setDisable(false);
                    clearBtn.setDisable(false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Search Failed", getException().getMessage());
                    searchBtn.setDisable(false);
                    clearBtn.setDisable(false);
                });
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Display Employee Lookup Result
     */
    private void displayEmployeeResult(EmployeeLookupResult result) {
        clearResults();
        
        if (!result.singleResult) {
            // Show multiple employees in table
            Label titleLabel = new Label("Multiple Employees Found");
            titleLabel.getStyleClass().add("result-card-title");
            
            Label infoLabel = new Label("Select an employee to view their assigned orders");
            infoLabel.getStyleClass().add("result-card-label");
            
            resultCard.getChildren().addAll(titleLabel, infoLabel);
            resultCard.setVisible(true);
            resultCard.setManaged(true);
            
            // Setup table for employees
            resultsTable.getColumns().clear();
            
            TableColumn<Object, Integer> idCol = new TableColumn<>("Employee ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
            idCol.setPrefWidth(120);
            
            TableColumn<Object, String> nameCol = new TableColumn<>("Full Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            nameCol.setPrefWidth(250);
            
            TableColumn<Object, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
            phoneCol.setPrefWidth(150);
            
            TableColumn<Object, String> addressCol = new TableColumn<>("Address");
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
            addressCol.setPrefWidth(220);
            
            resultsTable.getColumns().addAll(idCol, nameCol, phoneCol, addressCol);
            resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            resultsTable.setItems(FXCollections.observableArrayList(result.employees));
            resultsTable.setVisible(true);
            resultsTable.setManaged(true);
            
        } else {
            // Show single employee with assigned orders
            resultCard.getChildren().clear();
            
            Label titleLabel = new Label("Employee Details");
            titleLabel.getStyleClass().add("result-card-title");
            
            String fullName = result.firstName + (result.middleName != null && !result.middleName.isEmpty() ? " " + result.middleName : "") + " " + result.lastName;
            Label empInfo = new Label(String.format("ID: %d  |  Name: %s\nPhone: %s\nAddress: %s",
                    result.employeeId, fullName, result.phone, result.address));
            empInfo.getStyleClass().add("result-card-label");
            
            Label ordersTitle = new Label("Assigned Orders (" + result.assignedOrders.size() + ")");
            ordersTitle.getStyleClass().add("result-section-title");
            
            resultCard.getChildren().addAll(titleLabel, empInfo, ordersTitle);
            resultCard.setVisible(true);
            resultCard.setManaged(true);
            
            // Setup table for assigned orders
            resultsTable.getColumns().clear();
            
            TableColumn<Object, Integer> orderIdCol = new TableColumn<>("Order ID");
            orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
            orderIdCol.setPrefWidth(100);
            
            TableColumn<Object, String> customerCol = new TableColumn<>("Customer Name");
            customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            customerCol.setPrefWidth(200);
            
            TableColumn<Object, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusCol.setPrefWidth(120);
            
            TableColumn<Object, LocalDate> dateCol = new TableColumn<>("Order Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
            dateCol.setPrefWidth(120);
            
            TableColumn<Object, BigDecimal> amountCol = new TableColumn<>("Total Amount");
            amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            amountCol.setPrefWidth(120);
            
            resultsTable.getColumns().addAll(orderIdCol, customerCol, statusCol, dateCol, amountCol);
            resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            resultsTable.setItems(FXCollections.observableArrayList(result.assignedOrders));
            resultsTable.setVisible(true);
            resultsTable.setManaged(true);
        }
    }
    
    /**
     * Run Stone Lookup by ID or Name
     */
    private void runStoneLookup(String input) {
        Task<StoneLookupResult> task = new Task<StoneLookupResult>() {
            @Override
            protected StoneLookupResult call() throws Exception {
                Connection conn = null;
                try {
                    conn = DBConnection.getConnection();
                    StoneLookupResult result = new StoneLookupResult();
                    
                    // Check if input is numeric (Stone ID)
                    boolean isNumeric = input.matches("\\d+");
                    
                    if (isNumeric) {
                        // Search by Stone ID
                        int stoneId = Integer.parseInt(input);
                        String query = "SELECT s.\"Stone_ID\", s.\"Name\", s.\"Type\", s.\"Size\", " +
                                "s.\"Price_Per_Unit\", s.\"Quantity_In_Stock\" " +
                                "FROM \"Stone\" s WHERE s.\"Stone_ID\" = ?";
                        
                        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                            pstmt.setInt(1, stoneId);
                            ResultSet rs = pstmt.executeQuery();
                            
                            if (rs.next()) {
                                result.stoneId = rs.getInt("Stone_ID");
                                result.stoneName = rs.getString("Name");
                                result.stoneType = rs.getString("Type");
                                result.stoneSize = rs.getString("Size");
                                result.price = rs.getBigDecimal("Price_Per_Unit");
                                result.quantity = rs.getInt("Quantity_In_Stock");
                                result.singleResult = true;
                            } else {
                                return null;
                            }
                        }
                    } else {
                        // Search by name using ILIKE
                        String namePattern = "%" + input + "%";
                        String query = "SELECT s.\"Stone_ID\", s.\"Name\", s.\"Type\", s.\"Size\", " +
                                "s.\"Price_Per_Unit\", s.\"Quantity_In_Stock\" " +
                                "FROM \"Stone\" s " +
                                "WHERE s.\"Name\" ILIKE ? " +
                                "ORDER BY s.\"Stone_ID\" LIMIT 20";
                        
                        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                            pstmt.setString(1, namePattern);
                            ResultSet rs = pstmt.executeQuery();
                            
                            while (rs.next()) {
                                StoneSearchRow stone = new StoneSearchRow(
                                    rs.getInt("Stone_ID"),
                                    rs.getString("Name"),
                                    rs.getString("Type"),
                                    rs.getString("Size"),
                                    rs.getBigDecimal("Price_Per_Unit"),
                                    rs.getInt("Quantity_In_Stock")
                                );
                                result.stones.add(stone);
                            }
                            
                            if (result.stones.isEmpty()) {
                                return null;
                            }
                            
                            if (result.stones.size() == 1) {
                                // Auto-select single result
                                result.singleResult = true;
                                StoneSearchRow stone = result.stones.get(0);
                                result.stoneId = stone.getStoneId();
                                result.stoneName = stone.getStoneName();
                                result.stoneType = stone.getStoneType();
                                result.stoneSize = stone.getStoneSize();
                                result.price = stone.getPrice();
                                result.quantity = stone.getQuantity();
                            } else {
                                result.singleResult = false;
                            }
                        }
                    }
                    
                    // If single stone, get stats and recent usage
                    if (result.singleResult && result.stoneId > 0) {
                        // Get stats
                        String statsQuery = "SELECT COUNT(DISTINCT od.\"Order_ID\") AS orders_count, " +
                                "COALESCE(SUM(od.\"Quantity\"),0) AS total_quantity_sold " +
                                "FROM \"Order_Details\" od " +
                                "WHERE od.\"Stone_ID\" = ?";
                        
                        try (PreparedStatement pstmt = conn.prepareStatement(statsQuery)) {
                            pstmt.setInt(1, result.stoneId);
                            ResultSet rs = pstmt.executeQuery();
                            
                            if (rs.next()) {
                                result.ordersCount = rs.getInt("orders_count");
                                result.totalQuantitySold = rs.getInt("total_quantity_sold");
                            }
                        }
                        
                        // Get recent usage
                        String usageQuery = "SELECT o.\"Order_ID\" AS order_id, " +
                                "(c.\"First_Name\" || ' ' || c.\"Last_Name\") AS customer_name, " +
                                "od.\"Quantity\" AS quantity, od.\"Unit_Price\" AS unit_price, " +
                                "o.\"Order_Date\" AS order_date, o.\"Order_Status\" AS status " +
                                "FROM \"Order_Details\" od " +
                                "JOIN \"Orders\" o ON od.\"Order_ID\" = o.\"Order_ID\" " +
                                "JOIN \"Customer\" c ON o.\"Customer_ID\" = c.\"Customer_ID\" " +
                                "WHERE od.\"Stone_ID\" = ? " +
                                "ORDER BY o.\"Order_Date\" DESC LIMIT 20";
                        
                        try (PreparedStatement pstmt = conn.prepareStatement(usageQuery)) {
                            pstmt.setInt(1, result.stoneId);
                            ResultSet rs = pstmt.executeQuery();
                            
                            while (rs.next()) {
                                StoneOrderRow order = new StoneOrderRow(
                                    rs.getInt("order_id"),
                                    rs.getString("customer_name"),
                                    rs.getInt("quantity"),
                                    rs.getBigDecimal("unit_price"),
                                    rs.getDate("order_date") != null ? rs.getDate("order_date").toLocalDate() : null,
                                    rs.getString("status")
                                );
                                result.recentOrders.add(order);
                            }
                        }
                    }
                    
                    return result;
                    
                } finally {
                    if (conn != null) DBConnection.closeConnection(conn);
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    StoneLookupResult result = getValue();
                    if (result == null) {
                        showAlert(Alert.AlertType.INFORMATION, "No Results", "Stone not found");
                    } else {
                        displayStoneResult(result);
                    }
                    searchBtn.setDisable(false);
                    clearBtn.setDisable(false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Search Failed", getException().getMessage());
                    searchBtn.setDisable(false);
                    clearBtn.setDisable(false);
                });
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Display Stone Lookup Result
     */
    private void displayStoneResult(StoneLookupResult result) {
        clearResults();
        
        if (!result.singleResult) {
            // Show multiple stones in table
            Label titleLabel = new Label("Multiple Stones Found");
            titleLabel.getStyleClass().add("result-card-title");
            
            Label infoLabel = new Label("Select a stone to view its usage history");
            infoLabel.getStyleClass().add("result-card-label");
            
            resultCard.getChildren().addAll(titleLabel, infoLabel);
            resultCard.setVisible(true);
            resultCard.setManaged(true);
            
            // Setup table for stones
            resultsTable.getColumns().clear();
            
            TableColumn<Object, Integer> idCol = new TableColumn<>("Stone ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("stoneId"));
            idCol.setPrefWidth(100);
            
            TableColumn<Object, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("stoneName"));
            nameCol.setPrefWidth(180);
            
            TableColumn<Object, String> typeCol = new TableColumn<>("Type");
            typeCol.setCellValueFactory(new PropertyValueFactory<>("stoneType"));
            typeCol.setPrefWidth(120);
            
            TableColumn<Object, String> sizeCol = new TableColumn<>("Size");
            sizeCol.setCellValueFactory(new PropertyValueFactory<>("stoneSize"));
            sizeCol.setPrefWidth(100);
            
            TableColumn<Object, BigDecimal> priceCol = new TableColumn<>("Price");
            priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
            priceCol.setPrefWidth(100);
            
            TableColumn<Object, Integer> qtyCol = new TableColumn<>("Stock");
            qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            qtyCol.setPrefWidth(80);
            
            resultsTable.getColumns().addAll(idCol, nameCol, typeCol, sizeCol, priceCol, qtyCol);
            resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            resultsTable.setItems(FXCollections.observableArrayList(result.stones));
            resultsTable.setVisible(true);
            resultsTable.setManaged(true);
            
        } else {
            // Show single stone with stats and recent usage
            resultCard.getChildren().clear();
            
            Label titleLabel = new Label("Stone Details");
            titleLabel.getStyleClass().add("result-card-title");
            
            Label stoneInfo = new Label(String.format("ID: %d  |  Name: %s  |  Type: %s\nSize: %s  |  Price: $%.2f  |  Stock: %d",
                    result.stoneId, result.stoneName, result.stoneType, result.stoneSize, result.price, result.quantity));
            stoneInfo.getStyleClass().add("result-card-label");
            
            Label statsTitle = new Label("Statistics");
            statsTitle.getStyleClass().add("result-section-title");
            
            Label statsInfo = new Label(String.format("Total Orders: %d  |  Total Quantity Sold: %d",
                    result.ordersCount, result.totalQuantitySold));
            statsInfo.getStyleClass().add("result-card-label");
            
            Label ordersTitle = new Label("Recent Orders (" + result.recentOrders.size() + ")");
            ordersTitle.getStyleClass().add("result-section-title");
            
            resultCard.getChildren().addAll(titleLabel, stoneInfo, statsTitle, statsInfo, ordersTitle);
            resultCard.setVisible(true);
            resultCard.setManaged(true);
            
            // Setup table for recent orders
            resultsTable.getColumns().clear();
            
            TableColumn<Object, Integer> orderIdCol = new TableColumn<>("Order ID");
            orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
            orderIdCol.setPrefWidth(100);
            
            TableColumn<Object, String> customerCol = new TableColumn<>("Customer");
            customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            customerCol.setPrefWidth(180);
            
            TableColumn<Object, Integer> qtyCol = new TableColumn<>("Quantity");
            qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            qtyCol.setPrefWidth(100);
            
            TableColumn<Object, BigDecimal> priceCol = new TableColumn<>("Unit Price");
            priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
            priceCol.setPrefWidth(120);
            
            TableColumn<Object, LocalDate> dateCol = new TableColumn<>("Order Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
            dateCol.setPrefWidth(120);
            
            TableColumn<Object, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusCol.setPrefWidth(120);
            
            resultsTable.getColumns().addAll(orderIdCol, customerCol, qtyCol, priceCol, dateCol, statusCol);
            resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            resultsTable.setItems(FXCollections.observableArrayList(result.recentOrders));
            resultsTable.setVisible(true);
            resultsTable.setManaged(true);
        }
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ========================================
    // DTO CLASSES FOR TABLE ROWS
    // ========================================
    
    /**
     * Order Lookup Result Container
     */
    private static class OrderLookupResult {
        int orderId;
        LocalDate orderDate;
        String orderStatus;
        BigDecimal totalAmount;
        int customerId;
        String customerName;
        String customerPhone;
        String customerAddress;
        int employeeId;
        String employeeName;
        ObservableList<OrderItemRow> items = FXCollections.observableArrayList();
    }
    
    /**
     * Order Item Row for TableView
     */
    public static class OrderItemRow {
        private final IntegerProperty stoneId;
        private final StringProperty stoneName;
        private final IntegerProperty quantity;
        private final ObjectProperty<BigDecimal> unitPrice;
        private final ObjectProperty<BigDecimal> subtotal;
        
        public OrderItemRow(int stoneId, String stoneName, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
            this.stoneId = new SimpleIntegerProperty(stoneId);
            this.stoneName = new SimpleStringProperty(stoneName);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.unitPrice = new SimpleObjectProperty<>(unitPrice);
            this.subtotal = new SimpleObjectProperty<>(subtotal);
        }
        
        public int getStoneId() { return stoneId.get(); }
        public String getStoneName() { return stoneName.get(); }
        public int getQuantity() { return quantity.get(); }
        public BigDecimal getUnitPrice() { return unitPrice.get(); }
        public BigDecimal getSubtotal() { return subtotal.get(); }
    }
    
    /**
     * Employee Lookup Result Container
     */
    private static class EmployeeLookupResult {
        boolean singleResult;
        int employeeId;
        String firstName;
        String middleName;
        String lastName;
        String phone;
        String address;
        ObservableList<EmployeeSearchRow> employees = FXCollections.observableArrayList();
        ObservableList<AssignedOrderRow> assignedOrders = FXCollections.observableArrayList();
    }
    
    /**
     * Employee Search Row for TableView
     */
    public static class EmployeeSearchRow {
        private final IntegerProperty employeeId;
        private final StringProperty fullName;
        private final StringProperty phone;
        private final StringProperty address;
        
        public EmployeeSearchRow(int employeeId, String fullName, String phone, String address) {
            this.employeeId = new SimpleIntegerProperty(employeeId);
            this.fullName = new SimpleStringProperty(fullName);
            this.phone = new SimpleStringProperty(phone);
            this.address = new SimpleStringProperty(address);
        }
        
        public int getEmployeeId() { return employeeId.get(); }
        public String getFullName() { return fullName.get(); }
        public String getPhone() { return phone.get(); }
        public String getAddress() { return address.get(); }
    }
    
    /**
     * Assigned Order Row for TableView
     */
    public static class AssignedOrderRow {
        private final IntegerProperty orderId;
        private final StringProperty customerName;
        private final StringProperty status;
        private final ObjectProperty<LocalDate> orderDate;
        private final ObjectProperty<BigDecimal> totalAmount;
        
        public AssignedOrderRow(int orderId, String customerName, String status, LocalDate orderDate, BigDecimal totalAmount) {
            this.orderId = new SimpleIntegerProperty(orderId);
            this.customerName = new SimpleStringProperty(customerName);
            this.status = new SimpleStringProperty(status);
            this.orderDate = new SimpleObjectProperty<>(orderDate);
            this.totalAmount = new SimpleObjectProperty<>(totalAmount);
        }
        
        public int getOrderId() { return orderId.get(); }
        public String getCustomerName() { return customerName.get(); }
        public String getStatus() { return status.get(); }
        public LocalDate getOrderDate() { return orderDate.get(); }
        public BigDecimal getTotalAmount() { return totalAmount.get(); }
    }
    
    /**
     * Stone Lookup Result Container
     */
    private static class StoneLookupResult {
        boolean singleResult;
        int stoneId;
        String stoneName;
        String stoneType;
        String stoneSize;
        BigDecimal price;
        int quantity;
        int ordersCount;
        int totalQuantitySold;
        ObservableList<StoneSearchRow> stones = FXCollections.observableArrayList();
        ObservableList<StoneOrderRow> recentOrders = FXCollections.observableArrayList();
    }
    
    /**
     * Stone Search Row for TableView
     */
    public static class StoneSearchRow {
        private final IntegerProperty stoneId;
        private final StringProperty stoneName;
        private final StringProperty stoneType;
        private final StringProperty stoneSize;
        private final ObjectProperty<BigDecimal> price;
        private final IntegerProperty quantity;
        
        public StoneSearchRow(int stoneId, String stoneName, String stoneType, String stoneSize, BigDecimal price, int quantity) {
            this.stoneId = new SimpleIntegerProperty(stoneId);
            this.stoneName = new SimpleStringProperty(stoneName);
            this.stoneType = new SimpleStringProperty(stoneType);
            this.stoneSize = new SimpleStringProperty(stoneSize);
            this.price = new SimpleObjectProperty<>(price);
            this.quantity = new SimpleIntegerProperty(quantity);
        }
        
        public int getStoneId() { return stoneId.get(); }
        public String getStoneName() { return stoneName.get(); }
        public String getStoneType() { return stoneType.get(); }
        public String getStoneSize() { return stoneSize.get(); }
        public BigDecimal getPrice() { return price.get(); }
        public int getQuantity() { return quantity.get(); }
    }
    
    /**
     * Stone Order Row for TableView
     */
    public static class StoneOrderRow {
        private final IntegerProperty orderId;
        private final StringProperty customerName;
        private final IntegerProperty quantity;
        private final ObjectProperty<BigDecimal> unitPrice;
        private final ObjectProperty<LocalDate> orderDate;
        private final StringProperty status;
        
        public StoneOrderRow(int orderId, String customerName, int quantity, BigDecimal unitPrice, LocalDate orderDate, String status) {
            this.orderId = new SimpleIntegerProperty(orderId);
            this.customerName = new SimpleStringProperty(customerName);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.unitPrice = new SimpleObjectProperty<>(unitPrice);
            this.orderDate = new SimpleObjectProperty<>(orderDate);
            this.status = new SimpleStringProperty(status);
        }
        
        public int getOrderId() { return orderId.get(); }
        public String getCustomerName() { return customerName.get(); }
        public int getQuantity() { return quantity.get(); }
        public BigDecimal getUnitPrice() { return unitPrice.get(); }
        public LocalDate getOrderDate() { return orderDate.get(); }
        public String getStatus() { return status.get(); }
    }
}
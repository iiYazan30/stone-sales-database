package com.DB.databaseproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import com.DB.databaseproject.util.DBConnection;

/**
 * Controller for the Admin Dashboard
 * Stone Sales Management System
 */
public class DashboardController {

    @FXML
    private StackPane contentArea;

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

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        // Set Dashboard as the active button by default
        setActiveButton(dashboardBtn);
    }

    /**
     * Show Dashboard (Welcome Screen)
     */
    @FXML
    private void showDashboard() {
        setActiveButton(dashboardBtn);
        // Show welcome screen (already loaded in FXML)
        loadView("/fxml/admin_dashboard.fxml", true);
    }

    /**
     * Show Employees Management View
     */
    @FXML
    private void showEmployees() {
        setActiveButton(employeesBtn);
        loadView("/fxml/employees_view.fxml", false);
    }

    /**
     * Show Customers Viewer (Read Only)
     */
    @FXML
    private void showCustomers() {
        setActiveButton(customersBtn);
        loadView("/fxml/customers_view.fxml", false);
    }

    /**
     * Show Stones Catalog Management View
     */
    @FXML
    private void showStones() {
        setActiveButton(stonesBtn);
        loadView("/fxml/stones_view.fxml", false);
    }

    /**
     * Show Orders Management View
     */
    @FXML
    private void showOrders() {
        setActiveButton(ordersBtn);
        loadView("/fxml/orders_view.fxml", false);
    }

    /**
     * Show Custom Orders Management View
     */
    @FXML
    private void showCustomOrders() {
        setActiveButton(customOrdersBtn);
        loadView("/fxml/custom_orders_view.fxml", false);
    }

    /**
     * Show Archive View
     */
    @FXML
    private void showArchive() {
        setActiveButton(archiveBtn);
        loadView("/fxml/archive_view.fxml", false);
    }

    /**
     * Handle Generate Report - Generate and display JasperReport
     */
    @FXML
    private void handleGenerateReport() {
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
            
            // Force exact screen size using visual bounds (no gaps on sides)
            javafx.application.Platform.runLater(() -> {
                javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
            });

            System.out.println("Logged out successfully - login screen at exact screen size with title bar");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading login screen: " + e.getMessage());
        }
    }

    /**
     * Load a view into the content area
     * @param fxmlPath Path to the FXML file
     * @param isWelcome True if showing welcome screen (skip loading)
     */
    private void loadView(String fxmlPath, boolean isWelcome) {
        if (isWelcome) {
            // Dashboard/Welcome screen is already in the FXML, just clear and reload default
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_dashboard.fxml"));
                Parent dashboardRoot = loader.load();
                
                // Extract just the center content (welcome screen)
                contentArea.getChildren().clear();
                
                // Create welcome screen
                javafx.scene.layout.VBox welcomeBox = new javafx.scene.layout.VBox(20);
                welcomeBox.setAlignment(javafx.geometry.Pos.CENTER);
                
                javafx.scene.control.Label title = new javafx.scene.control.Label("Welcome Admin");
                title.getStyleClass().add("welcome-title");
                
                javafx.scene.control.Label subtitle = new javafx.scene.control.Label("Stone Sales Management System");
                subtitle.getStyleClass().add("welcome-subtitle");
                
                javafx.scene.control.Label tagline = new javafx.scene.control.Label("Premium Dark Edition");
                tagline.getStyleClass().add("welcome-tagline");
                
                welcomeBox.getChildren().addAll(title, subtitle, tagline);
                contentArea.getChildren().add(welcomeBox);
                
            } catch (IOException e) {
                System.err.println("Error loading dashboard: " + e.getMessage());
            }
            return;
        }

        try {
            // Load the FXML view
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Clear content area and add new view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlPath);
            
            // Show error message in content area
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "Error loading view: " + fxmlPath
            );
            errorLabel.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 16px;");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(errorLabel);
        }
    }

    /**
     * Set the active button style
     * @param activeButton The button to mark as active
     */
    private void setActiveButton(Button activeButton) {
        // Remove selected class from all buttons
        dashboardBtn.getStyleClass().remove("nav-button-selected");
        employeesBtn.getStyleClass().remove("nav-button-selected");
        customersBtn.getStyleClass().remove("nav-button-selected");
        stonesBtn.getStyleClass().remove("nav-button-selected");
        ordersBtn.getStyleClass().remove("nav-button-selected");
        customOrdersBtn.getStyleClass().remove("nav-button-selected");

        // Add selected class to active button
        if (!activeButton.getStyleClass().contains("nav-button-selected")) {
            activeButton.getStyleClass().add("nav-button-selected");
        }
    }
}

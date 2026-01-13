package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.OrderDetails;
import com.DB.databaseproject.model.Stone;
import com.DB.databaseproject.service.AuthenticationService;
import com.DB.databaseproject.service.OrderService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Customer Order Popup
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class CustomerOrderPopupController {
    
    private final OrderService orderService = OrderService.getInstance();
    private final AuthenticationService authService = AuthenticationService.getInstance();

    @FXML
    private ImageView stoneImageView;

    @FXML
    private Label stoneNameLabel;

    @FXML
    private Label stoneTypeLabel;

    @FXML
    private Label stoneSizeLabel;

    @FXML
    private Label stonePriceLabel;

    @FXML
    private Spinner<Integer> quantitySpinner;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    private Stone selectedStone;
    private boolean orderConfirmed = false;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Order Popup initialized");
    }

    /**
     * Set stone data for the popup
     * @param stone Stone object to display
     */
    public void setStone(Stone stone) {
        this.selectedStone = stone;

        // Set stone image
        try {
            var imageStream = getClass().getResourceAsStream(stone.getImagePath());
            if (imageStream != null) {
                stoneImageView.setImage(new Image(imageStream));
            }
        } catch (Exception e) {
            System.err.println("Error loading stone image: " + e.getMessage());
        }

        // Set stone details
        stoneNameLabel.setText(stone.getName());
        stoneTypeLabel.setText("Type: " + stone.getType());
        stoneSizeLabel.setText("Size: " + stone.getSize());
        stonePriceLabel.setText(String.format("Price: $%.2f per unit", stone.getPricePerUnit()));

        // Setup quantity spinner with stock limits
        // REQUIREMENT: minimum = 1, maximum = QuantityInStock from Stone table
        int maxQuantity = stone.getQuantityInStock();
        if (maxQuantity < 1) {
            maxQuantity = 1; // Failsafe - should not happen if only showing in-stock items
        }
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxQuantity, 1);
        quantitySpinner.setValueFactory(valueFactory);

        // Listen for quantity changes and update total
        // REQUIREMENT: Automatically update "Total Price" as user changes quantity
        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateTotal();
        });

        // Initialize total
        updateTotal();
    }

    /**
     * Update total price based on quantity
     */
    private void updateTotal() {
        if (selectedStone != null && quantitySpinner.getValue() != null) {
            int quantity = quantitySpinner.getValue();
            double total = quantity * selectedStone.getPricePerUnit();
            totalPriceLabel.setText(String.format("$%.2f", total));
        }
    }

    /**
     * Handle Confirm Order button click
     * SYSTEM BEHAVIOR:
     * 1. Validate quantity (cannot exceed stock)
     * 2. Insert into Orders: Customer_ID, Order_Date=NOW(), Total_Amount, Status='Completed', Employee_ID=NULL
     * 3. Insert into Order_Details: Order_ID, Stone_ID, Quantity, Price_Per_Unit, Subtotal
     * 4. Update Stone: QuantityInStock = QuantityInStock - ordered quantity
     */
    @FXML
    private void handleConfirmOrder() {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║      CONFIRM ORDER - DEBUG INFO                 ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println("🔍 Checking authentication state...");
        System.out.println("   AuthService instance: " + authService);
        System.out.println("   Current User: " + authService.getCurrentUser());
        System.out.println("   Current Customer: " + authService.getCurrentCustomer());
        
        if (selectedStone == null) {
            showError("Error", "No Stone Selected", "Please select a stone to order.");
            return;
        }
        
        // Check if customer is logged in
        if (authService.getCurrentCustomer() == null) {
            System.err.println("❌ Cannot place order: No customer logged in");
            
            // Try to check if there's a current user but customer object is missing
            if (authService.getCurrentUser() != null && 
                "Customer".equalsIgnoreCase(authService.getCurrentUser().getRole())) {
                System.err.println("⚠️ WARNING: User is logged in but customer object is null!");
                System.err.println("   This indicates the customer data was not loaded during login.");
                System.err.println("   User ID: " + authService.getCurrentUser().getUserId());
                System.err.println("   Username: " + authService.getCurrentUser().getUserName());
                System.err.println("   Attempting to reload customer data...");
                
                // Try to reload customer data as a fallback
                if (authService.reloadCustomerData()) {
                    System.out.println("✅ Customer data reloaded successfully - continuing with order");
                } else {
                    System.err.println("❌ Failed to reload customer data");
                    showError("Error", "Session Error", "Unable to retrieve your customer information. Please log out and log in again.");
                    return;
                }
            } else {
                showError("Error", "Not Logged In", "Please log in to place an order.");
                return;
            }
        }
        
        System.out.println("✅ Customer authenticated: " + authService.getCurrentCustomer().getFullName());
        
        int quantity = quantitySpinner.getValue();
        
        // REQUIREMENT: Validate quantity (cannot exceed stock)
        if (quantity > selectedStone.getQuantityInStock()) {
            System.err.println("❌ Quantity exceeds stock: " + quantity + " > " + selectedStone.getQuantityInStock());
            showError("Invalid Quantity", "Insufficient Stock", 
                     "The requested quantity (" + quantity + ") exceeds available stock (" + 
                     selectedStone.getQuantityInStock() + ").");
            return;
        }
        
        if (quantity < 1) {
            showError("Invalid Quantity", "Invalid Quantity", "Quantity must be at least 1.");
            return;
        }
        
        double total = quantity * selectedStone.getPricePerUnit();

        // Create order details list
        List<OrderDetails> orderDetailsList = new ArrayList<>();
        OrderDetails orderDetail = new OrderDetails(
            0, // order_id will be set by service
            selectedStone.getStoneId(),
            selectedStone.getName(),
            quantity,
            selectedStone.getPricePerUnit()
        );
        orderDetailsList.add(orderDetail);

        // Create order in database
        // REQUIREMENT: Customer orders are created with Status='Completed' and Employee_ID=NULL
        int customerId = authService.getCurrentCustomer().getCustomerId();
        int orderId = orderService.createCustomerOrder(customerId, orderDetailsList);

        if (orderId > 0) {
            // REQUIREMENT: Order created successfully - all DB operations complete
            System.out.println("✅ Customer order placed successfully!");
            System.out.println("Order ID: " + orderId);
            System.out.println("Customer ID: " + customerId);
            System.out.println("Stone: " + selectedStone.getName() + " (ID: " + selectedStone.getStoneId() + ")");
            System.out.println("Quantity: " + quantity);
            System.out.println("Total Amount: $" + String.format("%.2f", total));
            System.out.println("Status: Completed");
            System.out.println("Employee_ID: NULL (customer self-service order)");

            // REQUIREMENT: Show confirmation alert with custom styling
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Successful");
            alert.setHeaderText("✓ Order Placed Successfully!");
            alert.setContentText("Your order has been placed.\n\n" +
                               "Order ID: " + orderId + "\n" +
                               "Stone: " + selectedStone.getName() + "\n" +
                               "Quantity: " + quantity + " units\n" +
                               "Total: $" + String.format("%.2f", total));
            
            // Apply custom dark theme styling to match the application design
            applyCustomAlertStyling(alert);
            alert.showAndWait();

            orderConfirmed = true;
            // REQUIREMENT: Close the popup
            closePopup();
        } else {
            // Order failed
            System.err.println("❌ Failed to place order");
            showError("Order Failed", "Order Could Not Be Placed", 
                     "There was an error processing your order.\nPlease try again or contact support.");
        }
    }
    
    /**
     * Helper method to show error alerts with custom styling
     */
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        // Apply custom dark theme styling to match the application design
        applyCustomAlertStyling(alert);
        alert.showAndWait();
    }

    /**
     * Handle Cancel button click
     */
    @FXML
    private void handleCancelOrder() {
        System.out.println("Order cancelled");
        orderConfirmed = false;
        closePopup();
    }

    /**
     * Close the popup window
     */
    private void closePopup() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Check if order was confirmed
     * @return true if order was confirmed, false otherwise
     */
    public boolean isOrderConfirmed() {
        return orderConfirmed;
    }

    /**
     * Get the ordered quantity
     * @return quantity selected
     */
    public int getOrderedQuantity() {
        return quantitySpinner.getValue();
    }
    
    /**
     * Apply custom dark theme styling to alert dialog
     * Ensures maximum text readability with proper contrast on dark background
     * Color scheme: Dark charcoal background with gold titles and light gray body text
     */
    private void applyCustomAlertStyling(Alert alert) {
        javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
        
        // Dialog pane background - dark charcoal with gold border
        dialogPane.setStyle(
            "-fx-background-color: #151515;" +  // Dark charcoal background
            "-fx-border-color: #C9B06B;" +      // Gold/beige border
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        
        // Style the header panel background
        javafx.scene.Node headerPanel = dialogPane.lookup(".header-panel");
        if (headerPanel != null) {
            headerPanel.setStyle(
                "-fx-background-color: #151515;" +  // Same dark background
                "-fx-border-radius: 10 10 0 0;" +
                "-fx-background-radius: 10 10 0 0;"
            );
        }
        
        // Style header text (main title) - PURE WHITE for maximum visibility
        javafx.scene.Node headerLabel = dialogPane.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle(
                "-fx-text-fill: #FFFFFF;" +  // Pure white - MAXIMUM CONTRAST
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
            );
        }
        
        // Style ALL labels in header panel - ensure title is always pure white
        dialogPane.lookupAll(".header-panel .label").forEach(node -> {
            node.setStyle(
                "-fx-text-fill: #FFFFFF;" +  // Pure white
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
            );
        });
        
        // Style content text - PURE WHITE for body text (order details)
        javafx.scene.Node contentLabel = dialogPane.lookup(".content .label");
        if (contentLabel != null) {
            contentLabel.setStyle(
                "-fx-text-fill: #FFFFFF;" +  // Pure white - CRYSTAL CLEAR
                "-fx-font-size: 14px;" +
                "-fx-font-weight: normal;"
            );
        }
        
        // Style ALL labels in the content area - body text in PURE WHITE
        dialogPane.lookupAll(".content .label").forEach(node -> {
            node.setStyle(
                "-fx-text-fill: #FFFFFF;" +  // Pure white for maximum readability
                "-fx-font-size: 14px;" +
                "-fx-font-weight: normal;"
            );
        });
        
        // Style the content area background - same dark charcoal
        javafx.scene.Node content = dialogPane.lookup(".content");
        if (content != null) {
            content.setStyle(
                "-fx-background-color: #151515;" +  // Dark charcoal
                "-fx-padding: 20;"
            );
        }
        
        // Style any Text nodes in content area (for rich text content)
        dialogPane.lookupAll(".content .text").forEach(node -> {
            node.setStyle(
                "-fx-fill: #FFFFFF;" +  // Pure white for Text nodes - MAXIMUM VISIBILITY
                "-fx-font-size: 14px;"
            );
        });
        
        // Style buttons - gold background with dark text (app standard)
        for (javafx.scene.Node node : dialogPane.getButtonTypes().stream()
                .map(buttonType -> dialogPane.lookupButton(buttonType))
                .toList()) {
            if (node instanceof javafx.scene.control.Button button) {
                button.setStyle(
                    "-fx-background-color: #C9B06B;" +  // Gold/beige background
                    "-fx-text-fill: #151515;" +         // Dark charcoal text for maximum contrast
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 10 30;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;" +
                    "-fx-min-width: 100;"
                );
                
                // Add hover effect - slightly lighter gold
                button.setOnMouseEntered(e -> button.setStyle(
                    "-fx-background-color: #D4BD7D;" +  // Lighter gold on hover
                    "-fx-text-fill: #151515;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 10 30;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;" +
                    "-fx-min-width: 100;"
                ));
                
                button.setOnMouseExited(e -> button.setStyle(
                    "-fx-background-color: #C9B06B;" +  // Back to standard gold
                    "-fx-text-fill: #151515;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 10 30;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;" +
                    "-fx-min-width: 100;"
                ));
            }
        }
    }
    
}

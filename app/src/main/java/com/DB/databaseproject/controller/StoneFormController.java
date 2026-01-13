package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Stone;
import com.DB.databaseproject.service.StoneService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.function.Consumer;

/**
 * Controller for Stone Form (Add/Edit)
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class StoneFormController {

    @FXML private Label formTitle;
    
    // Stone Information Fields
    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField sizeField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextField imagePathField;
    @FXML private Button browseButton;
    
    // Action Buttons
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    // Service
    private final StoneService stoneService = StoneService.getInstance();
    
    // Edit mode tracking
    private boolean isEditMode = false;
    private int editStoneId = -1;
    
    // Callback to refresh parent table
    private Consumer<Boolean> onSaveCallback;

    /**
     * Initialize method - called after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Stone Form initialized");
        
        // Add input validation listeners
        setupValidationListeners();
    }

    /**
     * Setup real-time validation listeners
     */
    private void setupValidationListeners() {
        // Price - only numbers and decimal point
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                priceField.setText(oldValue);
            }
        });
        
        // Quantity - only integers
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantityField.setText(oldValue);
            }
        });
    }

    /**
     * Set callback for when save is successful
     */
    public void setOnSaveCallback(Consumer<Boolean> callback) {
        this.onSaveCallback = callback;
    }

    /**
     * Configure form for ADD mode
     */
    public void setAddMode() {
        isEditMode = false;
        formTitle.setText("Add New Stone");
        System.out.println("Form configured for ADD mode");
    }

    /**
     * Configure form for EDIT mode
     */
    public void setEditMode(Stone stone) {
        isEditMode = true;
        editStoneId = stone.getStoneId();
        
        formTitle.setText("Edit Stone");
        
        System.out.println("Form configured for EDIT mode");
        System.out.println("  Stone ID: " + editStoneId);
        
        // Load stone data
        loadStoneData(stone);
    }

    /**
     * Load stone data into form fields
     */
    private void loadStoneData(Stone stone) {
        nameField.setText(stone.getName());
        typeField.setText(stone.getType());
        sizeField.setText(stone.getSize());
        priceField.setText(String.valueOf(stone.getPricePerUnit()));
        quantityField.setText(String.valueOf(stone.getQuantityInStock()));
        imagePathField.setText(stone.getImagePath() != null ? stone.getImagePath() : "");
        
        System.out.println("✅ Stone data loaded into form");
    }

    /**
     * Handle Browse button click for image selection
     */
    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Stone Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        Stage stage = (Stage) browseButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
            System.out.println("Image selected: " + selectedFile.getAbsolutePath());
        }
    }

    /**
     * Handle Save button click
     */
    @FXML
    private void handleSave() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         STONE FORM - SAVE OPERATION                         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Mode: " + (isEditMode ? "EDIT" : "ADD"));
        
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        if (isEditMode) {
            updateStone();
        } else {
            addNewStone();
        }
    }

    /**
     * Validate all input fields
     */
    private boolean validateInput() {
        System.out.println("🔍 Validating input...");
        
        StringBuilder errors = new StringBuilder();
        
        // Required fields
        if (nameField.getText().trim().isEmpty()) {
            errors.append("• Stone Name is required\n");
        }
        if (typeField.getText().trim().isEmpty()) {
            errors.append("• Stone Type is required\n");
        }
        if (sizeField.getText().trim().isEmpty()) {
            errors.append("• Size is required\n");
        }
        
        // Price validation
        String priceText = priceField.getText().trim();
        if (priceText.isEmpty()) {
            errors.append("• Price Per Unit is required\n");
        } else {
            try {
                double price = Double.parseDouble(priceText);
                if (price < 0) {
                    errors.append("• Price must be a positive number\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Price must be a valid number\n");
            }
        }
        
        // Quantity validation
        String quantityText = quantityField.getText().trim();
        if (quantityText.isEmpty()) {
            errors.append("• Quantity in Stock is required\n");
        } else {
            try {
                int quantity = Integer.parseInt(quantityText);
                if (quantity < 0) {
                    errors.append("• Quantity must be a positive number\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Quantity must be a valid integer\n");
            }
        }
        
        if (errors.length() > 0) {
            System.err.println("❌ Validation failed:");
            System.err.println(errors.toString());
            showError("Validation Error", errors.toString());
            return false;
        }
        
        System.out.println("✅ Validation passed");
        return true;
    }

    /**
     * Add new stone (INSERT mode)
     */
    private void addNewStone() {
        System.out.println("\n📋 ADD NEW STONE - Starting...");
        
        try {
            // Create Stone object
            Stone stone = new Stone();
            stone.setName(nameField.getText().trim());
            stone.setType(typeField.getText().trim());
            stone.setSize(sizeField.getText().trim());
            stone.setPricePerUnit(Double.parseDouble(priceField.getText().trim()));
            stone.setQuantityInStock(Integer.parseInt(quantityField.getText().trim()));
            stone.setImagePath(imagePathField.getText().trim());
            
            System.out.println("📝 Stone object created:");
            System.out.println("   Name: " + stone.getName());
            System.out.println("   Type: " + stone.getType());
            System.out.println("   Size: " + stone.getSize());
            System.out.println("   Price: $" + stone.getPricePerUnit());
            System.out.println("   Quantity: " + stone.getQuantityInStock());
            
            // Insert into database
            int stoneId = stoneService.addStone(stone);
            
            if (stoneId > 0) {
                System.out.println("\n✅ ✅ ✅ ADD STONE SUCCESSFUL! ✅ ✅ ✅");
                System.out.println("   Stone_ID: " + stoneId);
                System.out.println("╚════════════════════════════════════════════════════════════╝\n");
                
                // Show success message
                showSuccess("Stone Added", 
                           "Stone " + stone.getName() + " has been successfully added to the catalog.");
                
                // Callback to refresh parent table
                if (onSaveCallback != null) {
                    onSaveCallback.accept(true);
                }
                
                // Close the form
                closeForm();
            } else {
                System.err.println("\n❌ ❌ ❌ STONE ADD ERROR ❌ ❌ ❌");
                System.err.println("   Failed to add stone - no Stone_ID returned");
                System.err.println("╚════════════════════════════════════════════════════════════╝\n");
                showError("Error", "Failed to add stone. Please try again.");
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ ❌ ❌ UNEXPECTED ERROR! ❌ ❌ ❌");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("╚════════════════════════════════════════════════════════════╝\n");
            e.printStackTrace();
            showError("Error", "An unexpected error occurred:\n" + e.getMessage());
        }
    }

    /**
     * Update existing stone (EDIT mode)
     */
    private void updateStone() {
        System.out.println("\n📋 UPDATE STONE - Starting...");
        System.out.println("   Stone_ID: " + editStoneId);
        
        try {
            // Create Stone object
            Stone stone = new Stone();
            stone.setStoneId(editStoneId);
            stone.setName(nameField.getText().trim());
            stone.setType(typeField.getText().trim());
            stone.setSize(sizeField.getText().trim());
            stone.setPricePerUnit(Double.parseDouble(priceField.getText().trim()));
            stone.setQuantityInStock(Integer.parseInt(quantityField.getText().trim()));
            stone.setImagePath(imagePathField.getText().trim());
            
            System.out.println("📝 Updating stone in database...");
            boolean success = stoneService.updateStone(stone);
            
            if (success) {
                System.out.println("\n✅ ✅ ✅ UPDATE STONE SUCCESSFUL! ✅ ✅ ✅");
                System.out.println("   Stone_ID: " + editStoneId);
                System.out.println("╚════════════════════════════════════════════════════════════╝\n");
                
                // Show success message
                showSuccess("Stone Updated", 
                           "Stone " + stone.getName() + " has been successfully updated.");
                
                // Callback to refresh parent table
                if (onSaveCallback != null) {
                    onSaveCallback.accept(true);
                }
                
                // Close the form
                closeForm();
            } else {
                System.err.println("\n❌ Failed to update stone");
                showError("Error", "Failed to update stone information.");
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ ❌ ❌ UNEXPECTED ERROR! ❌ ❌ ❌");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("╚════════════════════════════════════════════════════════════╝\n");
            e.printStackTrace();
            showError("Error", "An unexpected error occurred:\n" + e.getMessage());
        }
    }

    /**
     * Handle Cancel button click
     */
    @FXML
    private void handleCancel() {
        System.out.println("❌ User cancelled the form");
        closeForm();
    }

    /**
     * Close the form window
     */
    private void closeForm() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Show error alert with custom styling
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2b2b2b;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #ffffff;");
        
        alert.showAndWait();
    }

    /**
     * Show success alert with custom styling
     */
    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("✅ Success");
        alert.setContentText(content);
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2b2b2b;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #ffffff;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #d4c59e;");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: #2b2b2b;");
        
        alert.showAndWait();
    }
}

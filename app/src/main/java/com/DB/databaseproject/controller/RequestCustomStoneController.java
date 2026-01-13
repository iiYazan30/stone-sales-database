package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.CustomOrder;
import com.DB.databaseproject.service.AuthenticationService;
import com.DB.databaseproject.service.CustomOrderService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RequestCustomStoneController {
    
    @FXML
    private TextField stoneTypeField;
    
    @FXML
    private TextField sizeField;
    
    @FXML
    private Spinner<Integer> quantitySpinner;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private Button submitButton;
    
    @FXML
    private Button cancelButton;
    
    private final CustomOrderService customOrderService = CustomOrderService.getInstance();
    private final AuthenticationService authService = AuthenticationService.getInstance();
    
    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1);
        quantitySpinner.setValueFactory(valueFactory);
        quantitySpinner.setEditable(true);
    }
    
    @FXML
    private void handleSubmit() {
        String stoneType = stoneTypeField.getText().trim();
        String size = sizeField.getText().trim();
        Integer quantity = quantitySpinner.getValue();
        String description = descriptionArea.getText().trim();
        
        if (stoneType.isEmpty()) {
            showError("Stone Type is required");
            return;
        }
        
        if (size.isEmpty()) {
            showError("Size is required");
            return;
        }
        
        if (description.isEmpty()) {
            showError("Description is required");
            return;
        }
        
        if (quantity == null || quantity < 1) {
            showError("Quantity must be at least 1");
            return;
        }
        
        CustomOrder customOrder = new CustomOrder();
        customOrder.setStoneType(stoneType);
        customOrder.setSize(size);
        customOrder.setQuantity(quantity);
        customOrder.setStoneDescription(description);
        
        int customerId = authService.getCurrentCustomer().getCustomerId();
        boolean success = customOrderService.submitCustomOrder(customOrder, customerId);
        
        if (success) {
            showSuccess("Custom order request submitted successfully!\nOur admin will review your request.");
            closeDialog();
        } else {
            showError("Failed to submit custom order request.\nPlease try again.");
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
    
    private void showError(String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initOwner(submitButton.getScene().getWindow());
        dialogStage.setTitle("Error");
        dialogStage.setResizable(false);

        VBox content = new VBox(20);
        content.setPadding(new javafx.geometry.Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #2A2A2A;");

        Label icon = new Label("✕");
        icon.setStyle("-fx-font-size: 48px; -fx-text-fill: #F56C6C;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        messageLabel.setAlignment(Pos.CENTER);

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
    
    private void showSuccess(String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initOwner(submitButton.getScene().getWindow());
        dialogStage.setTitle("Success");
        dialogStage.setResizable(false);

        VBox content = new VBox(20);
        content.setPadding(new javafx.geometry.Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #2A2A2A;");

        Label icon = new Label("✓");
        icon.setStyle("-fx-font-size: 48px; -fx-text-fill: #4CAF50;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        messageLabel.setAlignment(Pos.CENTER);

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

        javafx.scene.Scene scene = new javafx.scene.Scene(content, 400, 250);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
}

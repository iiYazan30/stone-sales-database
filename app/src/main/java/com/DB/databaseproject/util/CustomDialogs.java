package com.DB.databaseproject.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Custom Dialog Utility for themed dialogs
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class CustomDialogs {
    
    // Theme Colors
    private static final String DARK_BG = "#1e1e1e";
    private static final String GOLD_ACCENT = "#d6c28a";
    private static final String RED_DELETE = "#c0392b";
    private static final String GRAY_CANCEL = "#555555";
    private static final String WHITE_TEXT = "#ffffff";
    private static final String HOVER_RED = "#e74c3c";
    private static final String HOVER_GRAY = "#666666";
    
    /**
     * Show custom delete confirmation dialog
     * @param owner Parent window
     * @param employeeName Name of employee to delete
     * @param employeeId ID of employee to delete
     * @return true if user confirmed deletion, false otherwise
     */
    public static boolean showDeleteConfirmation(Stage owner, String employeeName, int employeeId) {
        final boolean[] confirmed = {false};
        
        // Create custom dialog stage
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Delete Employee");
        
        // Main container
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: " + DARK_BG + "; " +
                     "-fx-background-radius: 12; " +
                     "-fx-border-color: " + GOLD_ACCENT + "; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 20, 0, 0, 5);");
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setPrefWidth(550);
        
        // Warning Icon and Header
        Label warningIcon = new Label("⚠️");
        warningIcon.setStyle("-fx-font-size: 48px;");
        warningIcon.setAlignment(Pos.CENTER);
        
        Label headerLabel = new Label("Delete Employee: " + employeeName);
        headerLabel.setStyle("-fx-text-fill: " + GOLD_ACCENT + "; " +
                           "-fx-font-size: 22px; " +
                           "-fx-font-weight: bold;");
        headerLabel.setWrapText(true);
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setAlignment(Pos.CENTER);
        
        Label subHeader = new Label("This action cannot be undone!");
        subHeader.setStyle("-fx-text-fill: " + RED_DELETE + "; " +
                         "-fx-font-size: 16px; " +
                         "-fx-font-weight: bold;");
        subHeader.setAlignment(Pos.CENTER);
        
        // Header container
        VBox headerBox = new VBox(10, warningIcon, headerLabel, subHeader);
        headerBox.setAlignment(Pos.CENTER);
        
        // Warning message
        Label messageLabel = new Label(
            "⚠️  WARNING: This will permanently delete:\n\n" +
            "• Employee record (ID: " + employeeId + ")\n" +
            "• Associated User account\n" +
            "• All related data\n\n" +
            "This action CANNOT be undone!\n\n" +
            "Are you sure you want to continue?"
        );
        messageLabel.setStyle("-fx-text-fill: " + WHITE_TEXT + "; " +
                            "-fx-font-size: 14px; " +
                            "-fx-line-spacing: 3px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Message container with background
        VBox messageBox = new VBox(messageLabel);
        messageBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                          "-fx-background-radius: 8; " +
                          "-fx-border-color: rgba(214, 194, 138, 0.3); " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 8;");
        messageBox.setPadding(new Insets(20));
        
        // Buttons
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle(
            "-fx-background-color: " + RED_DELETE + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);"
        );
        deleteButton.setPrefWidth(140);
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
            "-fx-background-color: " + HOVER_RED + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 8, 0, 0, 3);"
        ));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
            "-fx-background-color: " + RED_DELETE + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);"
        ));
        deleteButton.setOnAction(e -> {
            confirmed[0] = true;
            dialog.close();
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle(
            "-fx-background-color: " + GRAY_CANCEL + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: normal; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30;"
        );
        cancelButton.setPrefWidth(140);
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(
            "-fx-background-color: " + HOVER_GRAY + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: normal; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30;"
        ));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(
            "-fx-background-color: " + GRAY_CANCEL + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: normal; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30;"
        ));
        cancelButton.setOnAction(e -> {
            confirmed[0] = false;
            dialog.close();
        });
        
        // Button container
        HBox buttonBox = new HBox(20, cancelButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Add all to root
        root.getChildren().addAll(headerBox, messageBox, buttonBox);
        
        // Create scene and show
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(scene);
        
        // Center dialog
        if (owner != null) {
            dialog.setX(owner.getX() + (owner.getWidth() - root.getPrefWidth()) / 2);
            dialog.setY(owner.getY() + (owner.getHeight() - 400) / 2);
        }
        
        dialog.showAndWait();
        
        return confirmed[0];
    }
    
    /**
     * Show custom success dialog for employee deletion
     * @param owner Parent window
     * @param employeeName Name of deleted employee
     * @param employeeId ID of deleted employee
     */
    public static void showDeleteSuccess(Stage owner, String employeeName, int employeeId) {
        // Create custom dialog stage
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Deletion Successful");
        
        // Main container
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: " + DARK_BG + "; " +
                     "-fx-background-radius: 12; " +
                     "-fx-border-color: " + GOLD_ACCENT + "; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 20, 0, 0, 5);");
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setPrefWidth(550);
        
        // Success Icon and Header
        Label successIcon = new Label("✅");
        successIcon.setStyle("-fx-font-size: 48px;");
        successIcon.setAlignment(Pos.CENTER);
        
        Label headerLabel = new Label("Employee Deleted Successfully");
        headerLabel.setStyle("-fx-text-fill: " + GOLD_ACCENT + "; " +
                           "-fx-font-size: 22px; " +
                           "-fx-font-weight: bold;");
        headerLabel.setWrapText(true);
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setAlignment(Pos.CENTER);
        
        // Header container
        VBox headerBox = new VBox(10, successIcon, headerLabel);
        headerBox.setAlignment(Pos.CENTER);
        
        // Success message
        Label messageLabel = new Label(
            "Employee: " + employeeName + "\n" +
            "Employee ID: " + employeeId + "\n\n" +
            "✓ Employee record removed\n" +
            "✓ User account removed\n" +
            "✓ All orders unassigned from this employee\n\n" +
            "Now the admin can reassign these orders to another employee."
        );
        messageLabel.setStyle("-fx-text-fill: " + WHITE_TEXT + "; " +
                            "-fx-font-size: 14px; " +
                            "-fx-line-spacing: 3px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Message container with background
        VBox messageBox = new VBox(messageLabel);
        messageBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                          "-fx-background-radius: 8; " +
                          "-fx-border-color: rgba(214, 194, 138, 0.3); " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 8;");
        messageBox.setPadding(new Insets(20));
        
        // OK Button
        Button okButton = new Button("OK");
        okButton.setStyle(
            "-fx-background-color: " + DARK_BG + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-border-color: " + GOLD_ACCENT + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8;"
        );
        okButton.setPrefWidth(140);
        okButton.setOnMouseEntered(e -> okButton.setStyle(
            "-fx-background-color: " + GOLD_ACCENT + "; " +
            "-fx-text-fill: " + DARK_BG + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-border-color: " + GOLD_ACCENT + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(214, 194, 138, 0.5), 8, 0, 0, 3);"
        ));
        okButton.setOnMouseExited(e -> okButton.setStyle(
            "-fx-background-color: " + DARK_BG + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-border-color: " + GOLD_ACCENT + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8;"
        ));
        okButton.setOnAction(e -> dialog.close());
        
        // Button container
        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Add all to root
        root.getChildren().addAll(headerBox, messageBox, buttonBox);
        
        // Create scene and show
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(scene);
        
        // Center dialog
        if (owner != null) {
            dialog.setX(owner.getX() + (owner.getWidth() - root.getPrefWidth()) / 2);
            dialog.setY(owner.getY() + (owner.getHeight() - 450) / 2);
        }
        
        dialog.showAndWait();
    }
    
    /**
     * Show custom delete confirmation dialog for stone
     * @param owner Parent window
     * @param stoneName Name of stone to delete
     * @param stoneId ID of stone to delete
     * @param orderCount Number of orders using this stone
     * @return true if user confirmed deletion, false otherwise
     */
    public static boolean showStoneDeleteConfirmation(Stage owner, String stoneName, int stoneId, int orderCount) {
        final boolean[] confirmed = {false};
        
        // Create custom dialog stage
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Delete Stone");
        
        // Main container
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: " + DARK_BG + "; " +
                     "-fx-background-radius: 12; " +
                     "-fx-border-color: " + GOLD_ACCENT + "; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 20, 0, 0, 5);");
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setPrefWidth(550);
        
        // Warning Icon and Header
        Label warningIcon = new Label("⚠️");
        warningIcon.setStyle("-fx-font-size: 48px;");
        warningIcon.setAlignment(Pos.CENTER);
        
        Label headerLabel = new Label("Delete Stone: " + stoneName);
        headerLabel.setStyle("-fx-text-fill: " + GOLD_ACCENT + "; " +
                           "-fx-font-size: 22px; " +
                           "-fx-font-weight: bold;");
        headerLabel.setWrapText(true);
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setAlignment(Pos.CENTER);
        
        Label subHeader = new Label("This action cannot be undone!");
        subHeader.setStyle("-fx-text-fill: " + RED_DELETE + "; " +
                         "-fx-font-size: 16px; " +
                         "-fx-font-weight: bold;");
        subHeader.setAlignment(Pos.CENTER);
        
        // Header container
        VBox headerBox = new VBox(10, warningIcon, headerLabel, subHeader);
        headerBox.setAlignment(Pos.CENTER);
        
        // Warning message
        String orderWarning = orderCount > 0 ? 
            "\n⚠️  This stone is currently used in " + orderCount + " order(s)!\n" +
            "• These orders will have the stone unassigned (Stone_ID set to NULL)\n" : 
            "";
        
        Label messageLabel = new Label(
            "⚠️  WARNING: This will permanently delete:\n\n" +
            "• Stone record (ID: " + stoneId + ")\n" +
            "• Stone name: " + stoneName + "\n" +
            orderWarning +
            "\nThis action CANNOT be undone!\n\n" +
            "Are you sure you want to continue?"
        );
        messageLabel.setStyle("-fx-text-fill: " + WHITE_TEXT + "; " +
                            "-fx-font-size: 14px; " +
                            "-fx-line-spacing: 3px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Message container with background
        VBox messageBox = new VBox(messageLabel);
        messageBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                          "-fx-background-radius: 8; " +
                          "-fx-border-color: rgba(214, 194, 138, 0.3); " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 8;");
        messageBox.setPadding(new Insets(20));
        
        // Buttons
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle(
            "-fx-background-color: " + RED_DELETE + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);"
        );
        deleteButton.setPrefWidth(140);
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
            "-fx-background-color: " + HOVER_RED + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 8, 0, 0, 3);"
        ));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
            "-fx-background-color: " + RED_DELETE + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);"
        ));
        deleteButton.setOnAction(e -> {
            confirmed[0] = true;
            dialog.close();
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle(
            "-fx-background-color: " + GRAY_CANCEL + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: normal; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30;"
        );
        cancelButton.setPrefWidth(140);
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(
            "-fx-background-color: " + HOVER_GRAY + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: normal; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30;"
        ));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(
            "-fx-background-color: " + GRAY_CANCEL + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: normal; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30;"
        ));
        cancelButton.setOnAction(e -> {
            confirmed[0] = false;
            dialog.close();
        });
        
        // Button container
        HBox buttonBox = new HBox(20, cancelButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Add all to root
        root.getChildren().addAll(headerBox, messageBox, buttonBox);
        
        // Create scene and show
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(scene);
        
        // Center dialog
        if (owner != null) {
            dialog.setX(owner.getX() + (owner.getWidth() - root.getPrefWidth()) / 2);
            dialog.setY(owner.getY() + (owner.getHeight() - 450) / 2);
        }
        
        dialog.showAndWait();
        
        return confirmed[0];
    }
    
    /**
     * Show custom success dialog for stone deletion
     * @param owner Parent window
     * @param stoneName Name of deleted stone
     * @param stoneId ID of deleted stone
     * @param orderCount Number of orders that were updated
     */
    public static void showStoneDeleteSuccess(Stage owner, String stoneName, int stoneId, int orderCount) {
        // Create custom dialog stage
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Deletion Successful");
        
        // Main container
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: " + DARK_BG + "; " +
                     "-fx-background-radius: 12; " +
                     "-fx-border-color: " + GOLD_ACCENT + "; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 20, 0, 0, 5);");
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setPrefWidth(550);
        
        // Success Icon and Header
        Label successIcon = new Label("✅");
        successIcon.setStyle("-fx-font-size: 48px;");
        successIcon.setAlignment(Pos.CENTER);
        
        Label headerLabel = new Label("Stone Deleted Successfully");
        headerLabel.setStyle("-fx-text-fill: " + GOLD_ACCENT + "; " +
                           "-fx-font-size: 22px; " +
                           "-fx-font-weight: bold;");
        headerLabel.setWrapText(true);
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setAlignment(Pos.CENTER);
        
        // Header container
        VBox headerBox = new VBox(10, successIcon, headerLabel);
        headerBox.setAlignment(Pos.CENTER);
        
        // Success message
        String orderMessage = orderCount > 0 ?
            "✓ " + orderCount + " order(s) updated (stone unassigned)\n" :
            "";
        
        Label messageLabel = new Label(
            "Stone: " + stoneName + "\n" +
            "Stone ID: " + stoneId + "\n\n" +
            "✓ Stone record removed from catalog\n" +
            orderMessage +
            "\nThe stone has been successfully removed from the system."
        );
        messageLabel.setStyle("-fx-text-fill: " + WHITE_TEXT + "; " +
                            "-fx-font-size: 14px; " +
                            "-fx-line-spacing: 3px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Message container with background
        VBox messageBox = new VBox(messageLabel);
        messageBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                          "-fx-background-radius: 8; " +
                          "-fx-border-color: rgba(214, 194, 138, 0.3); " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 8;");
        messageBox.setPadding(new Insets(20));
        
        // OK Button
        Button okButton = new Button("OK");
        okButton.setStyle(
            "-fx-background-color: " + DARK_BG + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-border-color: " + GOLD_ACCENT + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8;"
        );
        okButton.setPrefWidth(140);
        okButton.setOnMouseEntered(e -> okButton.setStyle(
            "-fx-background-color: " + GOLD_ACCENT + "; " +
            "-fx-text-fill: " + DARK_BG + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-border-color: " + GOLD_ACCENT + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(214, 194, 138, 0.5), 8, 0, 0, 3);"
        ));
        okButton.setOnMouseExited(e -> okButton.setStyle(
            "-fx-background-color: " + DARK_BG + "; " +
            "-fx-text-fill: " + WHITE_TEXT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-border-color: " + GOLD_ACCENT + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8;"
        ));
        okButton.setOnAction(e -> dialog.close());
        
        // Button container
        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Add all to root
        root.getChildren().addAll(headerBox, messageBox, buttonBox);
        
        // Create scene and show
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(scene);
        
        // Center dialog
        if (owner != null) {
            dialog.setX(owner.getX() + (owner.getWidth() - root.getPrefWidth()) / 2);
            dialog.setY(owner.getY() + (owner.getHeight() - 450) / 2);
        }
        
        dialog.showAndWait();
    }
    
    /**
     * Show employee assignment success dialog
     */
    public static void showEmployeeAssignmentSuccess(Stage owner, int orderId, String employeeName) {
        // Create custom dialog stage
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Assignment Successful");
        
        // Main container
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: " + DARK_BG + "; " +
                     "-fx-background-radius: 12; " +
                     "-fx-border-color: " + GOLD_ACCENT + "; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 20, 0, 0, 5);");
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setPrefWidth(500);
        
        // Success Icon and Header
        Label successIcon = new Label("✅");
        successIcon.setStyle("-fx-font-size: 48px;");
        successIcon.setAlignment(Pos.CENTER);
        
        Label headerLabel = new Label("Employee Assigned Successfully");
        headerLabel.setStyle("-fx-text-fill: " + GOLD_ACCENT + "; " +
                           "-fx-font-size: 22px; " +
                           "-fx-font-weight: bold;");
        headerLabel.setWrapText(true);
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setAlignment(Pos.CENTER);
        
        // Header container
        VBox headerBox = new VBox(10, successIcon, headerLabel);
        headerBox.setAlignment(Pos.CENTER);
        
        // Success message
        Label messageLabel = new Label(
            "Order #" + orderId + "\n\n" +
            "✓ Employee: " + employeeName + "\n\n" +
            "The employee has been successfully assigned to this order."
        );
        messageLabel.setStyle("-fx-text-fill: " + WHITE_TEXT + "; " +
                            "-fx-font-size: 14px; " +
                            "-fx-line-spacing: 3px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setAlignment(Pos.CENTER);
        
        // Message container with background
        VBox messageBox = new VBox(messageLabel);
        messageBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                          "-fx-background-radius: 8; " +
                          "-fx-border-color: rgba(214, 194, 138, 0.3); " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 8;");
        messageBox.setPadding(new Insets(20));
        
        // OK Button
        Button okButton = new Button("OK");
        okButton.setStyle(
            "-fx-background-color: " + GOLD_ACCENT + "; " +
            "-fx-text-fill: " + DARK_BG + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(203, 181, 122, 0.5), 8, 0, 0, 3);"
        );
        okButton.setPrefWidth(140);
        okButton.setOnMouseEntered(e -> okButton.setStyle(
            "-fx-background-color: #c0a770; " +
            "-fx-text-fill: " + DARK_BG + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(203, 181, 122, 0.7), 10, 0, 0, 4);"
        ));
        okButton.setOnMouseExited(e -> okButton.setStyle(
            "-fx-background-color: " + GOLD_ACCENT + "; " +
            "-fx-text-fill: " + DARK_BG + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(203, 181, 122, 0.5), 8, 0, 0, 3);"
        ));
        okButton.setOnAction(e -> dialog.close());
        
        // Button container
        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Add all to root
        root.getChildren().addAll(headerBox, messageBox, buttonBox);
        
        // Create scene and show
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(scene);
        
        // Center dialog
        if (owner != null) {
            dialog.setX(owner.getX() + (owner.getWidth() - root.getPrefWidth()) / 2);
            dialog.setY(owner.getY() + (owner.getHeight() - 400) / 2);
        }
        
        dialog.showAndWait();
    }
    
    /**
     * Show order status update success dialog
     */
    public static void showStatusUpdateSuccess(Stage owner, int orderId, String oldStatus, String newStatus) {
        // Create custom dialog stage
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Status Updated");
        
        // Main container
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: " + DARK_BG + "; " +
                     "-fx-background-radius: 12; " +
                     "-fx-border-color: " + GOLD_ACCENT + "; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 20, 0, 0, 5);");
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setPrefWidth(500);
        
        // Success Icon and Header
        Label successIcon = new Label("✅");
        successIcon.setStyle("-fx-font-size: 48px;");
        successIcon.setAlignment(Pos.CENTER);
        
        Label headerLabel = new Label("Order Status Updated");
        headerLabel.setStyle("-fx-text-fill: " + GOLD_ACCENT + "; " +
                           "-fx-font-size: 22px; " +
                           "-fx-font-weight: bold;");
        headerLabel.setWrapText(true);
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setAlignment(Pos.CENTER);
        
        // Header container
        VBox headerBox = new VBox(10, successIcon, headerLabel);
        headerBox.setAlignment(Pos.CENTER);
        
        // Success message
        Label messageLabel = new Label(
            "Order #" + orderId + "\n\n" +
            "Status changed from:\n" +
            oldStatus + " → " + newStatus + "\n\n" +
            "The order status has been successfully updated."
        );
        messageLabel.setStyle("-fx-text-fill: " + WHITE_TEXT + "; " +
                            "-fx-font-size: 14px; " +
                            "-fx-line-spacing: 3px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setAlignment(Pos.CENTER);
        
        // Message container with background
        VBox messageBox = new VBox(messageLabel);
        messageBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                          "-fx-background-radius: 8; " +
                          "-fx-border-color: rgba(214, 194, 138, 0.3); " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 8;");
        messageBox.setPadding(new Insets(20));
        
        // OK Button
        Button okButton = new Button("OK");
        okButton.setStyle(
            "-fx-background-color: " + GOLD_ACCENT + "; " +
            "-fx-text-fill: " + DARK_BG + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(203, 181, 122, 0.5), 8, 0, 0, 3);"
        );
        okButton.setPrefWidth(140);
        okButton.setOnMouseEntered(e -> okButton.setStyle(
            "-fx-background-color: #c0a770; " +
            "-fx-text-fill: " + DARK_BG + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(203, 181, 122, 0.7), 10, 0, 0, 4);"
        ));
        okButton.setOnMouseExited(e -> okButton.setStyle(
            "-fx-background-color: " + GOLD_ACCENT + "; " +
            "-fx-text-fill: " + DARK_BG + "; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 12 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(203, 181, 122, 0.5), 8, 0, 0, 3);"
        ));
        okButton.setOnAction(e -> dialog.close());
        
        // Button container
        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Add all to root
        root.getChildren().addAll(headerBox, messageBox, buttonBox);
        
        // Create scene and show
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(scene);
        
        // Center dialog
        if (owner != null) {
            dialog.setX(owner.getX() + (owner.getWidth() - root.getPrefWidth()) / 2);
            dialog.setY(owner.getY() + (owner.getHeight() - 400) / 2);
        }
        
        dialog.showAndWait();
    }
}

package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.CustomOrder;
import com.DB.databaseproject.service.CustomOrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminCustomOrdersController {
    
    @FXML
    private TableView<CustomOrder> customOrdersTable;
    
    @FXML
    private TableColumn<CustomOrder, Integer> idColumn;
    
    @FXML
    private TableColumn<CustomOrder, String> customerNameColumn;
    
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
    
    @FXML
    private TableColumn<CustomOrder, Void> actionsColumn;
    
    private final CustomOrderService customOrderService = CustomOrderService.getInstance();
    private ObservableList<CustomOrder> customOrdersList;
    
    @FXML
    public void initialize() {
        customOrdersList = FXCollections.observableArrayList();
        
        setupTableColumns();
        setupActionsColumn();
        loadCustomOrders();
        
        customOrdersTable.setItems(customOrdersList);
        customOrdersTable.setPlaceholder(new Label("No custom orders"));
        customOrdersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customOrderId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
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
    
    private void setupActionsColumn() {
        actionsColumn.setPrefWidth(240);
        actionsColumn.setMinWidth(240);
        
        actionsColumn.setCellFactory(column -> new TableCell<CustomOrder, Void>() {
            private final Button approveButton = new Button("Approve & Create Order");
            private final Button rejectButton = new Button("Reject");
            
            {
                approveButton.getStyleClass().add("btn-approve");
                approveButton.setStyle(
                    "-fx-background-color: #C9B06B; " +
                    "-fx-text-fill: #151515; " +
                    "-fx-font-size: 11px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 6px 10px; " +
                    "-fx-background-radius: 4px; " +
                    "-fx-cursor: hand;"
                );
                approveButton.setOnMouseEntered(e -> approveButton.setStyle(
                    "-fx-background-color: #D4C07A; " +
                    "-fx-text-fill: #151515; " +
                    "-fx-font-size: 11px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 6px 10px; " +
                    "-fx-background-radius: 4px; " +
                    "-fx-cursor: hand;"
                ));
                approveButton.setOnMouseExited(e -> approveButton.setStyle(
                    "-fx-background-color: #C9B06B; " +
                    "-fx-text-fill: #151515; " +
                    "-fx-font-size: 11px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 6px 10px; " +
                    "-fx-background-radius: 4px; " +
                    "-fx-cursor: hand;"
                ));
                approveButton.setOnAction(event -> {
                    CustomOrder customOrder = getTableView().getItems().get(getIndex());
                    handleApprove(customOrder);
                });
                
                rejectButton.getStyleClass().add("btn-reject");
                rejectButton.setStyle(
                    "-fx-background-color: #D9534F; " +
                    "-fx-text-fill: #FFFFFF; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 6px 12px; " +
                    "-fx-background-radius: 4px; " +
                    "-fx-cursor: hand;"
                );
                rejectButton.setOnAction(event -> {
                    CustomOrder customOrder = getTableView().getItems().get(getIndex());
                    handleReject(customOrder);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CustomOrder customOrder = getTableView().getItems().get(getIndex());
                    if (customOrder != null && "Pending".equalsIgnoreCase(customOrder.getStatus())) {
                        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(8);
                        buttons.setAlignment(Pos.CENTER);
                        buttons.getChildren().addAll(approveButton, rejectButton);
                        setGraphic(buttons);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }
    
    private void handleApprove(CustomOrder customOrder) {
        boolean confirmed = showConfirmDialog(
            "Approve & Create Order",
            "This will:\n" +
            "• Create a new Order in the system\n" +
            "• Mark this custom request as 'Converted'\n" +
            "• Order will appear in Orders page\n\n" +
            "Customer: " + customOrder.getCustomerName() + "\n" +
            "Stone Type: " + customOrder.getStoneType() + "\n" +
            "Size: " + customOrder.getSize() + "\n" +
            "Quantity: " + customOrder.getQuantity() + "\n\n" +
            "Continue?"
        );
        
        if (confirmed) {
            try {
                boolean success = customOrderService.approveAndConvertCustomOrder(customOrder);
                if (success) {
                    showSuccess("✅ Custom order approved and converted to Order successfully!\n\nThe order now appears in the Orders page.");
                    loadCustomOrders();
                } else {
                    showError("Failed to approve and convert custom order");
                }
            } catch (Exception e) {
                showError("Error converting custom order:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void handleReject(CustomOrder customOrder) {
        boolean confirmed = showConfirmDialog(
            "Reject Custom Order",
            "Are you sure you want to reject this custom order request?\n\n" +
            "Customer: " + customOrder.getCustomerName() + "\n" +
            "Stone Type: " + customOrder.getStoneType()
        );
        
        if (confirmed) {
            boolean success = customOrderService.rejectCustomOrder(customOrder.getCustomOrderId());
            if (success) {
                showSuccess("Custom order rejected");
                loadCustomOrders();
            } else {
                showError("Failed to reject custom order");
            }
        }
    }
    
    private void loadCustomOrders() {
        customOrdersList.clear();
        customOrdersList.addAll(customOrderService.getAllCustomOrders());
    }
    
    private boolean showConfirmDialog(String title, String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initOwner(customOrdersTable.getScene().getWindow());
        dialogStage.setTitle(title);
        dialogStage.setResizable(false);

        VBox content = new VBox(20);
        content.setPadding(new javafx.geometry.Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #2A2A2A;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(350);

        final boolean[] confirmed = {false};

        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle(
            "-fx-background-color: #C2B280; " +
            "-fx-text-fill: #1F1F1F; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 25px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        confirmButton.setOnAction(e -> {
            confirmed[0] = true;
            dialogStage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #C2B280; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 25px; " +
            "-fx-border-color: #C2B280; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        cancelButton.setOnAction(e -> dialogStage.close());

        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        content.getChildren().addAll(messageLabel, buttonBox);

        javafx.scene.Scene scene = new javafx.scene.Scene(content, 450, 200);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();

        return confirmed[0];
    }
    
    private void showSuccess(String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initOwner(customOrdersTable.getScene().getWindow());
        dialogStage.setTitle("Success");
        dialogStage.setResizable(false);

        VBox content = new VBox(20);
        content.setPadding(new javafx.geometry.Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #2A2A2A;");

        // Success icon/title
        Label titleLabel = new Label("✅ Success");
        titleLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #C9B06B;"
        );

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setAlignment(Pos.CENTER);

        Button okButton = new Button("OK");
        okButton.setStyle(
            "-fx-background-color: #C9B06B; " +
            "-fx-text-fill: #1F1F1F; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 30px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        okButton.setOnAction(e -> dialogStage.close());

        content.getChildren().addAll(titleLabel, messageLabel, okButton);

        javafx.scene.Scene scene = new javafx.scene.Scene(content, 500, 250);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
    
    private void showError(String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initOwner(customOrdersTable.getScene().getWindow());
        dialogStage.setTitle("Error");
        dialogStage.setResizable(false);

        VBox content = new VBox(20);
        content.setPadding(new javafx.geometry.Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #2A2A2A;");

        // Error icon/title
        Label titleLabel = new Label("❌ Error");
        titleLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #D9534F;"
        );

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setAlignment(Pos.CENTER);

        Button okButton = new Button("OK");
        okButton.setStyle(
            "-fx-background-color: #D9534F; " +
            "-fx-text-fill: #FFFFFF; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 30px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        okButton.setOnAction(e -> dialogStage.close());

        content.getChildren().addAll(titleLabel, messageLabel, okButton);

        javafx.scene.Scene scene = new javafx.scene.Scene(content, 500, 250);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
}

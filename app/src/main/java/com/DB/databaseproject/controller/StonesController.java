package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Stone;
import com.DB.databaseproject.service.StoneService;
import com.DB.databaseproject.util.CustomDialogs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for Stones Catalog Management View
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class StonesController {
    
    private final StoneService stoneService = StoneService.getInstance();

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Stone> stonesTable;

    @FXML
    private TableColumn<Stone, Integer> idColumn;

    @FXML
    private TableColumn<Stone, String> nameColumn;

    @FXML
    private TableColumn<Stone, String> typeColumn;

    @FXML
    private TableColumn<Stone, String> sizeColumn;

    @FXML
    private TableColumn<Stone, Double> priceColumn;

    @FXML
    private TableColumn<Stone, Integer> quantityColumn;

    @FXML
    private TableColumn<Stone, Image> imageColumn;

    @FXML
    private TableColumn<Stone, Void> actionsColumn;

    private ObservableList<Stone> stonesList;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Stones Catalog Management View initialized");

        // Initialize the stones list
        stonesList = FXCollections.observableArrayList();

        // Configure table columns
        setupTableColumns();

        // Load sample data
        loadSampleData();

        // Set the data to the table ONCE - we never change this reference
        stonesTable.setItems(stonesList);

        // Placeholder message when table is empty
        stonesTable.setPlaceholder(new Label("No stones found"));

        // Make table responsive - columns auto-resize to fill available width
        stonesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        stonesTable.setPrefWidth(Double.MAX_VALUE);
        
        // Setup search field listener for real-time filtering
        setupSearchListener();
    }
    
    /**
     * Setup search field listener for real-time filtering
     * This ensures Edit/Delete buttons remain visible during search
     */
    private void setupSearchListener() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                // Automatically trigger search when text changes
                onSearchStone();
            });
        }
    }

    /**
     * Setup table columns with cell value factories
     * FIXED: Added proper column widths and padding to prevent truncation
     */
    private void setupTableColumns() {
        // Stone ID Column - FIXED: Set widths and padding
        idColumn.setCellValueFactory(new PropertyValueFactory<>("stoneId"));
        idColumn.setMinWidth(100);
        idColumn.setPrefWidth(110);
        idColumn.setStyle("-fx-alignment: CENTER; -fx-padding: 8px;");

        // Name Column - FIXED: Set widths and padding
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setMinWidth(150);
        nameColumn.setPrefWidth(180);
        nameColumn.setStyle("-fx-padding: 8px;");

        // Type Column - FIXED: Set widths and padding
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setMinWidth(120);
        typeColumn.setPrefWidth(140);
        typeColumn.setStyle("-fx-padding: 8px;");

        // Size Column - FIXED: Set widths and padding
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeColumn.setMinWidth(110);
        sizeColumn.setPrefWidth(120);
        sizeColumn.setStyle("-fx-padding: 8px;");

        // Price Per Unit Column - FIXED: Set widths and padding
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        priceColumn.setMinWidth(150);
        priceColumn.setPrefWidth(170);
        priceColumn.setStyle("-fx-padding: 8px;");
        priceColumn.setCellFactory(column -> new TableCell<Stone, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });

        // Quantity in Stock Column - FIXED: Set widths and padding
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));
        quantityColumn.setMinWidth(180);
        quantityColumn.setPrefWidth(200);
        quantityColumn.setStyle("-fx-padding: 8px;");

        // Image Column (ImageView) - FIXED: Set widths
        imageColumn.setMinWidth(100);
        imageColumn.setPrefWidth(110);
        setupImageColumn();

        // Actions Column (Edit and Delete buttons) - FIXED: Set widths
        actionsColumn.setMinWidth(200);
        actionsColumn.setPrefWidth(220);
        setupActionsColumn();
    }

    /**
     * Setup the Image column with ImageView and click-to-preview functionality
     * Displays small thumbnail (50x50) in table
     * Click on image opens large preview popup with dark theme styling
     * Hand cursor indicates the image is clickable
     */
    private void setupImageColumn() {
        System.out.println("📸 Setting up image column with click handlers...");
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        imageColumn.setCellFactory(column -> new TableCell<Stone, Image>() {
            private final ImageView imageView = new ImageView();
            private final HBox container = new HBox(imageView);

            {
                // Configure container
                container.setAlignment(Pos.CENTER);
                container.setStyle("-fx-padding: 5;");
                
                // Set small thumbnail size (50x50) for table display
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setPickOnBounds(true); // Ensure entire bounds are clickable
                imageView.getStyleClass().add("stone-image-preview");
                
                // Add mouse pressed handler (more reliable than clicked for TableCell)
                imageView.setOnMousePressed(event -> {
                    System.out.println("🖱️ Image mouse pressed in table!");
                    Image image = imageView.getImage();
                    if (image != null) {
                        System.out.println("✅ Image found, opening preview...");
                        showLargeImage(image);
                        event.consume(); // Prevent event from propagating to table
                    } else {
                        System.out.println("⚠️ Image is null, cannot show preview");
                    }
                });
                
                // Also add click handler as backup
                imageView.setOnMouseClicked(event -> {
                    System.out.println("🖱️ Image clicked in table!");
                    event.consume();
                });
                
                // Add hover effects
                imageView.setOnMouseEntered(event -> {
                    imageView.setOpacity(0.8);
                    System.out.println("🖱️ Mouse entered image");
                });
                
                imageView.setOnMouseExited(event -> {
                    imageView.setOpacity(1.0);
                    System.out.println("🖱️ Mouse exited image");
                });
                
                // Set cursor to hand to indicate clickability
                imageView.setStyle("-fx-cursor: hand;");
                
                // Make this cell non-selectable to prevent interference
                this.setMouseTransparent(false);
            }

            @Override
            protected void updateItem(Image image, boolean empty) {
                super.updateItem(image, empty);
                if (empty || image == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    imageView.setImage(image);
                    setGraphic(container);
                    setText(null);
                }
            }
        });
    }

    /**
     * Setup the Actions column with Edit and Delete buttons
     */
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<Stone, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox actionButtons = new HBox(8, editButton, deleteButton);

            {
                // Style the buttons using CSS classes (matches EmployeesController)
                editButton.getStyleClass().add("btn-edit");
                deleteButton.getStyleClass().add("btn-delete");
                
                // Set minimum widths to prevent text truncation
                editButton.setMinWidth(70);
                deleteButton.setMinWidth(70);

                // Center the buttons
                actionButtons.setAlignment(Pos.CENTER);

                // Set button actions
                editButton.setOnAction(event -> {
                    Stone stone = getTableView().getItems().get(getIndex());
                    onEditStone(stone);
                });

                deleteButton.setOnAction(event -> {
                    Stone stone = getTableView().getItems().get(getIndex());
                    onDeleteStone(stone);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });
    }

    /**
     * Load sample stone data (temporary for UI preview)
     * Later this will be replaced with PostgreSQL data
     */
    private void loadSampleData() {
        // Load stones from database
        stonesList.clear();
        stonesList.addAll(stoneService.getAllStones());
        
        System.out.println("Stones loaded from database: " + stonesList.size());
    }

    /**
     * Handle Add Stone button click
     */
    @FXML
    private void onAddStone() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         OPENING STONE FORM DIALOG - ADD MODE                ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        openStoneForm(null);
    }

    /**
     * Handle Edit Stone button click
     * @param stone The stone to edit
     */
    private void onEditStone(Stone stone) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         OPENING STONE FORM DIALOG - EDIT MODE               ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Editing Stone: " + stone.getName());
        System.out.println("Stone ID: " + stone.getStoneId());
        
        openStoneForm(stone);
    }

    /**
     * Handle Delete Stone button click
     * Deletes stone record and updates any orders using it
     * @param stone The stone to delete
     */
    private void onDeleteStone(Stone stone) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         DELETE STONE - CONTROLLER LAYER                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("🗑️  Delete Stone Request: " + stone.getName());
        System.out.println("📝 Stone ID: " + stone.getStoneId());
        
        // Check how many orders use this stone
        int orderCount = stoneService.getOrderCountForStone(stone.getStoneId());
        System.out.println("📊 Orders using this stone: " + orderCount);
        
        // Show custom themed confirmation dialog
        Stage ownerStage = (Stage) stonesTable.getScene().getWindow();
        boolean confirmed = CustomDialogs.showStoneDeleteConfirmation(
            ownerStage, 
            stone.getName(), 
            stone.getStoneId(),
            orderCount
        );
        
        if (confirmed) {
            System.out.println("✅ User confirmed deletion");
            System.out.println("🔄 Calling StoneService.deleteStone()...\n");
            
            try {
                // Call service to delete stone (handles orders automatically)
                boolean success = stoneService.deleteStone(stone.getStoneId());
                
                if (success) {
                    // Success - Remove from table
                    stonesList.remove(stone);
                    
                    System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                    System.out.println("║         ✅ DELETION SUCCESSFUL - UI UPDATED                 ║");
                    System.out.println("╚════════════════════════════════════════════════════════════╝");
                    System.out.println("✅ Stone removed from table view");
                    System.out.println("✅ " + stone.getName() + " has been deleted");
                    
                    // Show custom themed success dialog
                    CustomDialogs.showStoneDeleteSuccess(ownerStage, stone.getName(), stone.getStoneId(), orderCount);
                    
                    // Refresh the table to ensure consistency
                    refreshStoneTable();
                    
                } else {
                    // Failure
                    System.err.println("\n╔════════════════════════════════════════════════════════════╗");
                    System.err.println("║         ❌ DELETION FAILED                                  ║");
                    System.err.println("╚════════════════════════════════════════════════════════════╝");
                    System.err.println("❌ StoneService.deleteStone() returned false");
                    System.err.println("   Stone: " + stone.getName());
                    System.err.println("   Stone ID: " + stone.getStoneId());
                    
                    // Show error alert
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.setHeaderText("❌ Failed to Delete Stone");
                    errorAlert.setContentText(
                        "Could not delete stone: " + stone.getName() + "\n\n" +
                        "Possible reasons:\n" +
                        "• Stone ID not found in database\n" +
                        "• Database connection issue\n" +
                        "• Foreign key constraints\n\n" +
                        "Please check the console for detailed error messages."
                    );
                    errorAlert.showAndWait();
                }
                
            } catch (Exception e) {
                // Exception occurred
                System.err.println("\n╔════════════════════════════════════════════════════════════╗");
                System.err.println("║         ❌ EXCEPTION DURING DELETION                        ║");
                System.err.println("╚════════════════════════════════════════════════════════════╝");
                System.err.println("❌ Exception: " + e.getClass().getSimpleName());
                System.err.println("❌ Message: " + e.getMessage());
                e.printStackTrace();
                
                // Show exception alert
                Alert exceptionAlert = new Alert(Alert.AlertType.ERROR);
                exceptionAlert.setTitle("Deletion Error");
                exceptionAlert.setHeaderText("❌ Unexpected Error During Deletion");
                exceptionAlert.setContentText(
                    "An unexpected error occurred while deleting the stone.\n\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "Please contact your system administrator.\n" +
                    "Check the console for full stack trace."
                );
                exceptionAlert.showAndWait();
            }
        } else {
            System.out.println("❌ User cancelled deletion");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        }
    }
    
    /**
     * Refresh the stone table by reloading data from database
     */
    private void refreshStoneTable() {
        System.out.println("\n🔄 Refreshing stone table from database...");
        stonesList.clear();
        stonesList.addAll(stoneService.getAllStones());
        System.out.println("✅ Table refreshed: " + stonesList.size() + " stones loaded\n");
        
        // Re-apply search filter if search field has text
        if (searchField != null && !searchField.getText().trim().isEmpty()) {
            onSearchStone();
        }
    }
    
    /**
     * Open stone form dialog for adding or editing
     * @param stone Stone to edit (null for add mode)
     */
    private void openStoneForm(Stone stone) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║         OPENING STONE FORM DIALOG                           ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println("Mode: " + (stone == null ? "ADD" : "EDIT"));
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/stone_form.fxml")
            );
            Parent root = loader.load();
            
            // Get controller
            StoneFormController formController = loader.getController();
            
            // Set callback to refresh table when saved
            formController.setOnSaveCallback(success -> {
                if (success) {
                    System.out.println("✅ Stone saved - refreshing table...");
                    refreshStoneTable();
                }
            });
            
            // Configure for ADD or EDIT mode
            if (stone == null) {
                // ADD mode
                formController.setAddMode();
            } else {
                // EDIT mode
                formController.setEditMode(stone);
            }
            
            // Create and show dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle(stone == null ? "Add New Stone" : "Edit Stone");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stonesTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root, 600, 650));
            dialogStage.setResizable(false);
            
            System.out.println("✅ Stone form dialog opened");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("❌ Failed to load stone form FXML");
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open stone form");
            alert.setContentText("Could not load the stone form.\n\nError: " + e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error opening form");
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unexpected Error");
            alert.setContentText("An error occurred:\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Handle Search Stone button click
     * FIXED: Only filters the data, does NOT change table columns or cellFactory
     */
    @FXML
    private void onSearchStone() {
        String searchText = searchField.getText().trim();
        System.out.println("Search Stone: " + searchText);
        
        if (searchText.isEmpty()) {
            // Clear search - reload all stones from database and show them
            System.out.println("🔄 Search cleared - reloading all stones from database...");
            stonesList.clear();
            stonesList.addAll(stoneService.getAllStones());
            System.out.println("✅ Showing all stones: " + stonesList.size());
        } else {
            // Filter stones from the current list
            ObservableList<Stone> allStones = FXCollections.observableArrayList(stoneService.getAllStones());
            stonesList.clear();
            
            for (Stone stone : allStones) {
                if (stone.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    stone.getType().toLowerCase().contains(searchText.toLowerCase()) ||
                    stone.getSize().toLowerCase().contains(searchText.toLowerCase()) ||
                    String.valueOf(stone.getStoneId()).contains(searchText)) {
                    stonesList.add(stone);
                }
            }
            
            System.out.println("✅ Found " + stonesList.size() + " stones matching: " + searchText);
        }
        
        // Force table refresh to ensure buttons appear
        stonesTable.refresh();
    }

    /**
     * Get the stones list
     * @return Observable list of stones
     */
    public ObservableList<Stone> getStonesList() {
        return stonesList;
    }
    
    /**
     * Open image preview popup with full-size image
     * Shows the stone image in a large modal dialog with dark theme styling
     * @param image Image to display (if null, does nothing)
     */
    private void openImagePreview(Image image) {
        // Null check - do nothing if image is null
        if (image == null) {
            System.out.println("⚠️ Image is null - skipping preview");
            return;
        }
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         OPENING IMAGE PREVIEW                               ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Image Size: " + image.getWidth() + " x " + image.getHeight());
        
        try {
            // Create preview stage
            Stage previewStage = new Stage();
            previewStage.initModality(Modality.WINDOW_MODAL);
            previewStage.initOwner(stonesTable.getScene().getWindow());
            previewStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            previewStage.setTitle("Stone Image Preview");
            
            // Create ImageView for full-size image
            ImageView fullImageView = new ImageView(image);
            fullImageView.setPreserveRatio(true);
            fullImageView.setSmooth(true);
            
            // Set image size - fit to 600px width while maintaining aspect ratio
            double fitWidth = 600;
            fullImageView.setFitWidth(fitWidth);
            
            // Calculate actual display dimensions
            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();
            double aspectRatio = imageHeight / imageWidth;
            double displayHeight = fitWidth * aspectRatio;
            
            System.out.println("Display Size: " + fitWidth + " x " + displayHeight);
            
            // Create container with dark theme matching app style
            javafx.scene.layout.VBox container = new javafx.scene.layout.VBox(15);
            container.setAlignment(javafx.geometry.Pos.CENTER);
            container.setStyle(
                "-fx-background-color: #2b2b2b; " +
                "-fx-padding: 30; " +
                "-fx-border-color: #C2B280; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 20, 0, 0, 5);"
            );
            
            // Add title with gold styling
            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("🖼️ Stone Image Preview");
            titleLabel.setStyle(
                "-fx-text-fill: #d4c59e; " +
                "-fx-font-size: 20px; " +
                "-fx-font-weight: bold;"
            );
            
            // Wrap image in a styled card
            javafx.scene.layout.StackPane imageCard = new javafx.scene.layout.StackPane(fullImageView);
            imageCard.setStyle(
                "-fx-background-color: #1f1f1f; " +
                "-fx-padding: 10; " +
                "-fx-border-color: #555555; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;"
            );
            
            // Add close button with gold styling
            javafx.scene.control.Button closeButton = new javafx.scene.control.Button("✖ Close");
            closeButton.setStyle(
                "-fx-background-color: #D4BF83; " +
                "-fx-text-fill: #1F1F1F; " +
                "-fx-font-size: 15px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 40; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(212, 191, 131, 0.4), 8, 0, 0, 2);"
            );
            closeButton.setOnAction(e -> previewStage.close());
            
            // Add hover effect to close button
            closeButton.setOnMouseEntered(e -> {
                closeButton.setStyle(
                    "-fx-background-color: #E6D4A5; " +
                    "-fx-text-fill: #1F1F1F; " +
                    "-fx-font-size: 15px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 12 40; " +
                    "-fx-background-radius: 8; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(212, 191, 131, 0.6), 12, 0, 0, 3);"
                );
                closeButton.setScaleX(1.05);
                closeButton.setScaleY(1.05);
            });
            closeButton.setOnMouseExited(e -> {
                closeButton.setStyle(
                    "-fx-background-color: #D4BF83; " +
                    "-fx-text-fill: #1F1F1F; " +
                    "-fx-font-size: 15px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 12 40; " +
                    "-fx-background-radius: 8; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(212, 191, 131, 0.4), 8, 0, 0, 2);"
                );
                closeButton.setScaleX(1.0);
                closeButton.setScaleY(1.0);
            });
            
            // Add hint label
            javafx.scene.control.Label hintLabel = new javafx.scene.control.Label("Press ESC to close");
            hintLabel.setStyle(
                "-fx-text-fill: #888888; " +
                "-fx-font-size: 12px; " +
                "-fx-font-style: italic;"
            );
            
            // Add components to container
            container.getChildren().addAll(titleLabel, imageCard, closeButton, hintLabel);
            
            // Create scene
            javafx.scene.Scene scene = new javafx.scene.Scene(container);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            
            // Add ESC key handler to close dialog
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                    System.out.println("⌨️ ESC key pressed - closing preview");
                    previewStage.close();
                }
            });
            
            previewStage.setScene(scene);
            
            // Center on parent window
            Stage owner = (Stage) stonesTable.getScene().getWindow();
            if (owner != null) {
                // Calculate center position after stage is shown
                previewStage.setOnShown(e -> {
                    double centerX = owner.getX() + (owner.getWidth() - previewStage.getWidth()) / 2;
                    double centerY = owner.getY() + (owner.getHeight() - previewStage.getHeight()) / 2;
                    previewStage.setX(centerX);
                    previewStage.setY(centerY);
                });
            }
            
            System.out.println("✅ Image preview window opened");
            System.out.println("💡 Press ESC or click Close button to close");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            
            previewStage.showAndWait();
            
        } catch (Exception e) {
            System.err.println("❌ Error opening image preview: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Show large image preview in a styled popup dialog
     * Features: Close button, click-outside-to-close, ESC key support, dark theme styling
     * @param img Image to display (if null, does nothing)
     */
    private void showLargeImage(Image img) {
        // Null safety check
        if (img == null) {
            System.out.println("⚠️ Image is null - skipping preview");
            return;
        }
        
        try {
            System.out.println("🖼️ Opening large image preview...");
            
            // Create modal popup window with transparent background
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(stonesTable.getScene().getWindow());
            popup.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            popup.setTitle("Stone Image Preview");
        
            // Create large ImageView (preserve aspect ratio, max 650px width)
            ImageView largeView = new ImageView(img);
            largeView.setPreserveRatio(true);
            largeView.setFitWidth(650);
            largeView.setSmooth(true);
            largeView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 15, 0, 0, 5);");
        
        // Create Close button with theme styling
        Button closeButton = new Button("✕ Close");
        closeButton.setStyle(
            "-fx-background-color: #C2B280; " +
            "-fx-text-fill: #1F1F1F; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 25; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        
        // Close button hover effect
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(
            "-fx-background-color: #D4C59E; " +
            "-fx-text-fill: #1F1F1F; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 25; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        ));
        
        closeButton.setOnMouseExited(e -> closeButton.setStyle(
            "-fx-background-color: #C2B280; " +
            "-fx-text-fill: #1F1F1F; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 25; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        ));
        
        closeButton.setOnAction(e -> popup.close());
        
        // Create content container with dark theme styling and rounded corners
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(20);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(largeView, closeButton);
        content.setStyle(
            "-fx-background-color: #2B2B2B; " +
            "-fx-padding: 30; " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: rgba(194, 178, 128, 0.3); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 25, 0, 0, 8);"
        );
        
        // Create outer container for click-outside-to-close functionality
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(content);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        root.setAlignment(Pos.CENTER);
        
        // Click outside the content area to close
        root.setOnMouseClicked(e -> {
            if (e.getTarget() == root) {
                popup.close();
            }
        });
        
        // Create scene with transparent background
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        popup.setScene(scene);
        
        // Close on ESC key
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                popup.close();
            }
        });
        
            popup.show();
            System.out.println("✅ Large image preview opened");
            
        } catch (Exception e) {
            System.err.println("❌ Error showing large image preview: " + e.getMessage());
            e.printStackTrace();
            
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open image preview");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}

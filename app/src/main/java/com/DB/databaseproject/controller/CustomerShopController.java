package com.DB.databaseproject.controller;

import com.DB.databaseproject.model.Stone;
import com.DB.databaseproject.service.StoneService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for Customer Shop View (Stones Grid)
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class CustomerShopController {
    
    private final StoneService stoneService = StoneService.getInstance();

    @FXML
    private TextField searchField;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FlowPane stonesFlowPane;

    private ObservableList<Stone> stonesList;
    private ObservableList<Stone> filteredList;

    /**
     * Initialize method - called automatically after FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("Customer Shop View initialized");

        // Initialize stones list
        stonesList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();

        // Load sample stones
        loadSampleStones();

        // Display stones in grid
        displayStones(stonesList);
        
        // Setup search field listener for real-time filtering
        setupSearchListener();
    }
    
    /**
     * Setup search field listener for real-time filtering
     * This ensures Buy Now buttons remain visible during search
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
     * Load sample stones for preview
     * TWO sample stones as requested
     */
    private void loadSampleStones() {
        // Load stones from database (only stones in stock)
        stonesList.clear();
        stonesList.addAll(stoneService.getStonesInStock());
        
        System.out.println("Loaded " + stonesList.size() + " stones from database");
    }
    
    /**
     * Refresh stones list and re-apply search filter if active
     */
    private void refreshStones() {
        System.out.println("\n🔄 Refreshing stones from database...");
        loadSampleStones();
        
        // Re-apply search filter if search field has text
        if (searchField != null && !searchField.getText().trim().isEmpty()) {
            onSearchStone();
        } else {
            displayStones(stonesList);
        }
        
        System.out.println("✅ Stones refreshed: " + stonesList.size() + " stones loaded\n");
    }

    /**
     * Display stones in grid
     */
    private void displayStones(ObservableList<Stone> stones) {
        stonesFlowPane.getChildren().clear();

        for (Stone stone : stones) {
            VBox stoneCard = createStoneCard(stone);
            stonesFlowPane.getChildren().add(stoneCard);
        }
    }

    /**
     * Create a stone card with image, name, type, size, price, and Buy Now button
     */
    private VBox createStoneCard(Stone stone) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("stone-card");
        card.setPrefWidth(220);
        card.setPadding(new Insets(15));

        // Stone Image (150-200px as requested)
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.getStyleClass().add("stone-card-image");
        
        // Use the already-loaded image from Stone object
        Image stoneImage = stone.getImage();
        if (stoneImage != null) {
            imageView.setImage(stoneImage);
            System.out.println("✅ Image loaded for stone: " + stone.getName());
        } else {
            System.err.println("⚠️ No image found for stone: " + stone.getName() + " (path: " + stone.getImagePath() + ")");
            // Set a placeholder or leave empty
        }

        // Stone Name
        Label nameLabel = new Label(stone.getName());
        nameLabel.getStyleClass().add("stone-card-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(180);

        // Stone Type
        Label typeLabel = new Label("Type: " + stone.getType());
        typeLabel.getStyleClass().add("stone-card-type");

        // Stone Size
        Label sizeLabel = new Label("Size: " + stone.getSize());
        sizeLabel.getStyleClass().add("stone-card-size");

        // Price
        Label priceLabel = new Label(String.format("$%.2f / unit", stone.getPricePerUnit()));
        priceLabel.getStyleClass().add("stone-card-price");

        // Buy Now Button
        // REQUIREMENT: If stock = 0, disable the Buy button for that stone
        Button buyButton = new Button("Buy Now");
        buyButton.getStyleClass().add("btn-buy-now");
        buyButton.setMaxWidth(Double.MAX_VALUE);
        buyButton.setOnAction(e -> handleBuyNow(stone));
        
        // Disable button if out of stock
        if (stone.getQuantityInStock() <= 0) {
            buyButton.setDisable(true);
            buyButton.setText("Out of Stock");
            buyButton.setStyle("-fx-opacity: 0.5; -fx-cursor: default;");
        }

        card.getChildren().addAll(imageView, nameLabel, typeLabel, sizeLabel, priceLabel, buyButton);
        
        return card;
    }

    /**
     * Handle search
     * FIXED: Reloads from database and filters properly to ensure Buy Now buttons remain visible
     */
    @FXML
    private void onSearchStone() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            // Clear search - reload all stones from database
            System.out.println("🔄 Search cleared - reloading all stones from database...");
            stonesList.clear();
            stonesList.addAll(stoneService.getStonesInStock());
            displayStones(stonesList);
            System.out.println("✅ Showing all stones: " + stonesList.size());
        } else {
            // Filter stones from database
            ObservableList<Stone> allStones = FXCollections.observableArrayList(stoneService.getStonesInStock());
            filteredList.clear();

            for (Stone stone : allStones) {
                boolean matchesSearch = stone.getName().toLowerCase().contains(searchText) ||
                                      stone.getType().toLowerCase().contains(searchText) ||
                                      stone.getSize().toLowerCase().contains(searchText);

                if (matchesSearch) {
                    filteredList.add(stone);
                }
            }

            displayStones(filteredList);
            System.out.println("✅ Search results: " + filteredList.size() + " stones matching: " + searchText);
        }
    }

    /**
     * Handle Buy Now button click
     */
    private void handleBuyNow(Stone stone) {
        System.out.println("Buy Now clicked for: " + stone.getName());
        showOrderPopup(stone);
    }

    /**
     * Show order popup dialog using FXML
     * REQUIREMENT: After successful purchase, refresh Available Stones page so stock is updated
     */
    private void showOrderPopup(Stone stone) {
        try {
            // Load the FXML popup
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/customer_order_popup.fxml")
            );
            Parent root = loader.load();

            // Get the controller and set stone data
            CustomerOrderPopupController popupController = loader.getController();
            popupController.setStone(stone);

            // Create and configure the stage
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Place Order - " + stone.getName());
            dialog.setResizable(false);

            // Create scene with CSS
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/css/dashboard.css").toExternalForm()
            );

            dialog.setScene(scene);
            dialog.showAndWait();

            // REQUIREMENT: Check if order was confirmed, then refresh stones
            if (popupController.isOrderConfirmed()) {
                System.out.println("✅ Order confirmed - refreshing stones list");
                
                // REQUIREMENT: Refresh Available Stones page so stock is updated
                refreshStones();
                
                System.out.println("✅ Stones list refreshed with updated stock levels");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading order popup: " + e.getMessage());
        }
    }
    
    /**
     * Handle Request Custom Stone button click
     * Opens dialog for customer to request a stone not in catalog
     */
    @FXML
    private void onRequestCustomStone() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/request_custom_stone.fxml")
            );
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(searchField.getScene().getWindow());
            dialogStage.setTitle("Request Custom Stone");
            dialogStage.setResizable(false);
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error opening custom stone request dialog: " + e.getMessage());
        }
    }
}
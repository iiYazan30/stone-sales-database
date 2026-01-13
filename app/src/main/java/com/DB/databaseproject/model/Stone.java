package com.DB.databaseproject.model;

import javafx.beans.property.*;
import javafx.scene.image.Image;

/**
 * Stone Model Class
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class Stone {
    private final IntegerProperty stoneId;
    private final StringProperty name;
    private final StringProperty type;
    private final StringProperty size;
    private final DoubleProperty pricePerUnit;
    private final IntegerProperty quantityInStock;
    private final StringProperty imagePath;
    private final ObjectProperty<Image> image;

    /**
     * Constructor with all fields
     */
    public Stone(int stoneId, String name, String type, String size,
                double pricePerUnit, int quantityInStock, String imagePath) {
        this.stoneId = new SimpleIntegerProperty(stoneId);
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.size = new SimpleStringProperty(size);
        this.pricePerUnit = new SimpleDoubleProperty(pricePerUnit);
        this.quantityInStock = new SimpleIntegerProperty(quantityInStock);
        this.imagePath = new SimpleStringProperty(imagePath);
        this.image = new SimpleObjectProperty<>();
        
        // Load image if path is provided
        if (imagePath != null && !imagePath.isEmpty()) {
            loadImage(imagePath);
        }
    }

    /**
     * Default constructor
     */
    public Stone() {
        this(0, "", "", "", 0.0, 0, "");
    }

    /**
     * Load image from path with multiple strategies
     */
    private void loadImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            System.err.println("⚠️ Image path is null or empty");
            return;
        }
        
        try {
            // Strategy 1: Try to load from resources (for paths like /images/stone.jpg)
            var imageStream = getClass().getResourceAsStream(path);
            if (imageStream != null) {
                this.image.set(new Image(imageStream));
                System.out.println("✅ Image loaded from resources: " + path);
                return;
            }
            
            // Strategy 2: Try with leading slash if not present
            if (!path.startsWith("/")) {
                imageStream = getClass().getResourceAsStream("/" + path);
                if (imageStream != null) {
                    this.image.set(new Image(imageStream));
                    System.out.println("✅ Image loaded from resources (with /): " + path);
                    return;
                }
            }
            
            // Strategy 3: Try to load as file path (for absolute paths)
            try {
                this.image.set(new Image("file:" + path));
                System.out.println("✅ Image loaded from file: " + path);
                return;
            } catch (Exception e) {
                // File path didn't work, continue to next strategy
            }
            
            // Strategy 4: Try direct URL (if path starts with http/https)
            if (path.startsWith("http://") || path.startsWith("https://")) {
                this.image.set(new Image(path));
                System.out.println("✅ Image loaded from URL: " + path);
                return;
            }
            
            // All strategies failed
            System.err.println("❌ Failed to load image from any source: " + path);
            
        } catch (Exception e) {
            System.err.println("❌ Exception loading image: " + path);
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Stone ID
    public int getStoneId() {
        return stoneId.get();
    }

    public void setStoneId(int stoneId) {
        this.stoneId.set(stoneId);
    }

    public IntegerProperty stoneIdProperty() {
        return stoneId;
    }

    // Name
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    // Type
    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public StringProperty typeProperty() {
        return type;
    }

    // Size
    public String getSize() {
        return size.get();
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public StringProperty sizeProperty() {
        return size;
    }

    // Price Per Unit
    public double getPricePerUnit() {
        return pricePerUnit.get();
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit.set(pricePerUnit);
    }

    public DoubleProperty pricePerUnitProperty() {
        return pricePerUnit;
    }

    // Quantity in Stock
    public int getQuantityInStock() {
        return quantityInStock.get();
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock.set(quantityInStock);
    }

    public IntegerProperty quantityInStockProperty() {
        return quantityInStock;
    }

    // Image Path
    public String getImagePath() {
        return imagePath.get();
    }

    public void setImagePath(String imagePath) {
        this.imagePath.set(imagePath);
        loadImage(imagePath);
    }

    public StringProperty imagePathProperty() {
        return imagePath;
    }

    // Image
    public Image getImage() {
        return image.get();
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    @Override
    public String toString() {
        return "Stone{" +
                "stoneId=" + stoneId.get() +
                ", name='" + name.get() + '\'' +
                ", type='" + type.get() + '\'' +
                ", size='" + size.get() + '\'' +
                ", pricePerUnit=" + pricePerUnit.get() +
                ", quantityInStock=" + quantityInStock.get() +
                ", imagePath='" + imagePath.get() + '\'' +
                '}';
    }
}

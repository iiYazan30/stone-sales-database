package com.DB.databaseproject.model;

import javafx.beans.property.*;

/**
 * OrderDetails Model Class
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class OrderDetails {
    private final IntegerProperty orderId;
    private final IntegerProperty stoneId;
    private final StringProperty stoneName;
    private final IntegerProperty quantity;
    private final DoubleProperty unitPrice;
    private final DoubleProperty subtotal;

    /**
     * Constructor with all fields
     */
    public OrderDetails(int orderId, int stoneId, String stoneName, int quantity, double unitPrice) {
        this.orderId = new SimpleIntegerProperty(orderId);
        this.stoneId = new SimpleIntegerProperty(stoneId);
        this.stoneName = new SimpleStringProperty(stoneName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.unitPrice = new SimpleDoubleProperty(unitPrice);
        this.subtotal = new SimpleDoubleProperty(quantity * unitPrice);
    }

    /**
     * Default constructor
     */
    public OrderDetails() {
        this(0, 0, "", 0, 0.0);
    }

    // Order ID
    public int getOrderId() {
        return orderId.get();
    }

    public void setOrderId(int orderId) {
        this.orderId.set(orderId);
    }

    public IntegerProperty orderIdProperty() {
        return orderId;
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

    // Stone Name
    public String getStoneName() {
        return stoneName.get();
    }

    public void setStoneName(String stoneName) {
        this.stoneName.set(stoneName);
    }

    public StringProperty stoneNameProperty() {
        return stoneName;
    }

    // Quantity
    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
        updateSubtotal();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    // Unit Price
    public double getUnitPrice() {
        return unitPrice.get();
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice.set(unitPrice);
        updateSubtotal();
    }

    public DoubleProperty unitPriceProperty() {
        return unitPrice;
    }

    // Subtotal
    public double getSubtotal() {
        return subtotal.get();
    }

    public DoubleProperty subtotalProperty() {
        return subtotal;
    }

    private void updateSubtotal() {
        this.subtotal.set(quantity.get() * unitPrice.get());
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "orderId=" + orderId.get() +
                ", stoneId=" + stoneId.get() +
                ", quantity=" + quantity.get() +
                ", unitPrice=" + unitPrice.get() +
                ", subtotal=" + subtotal.get() +
                '}';
    }
}

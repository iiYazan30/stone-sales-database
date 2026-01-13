package com.DB.databaseproject.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class CustomOrder {
    private final IntegerProperty customOrderId;
    private final IntegerProperty customerId;
    private final StringProperty customerName;
    private final StringProperty stoneType;
    private final StringProperty stoneDescription;
    private final StringProperty size;
    private final IntegerProperty quantity;
    private final StringProperty status;
    private final ObjectProperty<LocalDateTime> createdAt;

    public CustomOrder() {
        this(0, 0, "", "", "", "", 0, "Pending", LocalDateTime.now());
    }

    public CustomOrder(int customOrderId, int customerId, String customerName, String stoneType, 
                      String stoneDescription, String size, int quantity, String status, LocalDateTime createdAt) {
        this.customOrderId = new SimpleIntegerProperty(customOrderId);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.customerName = new SimpleStringProperty(customerName);
        this.stoneType = new SimpleStringProperty(stoneType);
        this.stoneDescription = new SimpleStringProperty(stoneDescription);
        this.size = new SimpleStringProperty(size);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.status = new SimpleStringProperty(status);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
    }

    public int getCustomOrderId() {
        return customOrderId.get();
    }

    public void setCustomOrderId(int customOrderId) {
        this.customOrderId.set(customOrderId);
    }

    public IntegerProperty customOrderIdProperty() {
        return customOrderId;
    }

    public int getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public String getStoneType() {
        return stoneType.get();
    }

    public void setStoneType(String stoneType) {
        this.stoneType.set(stoneType);
    }

    public StringProperty stoneTypeProperty() {
        return stoneType;
    }

    public String getStoneDescription() {
        return stoneDescription.get();
    }

    public void setStoneDescription(String stoneDescription) {
        this.stoneDescription.set(stoneDescription);
    }

    public StringProperty stoneDescriptionProperty() {
        return stoneDescription;
    }

    public String getSize() {
        return size.get();
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }
}

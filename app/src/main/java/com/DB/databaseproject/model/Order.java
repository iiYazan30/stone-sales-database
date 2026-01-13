package com.DB.databaseproject.model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Order Model Class
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class Order {
    private final IntegerProperty orderId;
    private final IntegerProperty customerId;
    private final IntegerProperty employeeId; // Can be null (0 means unassigned)
    private final StringProperty customerName;
    private final StringProperty employeeName;
    private final ObjectProperty<LocalDate> orderDate;
    private final DoubleProperty totalAmount;
    private final StringProperty status;

    /**
     * Constructor with all fields
     */
    public Order(int orderId, int customerId, Integer employeeId, String customerName, String employeeName, 
                LocalDate orderDate, double totalAmount, String status) {
        this.orderId = new SimpleIntegerProperty(orderId);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.employeeId = new SimpleIntegerProperty(employeeId != null ? employeeId : 0);
        this.customerName = new SimpleStringProperty(customerName);
        this.employeeName = new SimpleStringProperty(employeeName);
        this.orderDate = new SimpleObjectProperty<>(orderDate);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.status = new SimpleStringProperty(status);
    }

    /**
     * Default constructor
     */
    public Order() {
        this(0, 0, null, "", "", LocalDate.now(), 0.0, "Pending");
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

    // Customer ID
    public int getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    // Employee ID
    public int getEmployeeId() {
        return employeeId.get();
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId.set(employeeId);
    }

    public IntegerProperty employeeIdProperty() {
        return employeeId;
    }
    
    public boolean hasEmployee() {
        return employeeId.get() > 0;
    }

    // Customer Name
    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    // Employee Name
    public String getEmployeeName() {
        return employeeName.get();
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName.set(employeeName);
    }

    public StringProperty employeeNameProperty() {
        return employeeName;
    }

    // Order Date
    public LocalDate getOrderDate() {
        return orderDate.get();
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate.set(orderDate);
    }

    public ObjectProperty<LocalDate> orderDateProperty() {
        return orderDate;
    }

    // Total Amount
    public double getTotalAmount() {
        return totalAmount.get();
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount.set(totalAmount);
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    // Status
    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId.get() +
                ", customerName='" + customerName.get() + '\'' +
                ", employeeName='" + employeeName.get() + '\'' +
                ", orderDate=" + orderDate.get() +
                ", totalAmount=" + totalAmount.get() +
                ", status='" + status.get() + '\'' +
                '}';
    }
}

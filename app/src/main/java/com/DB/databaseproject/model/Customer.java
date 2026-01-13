package com.DB.databaseproject.model;

import javafx.beans.property.*;

/**
 * Customer Model Class
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class Customer {
    private final IntegerProperty customerId;
    private final StringProperty fullName;
    private final StringProperty phoneNumber;
    private final StringProperty address;

    /**
     * Constructor with all fields
     */
    public Customer(int customerId, String fullName, String phoneNumber, String address) {
        this.customerId = new SimpleIntegerProperty(customerId);
        this.fullName = new SimpleStringProperty(fullName);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.address = new SimpleStringProperty(address);
    }

    /**
     * Default constructor
     */
    public Customer() {
        this(0, "", "", "");
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

    // Full Name
    public String getFullName() {
        return fullName.get();
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    // Phone Number
    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    // Address
    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId.get() +
                ", fullName='" + fullName.get() + '\'' +
                ", phoneNumber='" + phoneNumber.get() + '\'' +
                ", address='" + address.get() + '\'' +
                '}';
    }
}

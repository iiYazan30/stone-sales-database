package com.DB.databaseproject.model;

import javafx.beans.property.*;

/**
 * User Model Class
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class User {
    private final IntegerProperty userId;
    private final StringProperty userName;
    private final StringProperty password;
    private final StringProperty phoneNumber;
    private final StringProperty email;
    private final StringProperty firstName;
    private final StringProperty middleName;
    private final StringProperty lastName;
    private final StringProperty role;
    private final StringProperty address;

    /**
     * Constructor with all fields
     */
    public User(int userId, String userName, String password, String phoneNumber, String email,
                String firstName, String middleName, String lastName, String role, String address) {
        this.userId = new SimpleIntegerProperty(userId);
        this.userName = new SimpleStringProperty(userName);
        this.password = new SimpleStringProperty(password);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.email = new SimpleStringProperty(email);
        this.firstName = new SimpleStringProperty(firstName);
        this.middleName = new SimpleStringProperty(middleName);
        this.lastName = new SimpleStringProperty(lastName);
        this.role = new SimpleStringProperty(role);
        this.address = new SimpleStringProperty(address);
    }

    /**
     * Default constructor
     */
    public User() {
        this(0, "", "", "", "", "", "", "", "Customer", "");
    }

    // User ID
    public int getUserId() {
        return userId.get();
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    // Username
    public String getUserName() {
        return userName.get();
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    // Password
    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
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

    // Email
    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    // First Name
    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    // Middle Name
    public String getMiddleName() {
        return middleName.get();
    }

    public void setMiddleName(String middleName) {
        this.middleName.set(middleName);
    }

    public StringProperty middleNameProperty() {
        return middleName;
    }

    // Last Name
    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    // Role
    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public StringProperty roleProperty() {
        return role;
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

    /**
     * Get full name
     */
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName.get() != null && !firstName.get().isEmpty()) {
            fullName.append(firstName.get());
        }
        if (middleName.get() != null && !middleName.get().isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(middleName.get());
        }
        if (lastName.get() != null && !lastName.get().isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(lastName.get());
        }
        return fullName.toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId.get() +
                ", userName='" + userName.get() + '\'' +
                ", role='" + role.get() + '\'' +
                ", email='" + email.get() + '\'' +
                '}';
    }
}

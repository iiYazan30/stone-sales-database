package com.DB.databaseproject.model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Employee Model Class
 * Stone Sales Management System - Stone Premium Dark Theme
 */
public class Employee {
    private final IntegerProperty employeeId;
    private final StringProperty fullName;
    private final StringProperty firstName;
    private final StringProperty middleName;
    private final StringProperty lastName;
    private final StringProperty position;
    private final StringProperty phone;
    private final StringProperty address;
    private final DoubleProperty salary;
    private final ObjectProperty<LocalDate> dateHired;

    /**
     * Constructor with all fields
     */
    public Employee(int employeeId, String fullName, String phone, String address, 
                   double salary, LocalDate dateHired) {
        this.employeeId = new SimpleIntegerProperty(employeeId);
        this.fullName = new SimpleStringProperty(fullName);
        this.firstName = new SimpleStringProperty("");
        this.middleName = new SimpleStringProperty("");
        this.lastName = new SimpleStringProperty("");
        this.position = new SimpleStringProperty("");
        this.phone = new SimpleStringProperty(phone);
        this.address = new SimpleStringProperty(address);
        this.salary = new SimpleDoubleProperty(salary);
        this.dateHired = new SimpleObjectProperty<>(dateHired);
    }

    /**
     * Default constructor
     */
    public Employee() {
        this(0, "", "", "", 0.0, LocalDate.now());
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

    // Position
    public String getPosition() {
        return position.get();
    }

    public void setPosition(String position) {
        this.position.set(position);
    }

    public StringProperty positionProperty() {
        return position;
    }

    // Phone
    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public StringProperty phoneProperty() {
        return phone;
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

    // Salary
    public double getSalary() {
        return salary.get();
    }

    public void setSalary(double salary) {
        this.salary.set(salary);
    }

    public DoubleProperty salaryProperty() {
        return salary;
    }

    // Date Hired
    public LocalDate getDateHired() {
        return dateHired.get();
    }

    public void setDateHired(LocalDate dateHired) {
        this.dateHired.set(dateHired);
    }

    public ObjectProperty<LocalDate> dateHiredProperty() {
        return dateHired;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId.get() +
                ", fullName='" + fullName.get() + '\'' +
                ", phone='" + phone.get() + '\'' +
                ", address='" + address.get() + '\'' +
                ", salary=" + salary.get() +
                ", dateHired=" + dateHired.get() +
                '}';
    }
}

package com.DB.databaseproject.service;

import com.DB.databaseproject.dao.CustomerDAO;
import com.DB.databaseproject.dao.EmployeeDAO;
import com.DB.databaseproject.dao.UserDAO;
import com.DB.databaseproject.model.Customer;
import com.DB.databaseproject.model.Employee;
import com.DB.databaseproject.model.User;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Authentication Service
 * Handles user login and signup operations
 */
public class AuthenticationService {
    
    private final UserDAO userDAO;
    private final CustomerDAO customerDAO;
    private final EmployeeDAO employeeDAO;
    
    // Singleton instance
    private static AuthenticationService instance;
    
    // Store current logged-in user
    private User currentUser;
    private Customer currentCustomer;
    private Employee currentEmployee;
    
    private AuthenticationService() {
        this.userDAO = new UserDAO();
        this.customerDAO = new CustomerDAO();
        this.employeeDAO = new EmployeeDAO();
    }
    
    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Login user
     * @return User object if successful, null otherwise
     */
    public User login(String username, String password) {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║      AUTHENTICATION SERVICE - LOGIN             ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println("📥 Received login request");
        System.out.println("👤 Username: '" + username + "'");
        System.out.println("🔑 Password length: " + password.length());
        
        try {
            System.out.println("🔄 Calling UserDAO.authenticate()...\n");
            User user = userDAO.authenticate(username, password);
            
            if (user != null) {
                currentUser = user;
                
                System.out.println("\n✅ Authentication successful!");
                System.out.println("🎯 Loading role-specific data for: " + user.getRole());
                
                // Load customer or employee data based on role
                if ("Customer".equalsIgnoreCase(user.getRole())) {
                    try {
                        currentCustomer = customerDAO.getByUserId(user.getUserId());
                        if (currentCustomer != null) {
                            System.out.println("✓ Customer data loaded: Success");
                            System.out.println("   Customer ID: " + currentCustomer.getCustomerId());
                            System.out.println("   Customer Name: " + currentCustomer.getFullName());
                        } else {
                            System.err.println("❌ Customer data loaded: Failed - Customer not found for User ID: " + user.getUserId());
                        }
                    } catch (SQLException e) {
                        System.err.println("❌ Error loading customer data: " + e.getMessage());
                        e.printStackTrace();
                        currentCustomer = null;
                    }
                } else if ("Employee".equalsIgnoreCase(user.getRole()) || "Admin".equalsIgnoreCase(user.getRole())) {
                    try {
                        currentEmployee = employeeDAO.getByUserId(user.getUserId());
                        System.out.println("✓ Employee data loaded: " + (currentEmployee != null ? "Success" : "Not needed for Admin"));
                    } catch (SQLException e) {
                        System.err.println("❌ Error loading employee data: " + e.getMessage());
                        e.printStackTrace();
                        currentEmployee = null;
                    }
                }
                
                System.out.println("✅ SERVICE: Login successful - " + username + " (" + user.getRole() + ")");
                return user;
            } else {
                System.out.println("\n❌ SERVICE: Authentication returned NULL");
                System.out.println("💡 User not found or password incorrect");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("\n❌ SERVICE: SQL Exception during login!");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Signup new customer
     * @return true if successful
     */
    public boolean signupCustomer(String username, String password, String email, 
                                  String firstName, String middleName, String lastName,
                                  String phone, String address) {
        try {
            // Check if username already exists
            if (userDAO.usernameExists(username)) {
                System.out.println("❌ Signup failed: Username already exists");
                return false;
            }
            
            // Create user
            User user = new User();
            user.setUserName(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setMiddleName(middleName);
            user.setLastName(lastName);
            user.setPhoneNumber(phone);
            user.setAddress(address);
            user.setRole("Customer");
            
            int userId = userDAO.insert(user);
            if (userId > 0) {
                // Create customer
                String fullName = firstName;
                if (middleName != null && !middleName.isEmpty()) {
                    fullName += " " + middleName;
                }
                if (lastName != null && !lastName.isEmpty()) {
                    fullName += " " + lastName;
                }
                
                Customer customer = new Customer();
                customer.setFullName(fullName);
                customer.setPhoneNumber(phone);
                customer.setAddress(address);
                
                int customerId = customerDAO.insert(customer, userId);
                if (customerId > 0) {
                    System.out.println("✅ Customer signup successful: " + username);
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Signup error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create new employee (Admin only)
     * @return true if successful
     */
    public boolean createEmployee(String username, String password, String email,
                                  String firstName, String middleName, String lastName,
                                  String phone, String address, double salary, String role) {
        try {
            // Check if username already exists
            if (userDAO.usernameExists(username)) {
                System.out.println("❌ Employee creation failed: Username already exists");
                return false;
            }
            
            // Create user
            User user = new User();
            user.setUserName(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setMiddleName(middleName);
            user.setLastName(lastName);
            user.setPhoneNumber(phone);
            user.setAddress(address);
            user.setRole(role); // Employee or Admin
            
            int userId = userDAO.insert(user);
            if (userId > 0) {
                // Create employee
                String fullName = firstName;
                if (middleName != null && !middleName.isEmpty()) {
                    fullName += " " + middleName;
                }
                if (lastName != null && !lastName.isEmpty()) {
                    fullName += " " + lastName;
                }
                
                Employee employee = new Employee();
                employee.setFullName(fullName);
                employee.setPhone(phone);
                employee.setAddress(address);
                employee.setSalary(salary);
                employee.setDateHired(LocalDate.now());
                
                int employeeId = employeeDAO.insert(employee, userId);
                if (employeeId > 0) {
                    System.out.println("✅ Employee created successfully: " + username);
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Employee creation error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Logout current user
     */
    public void logout() {
        currentUser = null;
        currentCustomer = null;
        currentEmployee = null;
        System.out.println("✅ User logged out");
    }

    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get current customer (if logged in as customer)
     */
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    /**
     * Get current employee (if logged in as employee)
     */
    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        return currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Check if current user is employee
     */
    public boolean isEmployee() {
        return currentUser != null && "Employee".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Check if current user is customer
     */
    public boolean isCustomer() {
        return currentUser != null && "Customer".equalsIgnoreCase(currentUser.getRole());
    }
    
    /**
     * Reload customer data from database (used as fallback if customer object is null)
     * This should not normally be needed, but provides a safety net
     */
    public boolean reloadCustomerData() {
        if (currentUser == null) {
            System.err.println("❌ Cannot reload customer data: No user logged in");
            return false;
        }
        
        if (!"Customer".equalsIgnoreCase(currentUser.getRole())) {
            System.err.println("❌ Cannot reload customer data: Current user is not a customer");
            return false;
        }
        
        try {
            System.out.println("🔄 Reloading customer data for User ID: " + currentUser.getUserId());
            currentCustomer = customerDAO.getByUserId(currentUser.getUserId());
            
            if (currentCustomer != null) {
                System.out.println("✅ Customer data reloaded successfully");
                System.out.println("   Customer ID: " + currentCustomer.getCustomerId());
                System.out.println("   Customer Name: " + currentCustomer.getFullName());
                return true;
            } else {
                System.err.println("❌ Customer data not found in database for User ID: " + currentUser.getUserId());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error reloading customer data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

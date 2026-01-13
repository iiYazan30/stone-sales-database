package com.DB.databaseproject.service;

import com.DB.databaseproject.dao.CustomerDAO;
import com.DB.databaseproject.model.Customer;

import java.sql.SQLException;
import java.util.List;

/**
 * Customer Service
 * Handles customer-related business logic
 */
public class CustomerService {
    
    private final CustomerDAO customerDAO;
    
    // Singleton instance
    private static CustomerService instance;
    
    private CustomerService() {
        this.customerDAO = new CustomerDAO();
    }
    
    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }

    /**
     * Update customer
     */
    public boolean updateCustomer(Customer customer) {
        try {
            boolean updated = customerDAO.update(customer);
            if (updated) {
                System.out.println("✅ Customer updated successfully: " + customer.getFullName());
            }
            return updated;
        } catch (SQLException e) {
            System.err.println("❌ Customer update error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete customer
     */
    public boolean deleteCustomer(int customerId) {
        try {
            boolean deleted = customerDAO.delete(customerId);
            if (deleted) {
                System.out.println("✅ Customer deleted: Customer ID " + customerId);
            }
            return deleted;
        } catch (SQLException e) {
            System.err.println("❌ Customer deletion error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        try {
            return customerDAO.getAll();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching customers: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Search customers by name
     */
    public List<Customer> searchCustomers(String searchTerm) {
        try {
            return customerDAO.searchByName(searchTerm);
        } catch (SQLException e) {
            System.err.println("❌ Error searching customers: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get customer by ID
     */
    public Customer getCustomerById(int customerId) {
        try {
            return customerDAO.getById(customerId);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching customer: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get customer by user ID
     */
    public Customer getCustomerByUserId(int userId) {
        try {
            return customerDAO.getByUserId(userId);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching customer: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get customer count
     */
    public int getCustomerCount() {
        try {
            return customerDAO.getCount();
        } catch (SQLException e) {
            System.err.println("❌ Error getting customer count: " + e.getMessage());
            return 0;
        }
    }
}

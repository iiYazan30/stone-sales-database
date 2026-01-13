package com.DB.databaseproject.dao;

import com.DB.databaseproject.model.Customer;
import com.DB.databaseproject.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Customer table
 * Handles all database operations for customers
 * Uses multiline text blocks for SQL queries
 */
public class CustomerDAO {

    /**
     * Insert a new customer
     */
    public int insert(Customer customer, int userId) throws SQLException {
        String sql = """
            INSERT INTO "Customer"
            ("User_ID", "First_Name", "Middle_Name", "Last_Name", "Phone_Number", "Address")
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        // Extract names from fullName
        String fullName = customer.getFullName();
        String[] names = fullName.split(" ");
        String firstName = names.length > 0 ? names[0] : "";
        String middleName = names.length > 2 ? names[1] : "";
        String lastName = names.length > 1 ? names[names.length - 1] : "";
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 CustomerDAO.insert() - Executing INSERT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Parameters:");
        System.out.println("   User_ID: " + userId);
        System.out.println("   First_Name: " + firstName);
        System.out.println("   Middle_Name: " + middleName);
        System.out.println("   Last_Name: " + lastName);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, firstName);
            pstmt.setString(3, middleName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, customer.getPhoneNumber());
            pstmt.setString(6, customer.getAddress());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int customerId = rs.getInt(1);
                    System.out.println("✅ Customer created with ID: " + customerId);
                    System.out.println("═══════════════════════════════════════════════");
                    return customerId;
                }
            }
            System.out.println("═══════════════════════════════════════════════");
            return -1;
        } catch (SQLException e) {
            System.err.println("❌ INSERT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Update an existing customer
     */
    public boolean update(Customer customer) throws SQLException {
        String sql = """
            UPDATE "Customer"
            SET "First_Name" = ?, "Middle_Name" = ?, "Last_Name" = ?,
                "Phone_Number" = ?, "Address" = ?
            WHERE "Customer_ID" = ?
            """;
        
        String fullName = customer.getFullName();
        String[] names = fullName.split(" ");
        String firstName = names.length > 0 ? names[0] : "";
        String middleName = names.length > 2 ? names[1] : "";
        String lastName = names.length > 1 ? names[names.length - 1] : "";
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 CustomerDAO.update() - Executing UPDATE");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Customer_ID: " + customer.getCustomerId());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, firstName);
            pstmt.setString(2, middleName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, customer.getPhoneNumber());
            pstmt.setString(5, customer.getAddress());
            pstmt.setInt(6, customer.getCustomerId());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Rows affected: " + rowsAffected);
            System.out.println("═══════════════════════════════════════════════");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ UPDATE ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Delete a customer by ID
     */
    public boolean delete(int customerId) throws SQLException {
        String sql = """
            DELETE FROM "Customer"
            WHERE "Customer_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 CustomerDAO.delete() - Executing DELETE");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Customer_ID: " + customerId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Rows affected: " + rowsAffected);
            System.out.println("═══════════════════════════════════════════════");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ DELETE ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Get customer by ID
     */
    public Customer getById(int customerId) throws SQLException {
        String sql = """
            SELECT * FROM "Customer"
            WHERE "Customer_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 CustomerDAO.getById() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Customer_ID: " + customerId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = extractCustomerFromResultSet(rs);
                System.out.println("✅ Customer found");
                System.out.println("═══════════════════════════════════════════════");
                return customer;
            } else {
                System.out.println("❌ No customer found with ID: " + customerId);
                System.out.println("═══════════════════════════════════════════════");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Get customer by user ID
     */
    public Customer getByUserId(int userId) throws SQLException {
        String sql = """
            SELECT * FROM "Customer"
            WHERE "User_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 CustomerDAO.getByUserId() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 User_ID: " + userId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = extractCustomerFromResultSet(rs);
                System.out.println("✅ Customer found");
                System.out.println("═══════════════════════════════════════════════");
                return customer;
            } else {
                System.out.println("❌ No customer found with User_ID: " + userId);
                System.out.println("═══════════════════════════════════════════════");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.out.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Get all customers
     */
    public List<Customer> getAll() throws SQLException {
        String sql = """
            SELECT * FROM "Customer"
            ORDER BY "Customer_ID"
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 CustomerDAO.getAll() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
            
            System.out.println("✅ Retrieved " + customers.size() + " customers");
            System.out.println("═══════════════════════════════════════════════");
            return customers;
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Search customers by name
     */
    public List<Customer> searchByName(String searchTerm) throws SQLException {
        String sql = """
            SELECT * FROM "Customer"
            WHERE LOWER("First_Name" || ' ' || COALESCE("Middle_Name", '') || ' ' || "Last_Name") LIKE LOWER(?)
            ORDER BY "Customer_ID"
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 CustomerDAO.searchByName() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Search term: " + searchTerm);
        
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
            
            System.out.println("✅ Found " + customers.size() + " customers");
            System.out.println("═══════════════════════════════════════════════");
            return customers;
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Get customer count
     */
    public int getCount() throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM "Customer"
            """;
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    /**
     * Extract Customer object from ResultSet
     */
    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        String firstName = rs.getString("First_Name");
        String middleName = rs.getString("Middle_Name");
        String lastName = rs.getString("Last_Name");
        
        String fullName = firstName;
        if (middleName != null && !middleName.isEmpty()) {
            fullName += " " + middleName;
        }
        if (lastName != null && !lastName.isEmpty()) {
            fullName += " " + lastName;
        }
        
        return new Customer(
            rs.getInt("Customer_ID"),
            fullName,
            rs.getString("Phone_Number"),
            rs.getString("Address")
        );
    }
}

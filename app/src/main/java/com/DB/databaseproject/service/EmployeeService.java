package com.DB.databaseproject.service;

import com.DB.databaseproject.dao.EmployeeDAO;
import com.DB.databaseproject.dao.UserDAO;
import com.DB.databaseproject.model.Employee;

import java.sql.SQLException;
import java.util.List;

/**
 * Employee Service
 * Handles employee-related business logic
 */
public class EmployeeService {
    
    private final EmployeeDAO employeeDAO;
    private final UserDAO userDAO;
    
    // Singleton instance
    private static EmployeeService instance;
    
    private EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
        this.userDAO = new UserDAO();
    }
    
    public static EmployeeService getInstance() {
        if (instance == null) {
            instance = new EmployeeService();
        }
        return instance;
    }

    /**
     * Update employee
     */
    public boolean updateEmployee(Employee employee) {
        try {
            boolean updated = employeeDAO.update(employee);
            if (updated) {
                System.out.println("✅ Employee updated successfully: " + employee.getFullName());
            }
            return updated;
        } catch (SQLException e) {
            System.err.println("❌ Employee update error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete employee and associated user
     * This method ensures both Employee and User records are deleted
     * Also handles unassigning any orders that were assigned to this employee
     * 
     * @param employeeId The ID of the employee to delete
     * @return true if both deletions succeeded, false otherwise
     */
    public boolean deleteEmployee(int employeeId) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         EMPLOYEE SERVICE - DELETE OPERATION                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("🗑️  Attempting to delete Employee ID: " + employeeId);
        System.out.println("📌 Note: Any assigned orders will be automatically unassigned");
        
        try {
            // Step 1: Get the User_ID associated with this employee
            System.out.println("\n📋 Step 1: Fetching User_ID for Employee_ID: " + employeeId);
            int userId = employeeDAO.getUserIdByEmployeeId(employeeId);
            
            if (userId <= 0) {
                System.err.println("❌ ERROR: Could not find User_ID for Employee_ID: " + employeeId);
                System.err.println("   The employee may not exist or data is corrupted.");
                System.out.println("╚════════════════════════════════════════════════════════════╝\n");
                return false;
            }
            
            System.out.println("✅ Found User_ID: " + userId);
            
            // Step 2: Delete from Employee table
            // This will also unassign any orders (set Employee_ID to NULL)
            // The DAO handles this with a transaction
            System.out.println("\n📋 Step 2: Deleting from Employee table...");
            System.out.println("   (Orders will be unassigned automatically if any exist)");
            
            boolean employeeDeleted = employeeDAO.deleteEmployeeById(employeeId);
            
            if (!employeeDeleted) {
                System.err.println("❌ ERROR: Failed to delete from Employee table!");
                System.err.println("   Employee_ID: " + employeeId);
                System.err.println("   Check console for detailed error messages");
                System.out.println("╚════════════════════════════════════════════════════════════╝\n");
                return false;
            }
            
            System.out.println("✅ Employee record deleted successfully!");
            System.out.println("✅ All orders previously assigned to this employee are now unassigned");
            
            // Step 3: Delete from User table
            System.out.println("\n📋 Step 3: Deleting from User table...");
            boolean userDeleted = userDAO.deleteUserById(userId);
            
            if (!userDeleted) {
                System.err.println("⚠️  WARNING: Employee deleted but User record deletion failed!");
                System.err.println("   User_ID: " + userId);
                System.err.println("   This may cause orphaned User records in the database.");
                System.err.println("   However, the employee and order unassignment were successful.");
                System.out.println("╚════════════════════════════════════════════════════════════╝\n");
                return false;
            }
            
            System.out.println("✅ User record deleted successfully!");
            System.out.println("\n✅ ✅ ✅ COMPLETE DELETION SUCCESSFUL! ✅ ✅ ✅");
            System.out.println("   Employee_ID: " + employeeId + " (deleted)");
            System.out.println("   User_ID: " + userId + " (deleted)");
            System.out.println("   Orders: All previously assigned orders are now unassigned");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("\n❌ ❌ ❌ DATABASE ERROR DURING DELETION! ❌ ❌ ❌");
            System.err.println("   Employee_ID: " + employeeId);
            System.err.println("   Error Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            System.err.println("\n💡 Possible causes:");
            System.err.println("   - Database connection issue");
            System.err.println("   - Invalid Employee_ID");
            System.err.println("   - Transaction error");
            System.err.println("   - Unexpected foreign key constraint");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("\n❌ ❌ ❌ UNEXPECTED ERROR! ❌ ❌ ❌");
            System.err.println("   Employee_ID: " + employeeId);
            System.err.println("   Error: " + e.getMessage());
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all employees
     */
    public List<Employee> getAllEmployees() {
        try {
            return employeeDAO.getAll();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching employees: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Search employees by name
     */
    public List<Employee> searchEmployees(String searchTerm) {
        try {
            return employeeDAO.searchByName(searchTerm);
        } catch (SQLException e) {
            System.err.println("❌ Error searching employees: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get employee by ID
     */
    public Employee getEmployeeById(int employeeId) {
        try {
            return employeeDAO.getById(employeeId);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching employee: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get employee by user ID
     */
    public Employee getEmployeeByUserId(int userId) {
        try {
            return employeeDAO.getByUserId(userId);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching employee: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get User_ID for a given Employee_ID
     * Used when editing an employee to load user information
     */
    public int getUserIdByEmployeeId(int employeeId) {
        try {
            return employeeDAO.getUserIdByEmployeeId(employeeId);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching User_ID: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Get employee count
     */
    public int getEmployeeCount() {
        try {
            return employeeDAO.getCount();
        } catch (SQLException e) {
            System.err.println("❌ Error getting employee count: " + e.getMessage());
            return 0;
        }
    }
}

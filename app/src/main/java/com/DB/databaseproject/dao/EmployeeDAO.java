package com.DB.databaseproject.dao;

import com.DB.databaseproject.model.Employee;
import com.DB.databaseproject.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Employee table
 * Handles all database operations for employees
 * Uses multiline text blocks for SQL queries
 */
public class EmployeeDAO {

    /**
     * Insert a new employee
     */
    public int insert(Employee employee, int userId) throws SQLException {
        String sql = """
            INSERT INTO "Employee"
            ("User_ID", "Salary", "First_Name", "Middle_Name", "Last_Name", "Phone_Number", "Address", "Date_Hired")
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        // Extract names from fullName
        String fullName = employee.getFullName();
        String[] names = fullName.split(" ");
        String firstName = names.length > 0 ? names[0] : "";
        String middleName = names.length > 2 ? names[1] : "";
        String lastName = names.length > 1 ? names[names.length - 1] : "";
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 EmployeeDAO.insert() - Executing INSERT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Parameters:");
        System.out.println("   User_ID: " + userId);
        System.out.println("   Salary: " + employee.getSalary());
        System.out.println("   First_Name: " + firstName);
        System.out.println("   Middle_Name: " + middleName);
        System.out.println("   Last_Name: " + lastName);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, employee.getSalary());
            pstmt.setString(3, firstName);
            pstmt.setString(4, middleName);
            pstmt.setString(5, lastName);
            pstmt.setString(6, employee.getPhone());
            pstmt.setString(7, employee.getAddress());
            pstmt.setDate(8, Date.valueOf(employee.getDateHired()));
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int employeeId = rs.getInt(1);
                    System.out.println("✅ Employee created with ID: " + employeeId);
                    System.out.println("═══════════════════════════════════════════════");
                    return employeeId;
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
     * Update an existing employee
     */
    public boolean update(Employee employee) throws SQLException {
        String sql = """
            UPDATE "Employee"
            SET "Salary" = ?, "First_Name" = ?, "Middle_Name" = ?, "Last_Name" = ?,
                "Phone_Number" = ?, "Address" = ?, "Date_Hired" = ?
            WHERE "Employee_ID" = ?
            """;
        
        String fullName = employee.getFullName();
        String[] names = fullName.split(" ");
        String firstName = names.length > 0 ? names[0] : "";
        String middleName = names.length > 2 ? names[1] : "";
        String lastName = names.length > 1 ? names[names.length - 1] : "";
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 EmployeeDAO.update() - Executing UPDATE");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Employee_ID: " + employee.getEmployeeId());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, employee.getSalary());
            pstmt.setString(2, firstName);
            pstmt.setString(3, middleName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, employee.getPhone());
            pstmt.setString(6, employee.getAddress());
            pstmt.setDate(7, Date.valueOf(employee.getDateHired()));
            pstmt.setInt(8, employee.getEmployeeId());
            
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
     * Delete an employee by ID
     * This method now handles the case where employee has assigned orders
     * Since the DB has ON DELETE SET NULL, the orders will be automatically unassigned
     */
    public boolean deleteEmployeeById(int employeeId) throws SQLException {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 EmployeeDAO.deleteEmployeeById() - Executing DELETE with Order handling");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📝 Employee_ID: " + employeeId);
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            
            // IMPORTANT: Don't use try-with-resources here because we need manual transaction control
            // Disable auto-commit for transaction
            conn.setAutoCommit(false);
            
            System.out.println("🔄 Transaction started (auto-commit disabled)");
            
            // Step 1: Check how many orders are assigned to this employee
            String checkOrdersSql = """
                SELECT COUNT(*) FROM "Orders"
                WHERE "Employee_ID" = ?
                """;
            
            int orderCount = 0;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkOrdersSql)) {
                checkStmt.setInt(1, employeeId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    orderCount = rs.getInt(1);
                }
            }
            
            System.out.println("📊 Found " + orderCount + " orders assigned to this employee");
            
            if (orderCount > 0) {
                // Step 2: Set all orders to NULL (unassign them)
                String unassignOrdersSql = """
                    UPDATE "Orders"
                    SET "Employee_ID" = NULL
                    WHERE "Employee_ID" = ?
                    """;
                
                System.out.println("📋 Step 1: Unassigning orders...");
                System.out.println("   SQL: " + unassignOrdersSql);
                
                try (PreparedStatement unassignStmt = conn.prepareStatement(unassignOrdersSql)) {
                    unassignStmt.setInt(1, employeeId);
                    int rowsUpdated = unassignStmt.executeUpdate();
                    System.out.println("✅ Unassigned " + rowsUpdated + " orders (set Employee_ID to NULL)");
                }
            } else {
                System.out.println("✅ No orders assigned to this employee - skipping unassignment step");
            }
            
            // Step 3: Delete the employee
            String deleteEmployeeSql = """
                DELETE FROM "Employee"
                WHERE "Employee_ID" = ?
                """;
            
            System.out.println("\n📋 Step 2: Deleting employee record...");
            System.out.println("   SQL: " + deleteEmployeeSql);
            
            int rowsDeleted;
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteEmployeeSql)) {
                deleteStmt.setInt(1, employeeId);
                rowsDeleted = deleteStmt.executeUpdate();
            }
            
            if (rowsDeleted > 0) {
                System.out.println("✅ Employee record deleted successfully!");
                
                // Commit the transaction
                conn.commit();
                System.out.println("✅ Transaction committed successfully!");
                System.out.println("═══════════════════════════════════════════════");
                return true;
            } else {
                System.err.println("❌ Failed to delete employee - no rows affected");
                conn.rollback();
                System.out.println("🔄 Transaction rolled back");
                System.out.println("═══════════════════════════════════════════════");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("\n❌ DELETE ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            
            // Rollback transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("🔄 Transaction rolled back due to error");
                } catch (SQLException rollbackEx) {
                    System.err.println("❌ Rollback failed: " + rollbackEx.getMessage());
                }
            }
            
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        } finally {
            // Restore auto-commit and close connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println("✅ Database connection closed successfully!");
                } catch (SQLException e) {
                    System.err.println("⚠️  Warning: Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Delete an employee by ID (alias for backward compatibility)
     */
    public boolean delete(int employeeId) throws SQLException {
        return deleteEmployeeById(employeeId);
    }

    /**
     * Get User_ID for a given Employee_ID
     * This is needed to delete the corresponding User record
     */
    public int getUserIdByEmployeeId(int employeeId) throws SQLException {
        String sql = """
            SELECT "User_ID" FROM "Employee"
            WHERE "Employee_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 EmployeeDAO.getUserIdByEmployeeId() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Employee_ID: " + employeeId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("User_ID");
                System.out.println("✅ User_ID found: " + userId);
                System.out.println("═══════════════════════════════════════════════");
                return userId;
            } else {
                System.out.println("❌ No User_ID found for Employee_ID: " + employeeId);
                System.out.println("═══════════════════════════════════════════════");
                return -1;
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
     * Get employee by ID
     */
    public Employee getById(int employeeId) throws SQLException {
        String sql = """
            SELECT * FROM "Employee"
            WHERE "Employee_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 EmployeeDAO.getById() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Employee_ID: " + employeeId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Employee employee = extractEmployeeFromResultSet(rs);
                System.out.println("✅ Employee found");
                System.out.println("═══════════════════════════════════════════════");
                return employee;
            } else {
                System.out.println("❌ No employee found with ID: " + employeeId);
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
     * Get employee by user ID
     */
    public Employee getByUserId(int userId) throws SQLException {
        String sql = """
            SELECT * FROM "Employee"
            WHERE "User_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 EmployeeDAO.getByUserId() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 User_ID: " + userId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Employee employee = extractEmployeeFromResultSet(rs);
                System.out.println("✅ Employee found");
                System.out.println("═══════════════════════════════════════════════");
                return employee;
            } else {
                System.out.println("❌ No employee found with User_ID: " + userId);
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
     * Get all employees
     */
    public List<Employee> getAll() throws SQLException {
        String sql = """
            SELECT * FROM "Employee"
            ORDER BY "Employee_ID"
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 EmployeeDAO.getAll() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
            
            System.out.println("✅ Retrieved " + employees.size() + " employees");
            System.out.println("═══════════════════════════════════════════════");
            return employees;
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Search employees by name
     */
    public List<Employee> searchByName(String searchTerm) throws SQLException {
        String sql = """
            SELECT * FROM "Employee"
            WHERE LOWER("First_Name" || ' ' || COALESCE("Middle_Name", '') || ' ' || "Last_Name") LIKE LOWER(?)
            ORDER BY "Employee_ID"
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 EmployeeDAO.searchByName() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Search term: " + searchTerm);
        
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
            
            System.out.println("✅ Found " + employees.size() + " employees");
            System.out.println("═══════════════════════════════════════════════");
            return employees;
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Get employee count
     */
    public int getCount() throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM "Employee"
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
     * Extract Employee object from ResultSet
     */
    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
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
        
        return new Employee(
            rs.getInt("Employee_ID"),
            fullName,
            rs.getString("Phone_Number"),
            rs.getString("Address"),
            rs.getDouble("Salary"),
            rs.getDate("Date_Hired").toLocalDate()
        );
    }
}

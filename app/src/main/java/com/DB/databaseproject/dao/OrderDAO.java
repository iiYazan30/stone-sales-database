package com.DB.databaseproject.dao;

import com.DB.databaseproject.model.Order;
import com.DB.databaseproject.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Orders table
 * Handles all database operations for orders
 */
public class OrderDAO {

    /**
     * Insert a new order
     */
    public int insert(Order order, int customerId, Integer employeeId) throws SQLException {
        String sql = """
            INSERT INTO "Orders" 
            ("Customer_ID", "Employee_ID", "Order_Status", "Order_Date", "Total_Amount") 
            VALUES (?, ?, ?, ?, ?) 
            RETURNING "Order_ID"
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            if (employeeId != null) {
                pstmt.setInt(2, employeeId);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, order.getStatus());
            pstmt.setDate(4, Date.valueOf(order.getOrderDate()));
            pstmt.setDouble(5, order.getTotalAmount());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Order_ID");
            }
            return -1;
        }
    }

    /**
     * Create a new order from a custom order
     * Returns the generated Order_ID
     */
    public int createOrderFromCustomOrder(int customerId) throws SQLException {
        String sql = """
            INSERT INTO "Orders" 
            ("Customer_ID", "Employee_ID", "Order_Status", "Order_Date", "Total_Amount", "Payment") 
            VALUES (?, NULL, 'Pending', CURRENT_TIMESTAMP, 0, NULL) 
            RETURNING "Order_ID"
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Order_ID");
            }
            return -1;
        }
    }

    /**
     * Create a new order from a custom order (with transaction support)
     * This method uses the provided connection to participate in a transaction
     * Returns the generated Order_ID
     */
    public int createOrderFromCustomOrder(Connection conn, int customerId) throws SQLException {
        String sql = """
            INSERT INTO "Orders" 
            ("Customer_ID", "Employee_ID", "Order_Status", "Order_Date", "Total_Amount", "Payment") 
            VALUES (?, NULL, 'Pending', CURRENT_TIMESTAMP, 0, NULL) 
            RETURNING "Order_ID"
            """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Order_ID");
            }
            return -1;
        }
    }

    /**
     * Update an existing order
     */
    public boolean update(Order order) throws SQLException {
        String sql = """
            UPDATE "Orders" 
            SET "Order_Status" = ?, "Order_Date" = ?, "Total_Amount" = ? 
            WHERE "Order_ID" = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, order.getStatus());
            pstmt.setDate(2, Date.valueOf(order.getOrderDate()));
            pstmt.setDouble(3, order.getTotalAmount());
            pstmt.setInt(4, order.getOrderId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Update order status
     */
    public boolean updateStatus(int orderId, String status) throws SQLException {
        String sql = """
            UPDATE "Orders" 
            SET "Order_Status" = ? 
            WHERE "Order_ID" = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Assign employee to order
     */
    public boolean assignEmployee(int orderId, int employeeId) throws SQLException {
        String sql = """
            UPDATE "Orders" 
            SET "Employee_ID" = ? 
            WHERE "Order_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 OrderDAO.assignEmployee() - Assigning employee to order");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📝 Order ID: " + orderId);
        System.out.println("📝 Employee ID: " + employeeId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            pstmt.setInt(2, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Employee assigned successfully - " + rowsAffected + " row(s) updated");
                System.out.println("═══════════════════════════════════════════════");
                return true;
            } else {
                System.err.println("❌ No rows updated - Order ID may not exist");
                System.err.println("═══════════════════════════════════════════════");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ ASSIGN EMPLOYEE ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Delete an order by ID
     */
    public boolean delete(int orderId) throws SQLException {
        String sql = "DELETE FROM \"Orders\" WHERE \"Order_ID\" = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Get order by ID
     */
    public Order getById(int orderId) throws SQLException {
        String sql = """
            SELECT o."Order_ID", o."Customer_ID", o."Employee_ID", o."Order_Date", o."Order_Status", o."Total_Amount",
                   u."First_Name" || ' ' || COALESCE(u."Last_Name", '') AS customer_name,
                   COALESCE(u2."First_Name" || ' ' || COALESCE(u2."Last_Name", ''), 'Unassigned') AS employee_name
            FROM "Orders" o
            JOIN "Customer" c ON o."Customer_ID" = c."Customer_ID"
            JOIN "User" u ON c."User_ID" = u."User_ID"
            LEFT JOIN "Employee" e ON o."Employee_ID" = e."Employee_ID"
            LEFT JOIN "User" u2 ON e."User_ID" = u2."User_ID"
            WHERE o."Order_ID" = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractOrderFromResultSet(rs);
            }
            return null;
        }
    }

    /**
     * Get all orders
     */
    public List<Order> getAll() throws SQLException {
        String sql = """
            SELECT o."Order_ID", o."Customer_ID", o."Employee_ID", o."Order_Date", o."Order_Status", o."Total_Amount",
                   u."First_Name" || ' ' || COALESCE(u."Last_Name", '') AS customer_name,
                   COALESCE(u2."First_Name" || ' ' || COALESCE(u2."Last_Name", ''), 'Unassigned') AS employee_name
            FROM "Orders" o
            JOIN "Customer" c ON o."Customer_ID" = c."Customer_ID"
            JOIN "User" u ON c."User_ID" = u."User_ID"
            LEFT JOIN "Employee" e ON o."Employee_ID" = e."Employee_ID"
            LEFT JOIN "User" u2 ON e."User_ID" = u2."User_ID"
            ORDER BY o."Order_ID" DESC
            """;
        
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        }
        return orders;
    }

    /**
     * Get orders by customer ID
     */
    public List<Order> getByCustomerId(int customerId) throws SQLException {
        String sql = """
            SELECT o."Order_ID", o."Customer_ID", o."Employee_ID", o."Order_Date", o."Order_Status", o."Total_Amount",
                   u."First_Name" || ' ' || COALESCE(u."Last_Name", '') AS customer_name,
                   COALESCE(u2."First_Name" || ' ' || COALESCE(u2."Last_Name", ''), 'Unassigned') AS employee_name
            FROM "Orders" o
            JOIN "Customer" c ON o."Customer_ID" = c."Customer_ID"
            JOIN "User" u ON c."User_ID" = u."User_ID"
            LEFT JOIN "Employee" e ON o."Employee_ID" = e."Employee_ID"
            LEFT JOIN "User" u2 ON e."User_ID" = u2."User_ID"
            WHERE o."Customer_ID" = ?
            ORDER BY o."Order_ID" DESC
            """;
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        }
        return orders;
    }

    /**
     * Get orders by employee ID
     */
    public List<Order> getByEmployeeId(int employeeId) throws SQLException {
        String sql = """
            SELECT o."Order_ID", o."Customer_ID", o."Employee_ID", o."Order_Date", o."Order_Status", o."Total_Amount",
                   u."First_Name" || ' ' || COALESCE(u."Last_Name", '') AS customer_name,
                   u2."First_Name" || ' ' || COALESCE(u2."Last_Name", '') AS employee_name
            FROM "Orders" o
            JOIN "Customer" c ON o."Customer_ID" = c."Customer_ID"
            JOIN "User" u ON c."User_ID" = u."User_ID"
            JOIN "Employee" e ON o."Employee_ID" = e."Employee_ID"
            JOIN "User" u2 ON e."User_ID" = u2."User_ID"
            WHERE o."Employee_ID" = ?
            ORDER BY o."Order_ID" DESC
            """;
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        }
        return orders;
    }

    /**
     * Get orders by status
     */
    public List<Order> getByStatus(String status) throws SQLException {
        String sql = """
            SELECT o."Order_ID", o."Customer_ID", o."Employee_ID", o."Order_Date", o."Order_Status", o."Total_Amount",
                   u."First_Name" || ' ' || COALESCE(u."Last_Name", '') AS customer_name,
                   COALESCE(u2."First_Name" || ' ' || COALESCE(u2."Last_Name", ''), 'Unassigned') AS employee_name
            FROM "Orders" o
            JOIN "Customer" c ON o."Customer_ID" = c."Customer_ID"
            JOIN "User" u ON c."User_ID" = u."User_ID"
            LEFT JOIN "Employee" e ON o."Employee_ID" = e."Employee_ID"
            LEFT JOIN "User" u2 ON e."User_ID" = u2."User_ID"
            WHERE o."Order_Status" = ?
            ORDER BY o."Order_ID" DESC
            """;
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        }
        return orders;
    }

    /**
     * Get active orders (Pending, Assigned, Processing)
     * Excludes Completed and Canceled orders
     */
    public List<Order> getActiveOrders() throws SQLException {
        String sql = """
            SELECT o."Order_ID", o."Customer_ID", o."Employee_ID", o."Order_Date", o."Order_Status", o."Total_Amount",
                   u."First_Name" || ' ' || COALESCE(u."Last_Name", '') AS customer_name,
                   COALESCE(u2."First_Name" || ' ' || COALESCE(u2."Last_Name", ''), 'Unassigned') AS employee_name
            FROM "Orders" o
            JOIN "Customer" c ON o."Customer_ID" = c."Customer_ID"
            JOIN "User" u ON c."User_ID" = u."User_ID"
            LEFT JOIN "Employee" e ON o."Employee_ID" = e."Employee_ID"
            LEFT JOIN "User" u2 ON e."User_ID" = u2."User_ID"
            WHERE o."Order_Status" IN ('Pending', 'Assigned', 'Processing')
            ORDER BY o."Order_ID" DESC
            """;
        
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        }
        return orders;
    }

    /**
     * Get archived orders (Completed, Canceled)
     * READ-ONLY orders that are no longer active
     */
    public List<Order> getArchivedOrders() throws SQLException {
        String sql = """
            SELECT o."Order_ID", o."Customer_ID", o."Employee_ID", o."Order_Date", o."Order_Status", o."Total_Amount",
                   u."First_Name" || ' ' || COALESCE(u."Last_Name", '') AS customer_name,
                   COALESCE(u2."First_Name" || ' ' || COALESCE(u2."Last_Name", ''), 'Unassigned') AS employee_name
            FROM "Orders" o
            JOIN "Customer" c ON o."Customer_ID" = c."Customer_ID"
            JOIN "User" u ON c."User_ID" = u."User_ID"
            LEFT JOIN "Employee" e ON o."Employee_ID" = e."Employee_ID"
            LEFT JOIN "User" u2 ON e."User_ID" = u2."User_ID"
            WHERE o."Order_Status" IN ('Completed', 'Canceled')
            ORDER BY o."Order_ID" DESC
            """;
        
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        }
        return orders;
    }

    /**
     * Get order count
     */
    public int getCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM \"Orders\"";
        
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
     * Get total revenue
     */
    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(\"Total_Amount\") FROM \"Orders\" WHERE \"Order_Status\" = 'Completed'";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        }
    }

    /**
     * Get pending orders count
     */
    public int getPendingCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM \"Orders\" WHERE \"Order_Status\" = 'Pending'";
        
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
     * Extract Order object from ResultSet
     * Handles NULL Employee_ID by showing "Unassigned"
     */
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        String customerName = rs.getString("customer_name");
        String employeeName = rs.getString("employee_name");
        
        // Get employee ID (may be null)
        int employeeIdValue = rs.getInt("Employee_ID");
        Integer employeeId = rs.wasNull() ? null : employeeIdValue;
        
        // Ensure employee name is never null or empty
        if (employeeName == null || employeeName.trim().isEmpty()) {
            employeeName = "Unassigned";
        }
        
        return new Order(
            rs.getInt("Order_ID"),
            rs.getInt("Customer_ID"),
            employeeId,
            customerName,
            employeeName,
            rs.getDate("Order_Date").toLocalDate(),
            rs.getDouble("Total_Amount"),
            rs.getString("Order_Status")
        );
    }
    
    /**
     * Get all employees for assignment
     */
    public List<com.DB.databaseproject.model.Employee> getAllEmployees() throws SQLException {
        String sql = """
            SELECT e."Employee_ID", 
                   e."First_Name", 
                   e."Middle_Name", 
                   e."Last_Name",
                   e."Phone_Number",
                   e."Address",
                   e."Salary",
                   e."Date_Hired"
            FROM "Employee" e
            ORDER BY e."First_Name", e."Last_Name"
            """;
        
        List<com.DB.databaseproject.model.Employee> employees = new ArrayList<>();
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 OrderDAO.getAllEmployees() - Fetching employees for assignment");
        System.out.println("═══════════════════════════════════════════════");
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // Build full name from parts
                String firstName = rs.getString("First_Name");
                String middleName = rs.getString("Middle_Name");
                String lastName = rs.getString("Last_Name");
                
                String fullName = firstName;
                if (middleName != null && !middleName.trim().isEmpty()) {
                    fullName += " " + middleName;
                }
                if (lastName != null && !lastName.trim().isEmpty()) {
                    fullName += " " + lastName;
                }
                
                // Create employee object
                com.DB.databaseproject.model.Employee emp = new com.DB.databaseproject.model.Employee(
                    rs.getInt("Employee_ID"),
                    fullName,
                    rs.getString("Phone_Number"),
                    rs.getString("Address"),
                    rs.getDouble("Salary"),
                    rs.getDate("Date_Hired").toLocalDate()
                );
                
                // Set individual name fields for ComboBox display
                emp.setFirstName(firstName);
                emp.setLastName(lastName);
                
                employees.add(emp);
            }
            
            System.out.println("✅ Loaded " + employees.size() + " employees for assignment");
            System.out.println("═══════════════════════════════════════════════");
        }
        return employees;
    }
    
    /**
     * Get customer email and first name for order notification
     * Returns String array: [0] = email, [1] = first name
     * Returns null if customer not found or no email
     */
    public String[] getCustomerEmailByOrderId(int orderId) throws SQLException {
        String sql = """
            SELECT u."Email", u."First_Name"
            FROM "Orders" o
            JOIN "Customer" c ON o."Customer_ID" = c."Customer_ID"
            JOIN "User" u ON c."User_ID" = u."User_ID"
            WHERE o."Order_ID" = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String email = rs.getString("Email");
                String firstName = rs.getString("First_Name");
                
                // Return null if email is null or empty
                if (email == null || email.trim().isEmpty()) {
                    return null;
                }
                
                return new String[]{email, firstName};
            }
            return null;
        }
    }
}

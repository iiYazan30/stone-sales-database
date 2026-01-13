package com.DB.databaseproject.dao;

import com.DB.databaseproject.model.CustomOrder;
import com.DB.databaseproject.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomOrderDAO {

    public int insert(CustomOrder customOrder, int customerId) throws SQLException {
        String sql = """
            INSERT INTO "Custom_Orders" 
            ("Customer_ID", "Stone_Type", "Stone_Description", "Size", "Requested_Quantity", "Status", "Created_At")
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING "Custom_Order_ID"
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            pstmt.setString(2, customOrder.getStoneType());
            pstmt.setString(3, customOrder.getStoneDescription());
            pstmt.setString(4, customOrder.getSize());
            pstmt.setInt(5, customOrder.getQuantity());
            pstmt.setString(6, "Pending");
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Custom_Order_ID");
            }
            return -1;
        }
    }

    public boolean updateStatus(int customOrderId, String status) throws SQLException {
        String sql = """
            UPDATE "Custom_Orders"
            SET "Status" = ?
            WHERE "Custom_Order_ID" = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, customOrderId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Update custom order status and link it to a real Order_ID
     * Used when converting custom order to real order
     */
    public boolean updateStatusAndOrderId(int customOrderId, String status, int orderId) throws SQLException {
        String sql = """
            UPDATE "Custom_Orders"
            SET "Status" = ?
            WHERE "Custom_Order_ID" = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, customOrderId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Update custom order status and link it to a real Order_ID (with transaction support)
     * This method uses the provided connection to participate in a transaction
     */
    public boolean updateStatusAndOrderId(Connection conn, int customOrderId, String status, int orderId) throws SQLException {
        String sql = """
            UPDATE "Custom_Orders"
            SET "Status" = ?
            WHERE "Custom_Order_ID" = ?
            """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, customOrderId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<CustomOrder> getAll() throws SQLException {
        String sql = """
            SELECT co."Custom_Order_ID", co."Customer_ID", co."Stone_Type", co."Stone_Description",
                   co."Size", co."Requested_Quantity", co."Status", co."Created_At",
                   u."First_Name" || ' ' || COALESCE(u."Last_Name", '') AS customer_name
            FROM "Custom_Orders" co
            JOIN "Customer" c ON co."Customer_ID" = c."Customer_ID"
            JOIN "User" u ON c."User_ID" = u."User_ID"
            ORDER BY co."Created_At" DESC
            """;
        
        List<CustomOrder> customOrders = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customOrders.add(extractFromResultSet(rs));
            }
        }
        return customOrders;
    }

    public List<CustomOrder> getByCustomerId(int customerId) throws SQLException {
        String sql = """
            SELECT co."Custom_Order_ID", co."Customer_ID", co."Stone_Type", co."Stone_Description",
                   co."Size", co."Requested_Quantity", co."Status", co."Created_At",
                   u."First_Name" || ' ' || COALESCE(u."Last_Name", '') AS customer_name
            FROM "Custom_Orders" co
            JOIN "Customer" c ON co."Customer_ID" = c."Customer_ID"
            JOIN "User" u ON c."User_ID" = u."User_ID"
            WHERE co."Customer_ID" = ?
            ORDER BY co."Created_At" DESC
            """;
        
        List<CustomOrder> customOrders = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customOrders.add(extractFromResultSet(rs));
            }
        }
        return customOrders;
    }

    public CustomOrder getById(int customOrderId) throws SQLException {
        String sql = """
            SELECT co."Custom_Order_ID", co."Customer_ID", co."Stone_Type", co."Stone_Description",
                   co."Size", co."Requested_Quantity", co."Status", co."Created_At",
                   u."First_Name" || ' ' || COALESCE(u."Last_Name", '') AS customer_name
            FROM "Custom_Orders" co
            JOIN "Customer" c ON co."Customer_ID" = c."Customer_ID"
            JOIN "User" u ON c."User_ID" = u."User_ID"
            WHERE co."Custom_Order_ID" = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customOrderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractFromResultSet(rs);
            }
            return null;
        }
    }

    private CustomOrder extractFromResultSet(ResultSet rs) throws SQLException {
        return new CustomOrder(
            rs.getInt("Custom_Order_ID"),
            rs.getInt("Customer_ID"),
            rs.getString("customer_name"),
            rs.getString("Stone_Type"),
            rs.getString("Stone_Description"),
            rs.getString("Size"),
            rs.getInt("Requested_Quantity"),
            rs.getString("Status"),
            rs.getTimestamp("Created_At").toLocalDateTime()
        );
    }
}
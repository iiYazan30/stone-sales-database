package com.DB.databaseproject.dao;

import com.DB.databaseproject.model.OrderDetails;
import com.DB.databaseproject.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Order_Details table
 * Handles all database operations for order details
 */
public class OrderDetailsDAO {

    /**
     * Insert a new order detail
     */
    public boolean insert(OrderDetails orderDetail) throws SQLException {
        String sql = """
            INSERT INTO "Order_Details" 
            ("Order_ID", "Stone_ID", "Quantity", "Unit_Price") 
            VALUES (?, ?, ?, ?)
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 OrderDetailsDAO.insert() - Executing INSERT");
        System.out.println("═══════════════════════════════════════════════");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderDetail.getOrderId());
            pstmt.setInt(2, orderDetail.getStoneId());
            pstmt.setInt(3, orderDetail.getQuantity());
            pstmt.setDouble(4, orderDetail.getUnitPrice());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Rows affected: " + rowsAffected);
            System.out.println("═══════════════════════════════════════════════");
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ INSERT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Insert multiple order details (batch insert)
     * Uses PostgreSQL table structure with proper column names
     */
    public boolean insertBatch(List<OrderDetails> orderDetails) throws SQLException {
        String sql = """
            INSERT INTO "Order_Details" 
            ("Order_ID", "Stone_ID", "Quantity", "Unit_Price") 
            VALUES (?, ?, ?, ?)
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 OrderDetailsDAO.insertBatch() - Batch INSERT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Inserting " + orderDetails.size() + " order detail(s)");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (OrderDetails detail : orderDetails) {
                pstmt.setInt(1, detail.getOrderId());
                pstmt.setInt(2, detail.getStoneId());
                pstmt.setInt(3, detail.getQuantity());
                pstmt.setDouble(4, detail.getUnitPrice());
                pstmt.addBatch();
                
                double subtotal = detail.getQuantity() * detail.getUnitPrice();
                System.out.println("   Batch: Order_ID=" + detail.getOrderId() + 
                                 ", Stone_ID=" + detail.getStoneId() + 
                                 ", Quantity=" + detail.getQuantity() + 
                                 ", Unit_Price=$" + detail.getUnitPrice() +
                                 ", Subtotal(calc)=$" + String.format("%.2f", subtotal));
            }
            
            int[] results = pstmt.executeBatch();
            conn.commit();
            
            System.out.println("✅ Batch executed: " + results.length + " row(s) affected");
            
            // Check if all inserts were successful
            for (int result : results) {
                if (result <= 0) {
                    System.err.println("❌ Some batch inserts failed");
                    System.out.println("═══════════════════════════════════════════════");
                    return false;
                }
            }
            
            System.out.println("✅ All order details inserted successfully");
            System.out.println("═══════════════════════════════════════════════");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ BATCH INSERT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.out.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Update an existing order detail
     */
    public boolean update(OrderDetails orderDetail) throws SQLException {
        String sql = """
            UPDATE "Order_Details" 
            SET "Quantity" = ?, "Unit_Price" = ? 
            WHERE "Order_ID" = ? AND "Stone_ID" = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderDetail.getQuantity());
            pstmt.setDouble(2, orderDetail.getUnitPrice());
            pstmt.setInt(3, orderDetail.getOrderId());
            pstmt.setInt(4, orderDetail.getStoneId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete order details by order ID and stone ID
     */
    public boolean delete(int orderId, int stoneId) throws SQLException {
        String sql = """
            DELETE FROM "Order_Details" 
            WHERE "Order_ID" = ? AND "Stone_ID" = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, stoneId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete all order details for an order
     */
    public boolean deleteByOrderId(int orderId) throws SQLException {
        String sql = """
            DELETE FROM "Order_Details" 
            WHERE "Order_ID" = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Get order details by order ID
     */
    public List<OrderDetails> getByOrderId(int orderId) throws SQLException {
        String sql = """
            SELECT od.*, s."Name" AS stone_name 
            FROM "Order_Details" od 
            JOIN "Stone" s ON od."Stone_ID" = s."Stone_ID" 
            WHERE od."Order_ID" = ?
            """;
        List<OrderDetails> orderDetails = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orderDetails.add(extractOrderDetailsFromResultSet(rs));
            }
        }
        return orderDetails;
    }

    /**
     * Get order details by stone ID
     */
    public List<OrderDetails> getByStoneId(int stoneId) throws SQLException {
        String sql = """
            SELECT od.*, s."Name" AS stone_name 
            FROM "Order_Details" od 
            JOIN "Stone" s ON od."Stone_ID" = s."Stone_ID" 
            WHERE od."Stone_ID" = ?
            """;
        List<OrderDetails> orderDetails = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, stoneId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orderDetails.add(extractOrderDetailsFromResultSet(rs));
            }
        }
        return orderDetails;
    }

    /**
     * Get specific order detail
     */
    public OrderDetails get(int orderId, int stoneId) throws SQLException {
        String sql = "SELECT od.*, s.name AS stone_name " +
                     "FROM order_details od " +
                     "JOIN stone s ON od.stone_id = s.stone_id " +
                     "WHERE od.order_id = ? AND od.stone_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, stoneId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractOrderDetailsFromResultSet(rs);
            }
            return null;
        }
    }

    /**
     * Get all order details
     */
    public List<OrderDetails> getAll() throws SQLException {
        String sql = "SELECT od.*, s.name AS stone_name " +
                     "FROM order_details od " +
                     "JOIN stone s ON od.stone_id = s.stone_id " +
                     "ORDER BY od.order_id, od.stone_id";
        List<OrderDetails> orderDetails = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orderDetails.add(extractOrderDetailsFromResultSet(rs));
            }
        }
        return orderDetails;
    }

    /**
     * Extract OrderDetails object from ResultSet
     */
    private OrderDetails extractOrderDetailsFromResultSet(ResultSet rs) throws SQLException {
        return new OrderDetails(
            rs.getInt("order_id"),
            rs.getInt("stone_id"),
            rs.getString("stone_name"),
            rs.getInt("quantity"),
            rs.getDouble("unit_price")
        );
    }
}

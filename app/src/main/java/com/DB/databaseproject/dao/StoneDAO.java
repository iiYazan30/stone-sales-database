package com.DB.databaseproject.dao;

import com.DB.databaseproject.model.Stone;
import com.DB.databaseproject.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Stone table
 * Handles all database operations for stones
 * Uses multiline text blocks for SQL queries
 */
public class StoneDAO {

    /**
     * Insert a new stone
     */
    public int insert(Stone stone) throws SQLException {
        String sql = """
            INSERT INTO "Stone"
            ("Name", "Type", "Size", "Quantity_In_Stock", "Price_Per_Unit", "Image")
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.insert() - Executing INSERT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Parameters:");
        System.out.println("   Name: " + stone.getName());
        System.out.println("   Type: " + stone.getType());
        System.out.println("   Size: " + stone.getSize());
        System.out.println("   Quantity: " + stone.getQuantityInStock());
        System.out.println("   Price: " + stone.getPricePerUnit());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, stone.getName());
            pstmt.setString(2, stone.getType());
            pstmt.setString(3, stone.getSize());
            pstmt.setInt(4, stone.getQuantityInStock());
            pstmt.setDouble(5, stone.getPricePerUnit());
            pstmt.setString(6, stone.getImagePath());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int stoneId = rs.getInt(1);
                    System.out.println("✅ Stone created with ID: " + stoneId);
                    System.out.println("═══════════════════════════════════════════════");
                    return stoneId;
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
     * Update an existing stone
     */
    public boolean update(Stone stone) throws SQLException {
        String sql = """
            UPDATE "Stone"
            SET "Name" = ?, "Type" = ?, "Size" = ?,
                "Quantity_In_Stock" = ?, "Price_Per_Unit" = ?, "Image" = ?
            WHERE "Stone_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.update() - Executing UPDATE");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Stone_ID: " + stone.getStoneId());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, stone.getName());
            pstmt.setString(2, stone.getType());
            pstmt.setString(3, stone.getSize());
            pstmt.setInt(4, stone.getQuantityInStock());
            pstmt.setDouble(5, stone.getPricePerUnit());
            pstmt.setString(6, stone.getImagePath());
            pstmt.setInt(7, stone.getStoneId());
            
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
     * Check if stone is used in any order details
     * Checks Order_Details table for foreign key references
     */
    public int countOrdersUsingStone(int stoneId) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM "Order_Details"
            WHERE "Stone_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.countOrdersUsingStone()");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Stone_ID: " + stoneId);
        System.out.println("📋 Target Table: Order_Details");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, stoneId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("✅ Order_Details records using this stone: " + count);
                System.out.println("═══════════════════════════════════════════════");
                return count;
            }
            System.out.println("═══════════════════════════════════════════════");
            return 0;
        } catch (SQLException e) {
            System.err.println("❌ COUNT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            // If column doesn't exist (42703), return 0 instead of throwing
            if ("42703".equals(e.getSQLState())) {
                System.err.println("⚠️  Stone_ID column not found in Order_Details table - returning 0");
                System.err.println("═══════════════════════════════════════════════");
                return 0;
            }
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }
    
    /**
     * Unassign stone from all order details (set Stone_ID to NULL)
     * This must be called before deleting a stone to avoid foreign key constraint violations
     */
    public int unassignStoneFromOrders(int stoneId) throws SQLException {
        String sql = """
            UPDATE "Order_Details"
            SET "Stone_ID" = NULL
            WHERE "Stone_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.unassignStoneFromOrders()");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Stone_ID: " + stoneId);
        System.out.println("📋 Target Table: Order_Details");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, stoneId);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Order_Details records updated: " + rowsAffected);
            System.out.println("═══════════════════════════════════════════════");
            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("❌ UPDATE ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }
    
    /**
     * Delete a stone by ID (after unassigning from orders)
     */
    public boolean delete(int stoneId) throws SQLException {
        String sql = """
            DELETE FROM "Stone"
            WHERE "Stone_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.delete() - Executing DELETE");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Stone_ID: " + stoneId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, stoneId);
            
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
     * Get stone by ID
     */
    public Stone getById(int stoneId) throws SQLException {
        String sql = """
            SELECT * FROM "Stone"
            WHERE "Stone_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.getById() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Stone_ID: " + stoneId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, stoneId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Stone stone = extractStoneFromResultSet(rs);
                System.out.println("✅ Stone found: " + stone.getName());
                System.out.println("═══════════════════════════════════════════════");
                return stone;
            } else {
                System.out.println("❌ No stone found with ID: " + stoneId);
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
     * Get all stones
     */
    public List<Stone> getAll() throws SQLException {
        String sql = """
            SELECT * FROM "Stone"
            ORDER BY "Stone_ID"
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.getAll() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        
        List<Stone> stones = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                stones.add(extractStoneFromResultSet(rs));
            }
            
            System.out.println("✅ Retrieved " + stones.size() + " stones");
            System.out.println("═══════════════════════════════════════════════");
            return stones;
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Get stones in stock (quantity > 0)
     */
    public List<Stone> getStonesInStock() throws SQLException {
        String sql = """
            SELECT * FROM "Stone"
            WHERE "Quantity_In_Stock" > 0
            ORDER BY "Stone_ID"
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.getStonesInStock() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        
        List<Stone> stones = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                stones.add(extractStoneFromResultSet(rs));
            }
            
            System.out.println("✅ Retrieved " + stones.size() + " stones in stock");
            System.out.println("═══════════════════════════════════════════════");
            return stones;
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Search stones by name
     */
    public List<Stone> searchByName(String searchTerm) throws SQLException {
        String sql = """
            SELECT * FROM "Stone"
            WHERE LOWER("Name") LIKE LOWER(?)
            ORDER BY "Stone_ID"
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.searchByName() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Search term: " + searchTerm);
        
        List<Stone> stones = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                stones.add(extractStoneFromResultSet(rs));
            }
            
            System.out.println("✅ Found " + stones.size() + " stones");
            System.out.println("═══════════════════════════════════════════════");
            return stones;
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Update stone stock (quantity)
     */
    public boolean updateStock(int stoneId, int newQuantity) throws SQLException {
        String sql = """
            UPDATE "Stone"
            SET "Quantity_In_Stock" = ?
            WHERE "Stone_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.updateStock() - Executing UPDATE");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Stone_ID: " + stoneId);
        System.out.println("📝 New Quantity: " + newQuantity);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, stoneId);
            
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
     * Decrease stone stock (for orders)
     */
    public boolean decreaseStock(int stoneId, int quantity) throws SQLException {
        String sql = """
            UPDATE "Stone"
            SET "Quantity_In_Stock" = "Quantity_In_Stock" - ?
            WHERE "Stone_ID" = ? AND "Quantity_In_Stock" >= ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.decreaseStock() - Executing UPDATE");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Stone_ID: " + stoneId);
        System.out.println("📝 Decrease by: " + quantity);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, stoneId);
            pstmt.setInt(3, quantity);
            
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
     * Increase stone stock (for order cancellations)
     */
    public boolean increaseStock(int stoneId, int quantity) throws SQLException {
        String sql = """
            UPDATE "Stone"
            SET "Quantity_In_Stock" = "Quantity_In_Stock" + ?
            WHERE "Stone_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.increaseStock() - Executing UPDATE");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Stone_ID: " + stoneId);
        System.out.println("📝 Increase by: " + quantity);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, stoneId);
            
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
     * Get stone count
     */
    public int getCount() throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM "Stone"
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
     * Get all stones in stock (alias for getStonesInStock for compatibility)
     */
    public List<Stone> getAllInStock() throws SQLException {
        return getStonesInStock();
    }

    /**
     * Filter stones by type
     */
    public List<Stone> filterByType(String type) throws SQLException {
        String sql = """
            SELECT * FROM "Stone"
            WHERE LOWER("Type") = LOWER(?)
            ORDER BY "Stone_ID"
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 StoneDAO.filterByType() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Type: " + type);
        
        List<Stone> stones = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                stones.add(extractStoneFromResultSet(rs));
            }
            
            System.out.println("✅ Found " + stones.size() + " stones of type: " + type);
            System.out.println("═══════════════════════════════════════════════");
            return stones;
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Update stone quantity (alias for updateStock for compatibility)
     */
    public boolean updateQuantity(int stoneId, int newQuantity) throws SQLException {
        return updateStock(stoneId, newQuantity);
    }

    /**
     * Decrease stone quantity (alias for decreaseStock for compatibility)
     */
    public boolean decreaseQuantity(int stoneId, int quantity) throws SQLException {
        return decreaseStock(stoneId, quantity);
    }

    /**
     * Get total inventory value
     */
    public double getTotalInventoryValue() throws SQLException {
        String sql = """
            SELECT SUM("Quantity_In_Stock" * "Price_Per_Unit") FROM "Stone"
            """;
        
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
     * Extract Stone object from ResultSet
     */
    private Stone extractStoneFromResultSet(ResultSet rs) throws SQLException {
        return new Stone(
            rs.getInt("Stone_ID"),
            rs.getString("Name"),
            rs.getString("Type"),
            rs.getString("Size"),
            rs.getDouble("Price_Per_Unit"),
            rs.getInt("Quantity_In_Stock"),
            rs.getString("Image")
        );
    }
}

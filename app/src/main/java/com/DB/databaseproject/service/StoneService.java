package com.DB.databaseproject.service;

import com.DB.databaseproject.dao.StoneDAO;
import com.DB.databaseproject.model.Stone;

import java.sql.SQLException;
import java.util.List;

/**
 * Stone Service
 * Handles stone-related business logic
 */
public class StoneService {
    
    private final StoneDAO stoneDAO;
    
    // Singleton instance
    private static StoneService instance;
    
    private StoneService() {
        this.stoneDAO = new StoneDAO();
    }
    
    public static StoneService getInstance() {
        if (instance == null) {
            instance = new StoneService();
        }
        return instance;
    }

    /**
     * Add a new stone
     */
    public int addStone(Stone stone) {
        try {
            int stoneId = stoneDAO.insert(stone);
            if (stoneId > 0) {
                System.out.println("✅ Stone added successfully: " + stone.getName());
            }
            return stoneId;
        } catch (SQLException e) {
            System.err.println("❌ Stone addition error: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Update stone
     */
    public boolean updateStone(Stone stone) {
        try {
            boolean updated = stoneDAO.update(stone);
            if (updated) {
                System.out.println("✅ Stone updated successfully: " + stone.getName());
            }
            return updated;
        } catch (SQLException e) {
            System.err.println("❌ Stone update error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if stone is used in orders
     */
    public int getOrderCountForStone(int stoneId) {
        try {
            return stoneDAO.countOrdersUsingStone(stoneId);
        } catch (SQLException e) {
            System.err.println("❌ Error counting orders: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Delete stone (handles orders first)
     */
    public boolean deleteStone(int stoneId) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║         DELETE STONE - SERVICE LAYER                        ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println("🗑️  Delete Stone Request - Stone ID: " + stoneId);
            
            // Step 1: Check if stone is used in orders
            int orderCount = stoneDAO.countOrdersUsingStone(stoneId);
            System.out.println("📊 Orders using this stone: " + orderCount);
            
            // Step 2: If used in orders, unassign it first
            if (orderCount > 0) {
                System.out.println("⚠️  Stone is used in " + orderCount + " order(s)");
                System.out.println("🔄 Unassigning stone from orders...");
                int unassigned = stoneDAO.unassignStoneFromOrders(stoneId);
                System.out.println("✅ Unassigned from " + unassigned + " order(s)");
            }
            
            // Step 3: Delete the stone
            System.out.println("🗑️  Deleting stone from database...");
            boolean deleted = stoneDAO.delete(stoneId);
            
            if (deleted) {
                System.out.println("✅ ✅ ✅ STONE DELETED SUCCESSFULLY! ✅ ✅ ✅");
                System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            } else {
                System.err.println("❌ Stone deletion failed");
                System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            }
            
            return deleted;
        } catch (SQLException e) {
            System.err.println("❌ Stone deletion error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            return false;
        }
    }

    /**
     * Get all stones
     */
    public List<Stone> getAllStones() {
        try {
            return stoneDAO.getAll();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching stones: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get all stones in stock
     */
    public List<Stone> getStonesInStock() {
        try {
            return stoneDAO.getAllInStock();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching stones in stock: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Search stones by name
     */
    public List<Stone> searchStones(String searchTerm) {
        try {
            return stoneDAO.searchByName(searchTerm);
        } catch (SQLException e) {
            System.err.println("❌ Error searching stones: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Filter stones by type
     */
    public List<Stone> filterStonesByType(String type) {
        try {
            return stoneDAO.filterByType(type);
        } catch (SQLException e) {
            System.err.println("❌ Error filtering stones: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get stone by ID
     */
    public Stone getStoneById(int stoneId) {
        try {
            return stoneDAO.getById(stoneId);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching stone: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update stone quantity
     */
    public boolean updateStoneQuantity(int stoneId, int newQuantity) {
        try {
            boolean updated = stoneDAO.updateQuantity(stoneId, newQuantity);
            if (updated) {
                System.out.println("✅ Stone quantity updated: Stone ID " + stoneId);
            }
            return updated;
        } catch (SQLException e) {
            System.err.println("❌ Stone quantity update error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get stone count
     */
    public int getStoneCount() {
        try {
            return stoneDAO.getCount();
        } catch (SQLException e) {
            System.err.println("❌ Error getting stone count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get total inventory value
     */
    public double getTotalInventoryValue() {
        try {
            return stoneDAO.getTotalInventoryValue();
        } catch (SQLException e) {
            System.err.println("❌ Error getting inventory value: " + e.getMessage());
            return 0.0;
        }
    }
}

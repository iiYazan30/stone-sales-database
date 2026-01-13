package com.DB.databaseproject.service;

import com.DB.databaseproject.dao.CustomOrderDAO;
import com.DB.databaseproject.dao.OrderDAO;
import com.DB.databaseproject.model.CustomOrder;
import com.DB.databaseproject.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomOrderService {
    
    private static CustomOrderService instance;
    private final CustomOrderDAO customOrderDAO;
    private final OrderDAO orderDAO;
    
    private CustomOrderService() {
        this.customOrderDAO = new CustomOrderDAO();
        this.orderDAO = new OrderDAO();
    }
    
    public static CustomOrderService getInstance() {
        if (instance == null) {
            instance = new CustomOrderService();
        }
        return instance;
    }
    
    public boolean submitCustomOrder(CustomOrder customOrder, int customerId) {
        try {
            int customOrderId = customOrderDAO.insert(customOrder, customerId);
            return customOrderId > 0;
        } catch (SQLException e) {
            System.err.println("Error submitting custom order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean approveCustomOrder(int customOrderId) {
        try {
            return customOrderDAO.updateStatus(customOrderId, "Approved");
        } catch (SQLException e) {
            System.err.println("Error approving custom order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Approve custom order and convert it to a real Order
     * Creates new Order record and links it to the custom order
     * Uses transaction to ensure data consistency
     */
    public boolean approveAndConvertCustomOrder(CustomOrder customOrder) throws SQLException {
        Connection conn = null;
        try {
            // Get connection and start transaction
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            System.out.println("═══════════════════════════════════════════════");
            System.out.println("🔄 Converting Custom Order to Real Order");
            System.out.println("═══════════════════════════════════════════════");
            System.out.println("Custom Order ID: " + customOrder.getCustomOrderId());
            System.out.println("Customer ID: " + customOrder.getCustomerId());
            
            // Step 1: Create new Order using the transaction connection
            int newOrderId = orderDAO.createOrderFromCustomOrder(conn, customOrder.getCustomerId());
            
            if (newOrderId <= 0) {
                System.err.println("❌ Failed to create new order");
                conn.rollback();
                return false;
            }
            
            System.out.println("✅ Created new Order with ID: " + newOrderId);
            
            // Step 2: Update Custom Order status and link to Order using the transaction connection
            boolean updated = customOrderDAO.updateStatusAndOrderId(
                conn,
                customOrder.getCustomOrderId(), 
                "Converted", 
                newOrderId
            );
            
            if (!updated) {
                System.err.println("❌ Failed to update custom order");
                conn.rollback();
                return false;
            }
            
            System.out.println("✅ Updated Custom Order - Status: Converted, Order_ID: " + newOrderId);
            
            // Commit transaction
            conn.commit();
            System.out.println("✅ Transaction committed successfully");
            System.out.println("═══════════════════════════════════════════════");
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ ERROR during conversion:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("⚠️ Transaction rolled back");
                } catch (SQLException rollbackEx) {
                    System.err.println("❌ Error rolling back transaction: " + rollbackEx.getMessage());
                }
            }
            throw e;
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    public boolean rejectCustomOrder(int customOrderId) {
        try {
            return customOrderDAO.updateStatus(customOrderId, "Rejected");
        } catch (SQLException e) {
            System.err.println("Error rejecting custom order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<CustomOrder> getAllCustomOrders() {
        try {
            return customOrderDAO.getAll();
        } catch (SQLException e) {
            System.err.println("Error fetching all custom orders: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<CustomOrder> getCustomOrdersByCustomer(int customerId) {
        try {
            return customOrderDAO.getByCustomerId(customerId);
        } catch (SQLException e) {
            System.err.println("Error fetching customer orders: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public CustomOrder getCustomOrderById(int customOrderId) {
        try {
            return customOrderDAO.getById(customOrderId);
        } catch (SQLException e) {
            System.err.println("Error fetching custom order: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
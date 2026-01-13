package com.DB.databaseproject.service;

import com.DB.databaseproject.dao.OrderDAO;
import com.DB.databaseproject.dao.OrderDetailsDAO;
import com.DB.databaseproject.dao.StoneDAO;
import com.DB.databaseproject.model.Order;
import com.DB.databaseproject.model.OrderDetails;
import com.DB.databaseproject.model.Stone;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Order Service
 * Handles order-related business logic
 */
public class OrderService {
    
    private final OrderDAO orderDAO;
    private final OrderDetailsDAO orderDetailsDAO;
    private final StoneDAO stoneDAO;
    
    // Singleton instance
    private static OrderService instance;
    
    private OrderService() {
        this.orderDAO = new OrderDAO();
        this.orderDetailsDAO = new OrderDetailsDAO();
        this.stoneDAO = new StoneDAO();
    }
    
    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    /**
     * Create a new order with order details (admin/employee orders)
     * @param customerId Customer ID
     * @param orderDetailsList List of order details (stones and quantities)
     * @return Order ID if successful, -1 otherwise
     */
    public int createOrder(int customerId, List<OrderDetails> orderDetailsList) {
        try {
            // Calculate total amount
            double totalAmount = 0.0;
            for (OrderDetails detail : orderDetailsList) {
                totalAmount += detail.getSubtotal();
            }
            
            // Create order
            Order order = new Order();
            order.setOrderDate(LocalDate.now());
            order.setTotalAmount(totalAmount);
            order.setStatus("Pending");
            
            int orderId = orderDAO.insert(order, customerId, null);
            
            if (orderId > 0) {
                // Set order ID for all details
                for (OrderDetails detail : orderDetailsList) {
                    detail.setOrderId(orderId);
                }
                
                // Insert order details
                boolean detailsInserted = orderDetailsDAO.insertBatch(orderDetailsList);
                
                if (detailsInserted) {
                    // Update stone quantities
                    for (OrderDetails detail : orderDetailsList) {
                        boolean updated = stoneDAO.decreaseQuantity(detail.getStoneId(), detail.getQuantity());
                        if (!updated) {
                            System.err.println("⚠️ Warning: Failed to update stock for stone ID: " + detail.getStoneId());
                        }
                    }
                    
                    System.out.println("✅ Order created successfully: Order ID " + orderId);
                    return orderId;
                }
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("❌ Order creation error: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Create a customer self-service order (Buy Now from shop)
     * REQUIREMENT: Status='Pending', Employee_ID=NULL, Stock reduced immediately
     * @param customerId Customer ID
     * @param orderDetailsList List of order details (stones and quantities)
     * @return Order ID if successful, -1 otherwise
     */
    public int createCustomerOrder(int customerId, List<OrderDetails> orderDetailsList) {
        try {
            System.out.println("\n═══════════════════════════════════════════════");
            System.out.println("🛒 Creating Customer Self-Service Order");
            System.out.println("═══════════════════════════════════════════════");
            System.out.println("Customer ID: " + customerId);
            
            // Calculate total amount
            double totalAmount = 0.0;
            for (OrderDetails detail : orderDetailsList) {
                totalAmount += detail.getSubtotal();
                System.out.println("Stone ID: " + detail.getStoneId() + " | Qty: " + detail.getQuantity() + 
                                 " | Price: $" + detail.getUnitPrice() + " | Subtotal: $" + detail.getSubtotal());
            }
            System.out.println("Total Amount: $" + String.format("%.2f", totalAmount));
            
            // REQUIREMENT: Create order with Status='Pending' and Employee_ID=NULL
            Order order = new Order();
            order.setOrderDate(LocalDate.now());
            order.setTotalAmount(totalAmount);
            order.setStatus("Pending"); // Customer orders start as Pending
            
            // REQUIREMENT: Insert into Orders with Employee_ID = NULL (unassigned)
            int orderId = orderDAO.insert(order, customerId, null);
            System.out.println("Order inserted with ID: " + orderId + " | Status: Pending | Employee_ID: NULL (Unassigned)");
            
            if (orderId > 0) {
                // Set order ID for all details
                for (OrderDetails detail : orderDetailsList) {
                    detail.setOrderId(orderId);
                }
                
                // REQUIREMENT: Insert into Order_Details
                boolean detailsInserted = orderDetailsDAO.insertBatch(orderDetailsList);
                System.out.println("Order details inserted: " + detailsInserted);
                
                if (detailsInserted) {
                    // REQUIREMENT: Update Stone table - decrease QuantityInStock
                    for (OrderDetails detail : orderDetailsList) {
                        boolean updated = stoneDAO.decreaseQuantity(detail.getStoneId(), detail.getQuantity());
                        if (updated) {
                            System.out.println("✅ Stock updated for Stone ID " + detail.getStoneId() + 
                                             " | Decreased by: " + detail.getQuantity());
                        } else {
                            System.err.println("⚠️ Warning: Failed to update stock for stone ID: " + detail.getStoneId());
                        }
                    }
                    
                    System.out.println("✅ Customer order created successfully: Order ID " + orderId);
                    System.out.println("═══════════════════════════════════════════════\n");
                    return orderId;
                }
            }
            
            System.err.println("❌ Failed to create customer order");
            System.out.println("═══════════════════════════════════════════════\n");
            return -1;
        } catch (SQLException e) {
            System.err.println("❌ Customer order creation error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("═══════════════════════════════════════════════\n");
            return -1;
        }
    }

    /**
     * Update order status with validation
     * BUSINESS RULES:
     * - Valid transitions: Pending → Processing → Completed
     * - Once Completed, order becomes read-only (no further changes)
     * - Send email notification when order becomes Completed
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        try {
            // Get current order to check existing status
            Order currentOrder = orderDAO.getById(orderId);
            if (currentOrder == null) {
                System.err.println("❌ Order not found: " + orderId);
                return false;
            }
            
            String currentStatus = currentOrder.getStatus();
            
            // BUSINESS RULE: Once Completed, no status changes allowed
            if ("Completed".equalsIgnoreCase(currentStatus)) {
                System.err.println("❌ Cannot update status: Order " + orderId + " is already Completed (read-only)");
                return false;
            }
            
            // Validate status transition
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                System.err.println("❌ Invalid status transition: " + currentStatus + " → " + newStatus);
                System.err.println("   Valid transitions: Pending → Processing → Completed");
                return false;
            }
            
            boolean updated = orderDAO.updateStatus(orderId, newStatus);
            if (updated) {
                System.out.println("✅ Order status updated: Order ID " + orderId + " | " + currentStatus + " → " + newStatus);
                
                // Send email notification if order is now Completed
                if ("Completed".equalsIgnoreCase(newStatus)) {
                    sendOrderCompletedEmail(orderId);
                }
            }
            return updated;
        } catch (SQLException e) {
            System.err.println("❌ Order status update error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Send order completed email notification to customer
     * Called when order status changes to Completed
     * Runs asynchronously - does not block
     */
    private void sendOrderCompletedEmail(int orderId) {
        try {
            // Get customer email and first name
            String[] customerInfo = orderDAO.getCustomerEmailByOrderId(orderId);
            
            if (customerInfo == null) {
                System.out.println("⚠️ No customer email found for Order ID: " + orderId);
                return;
            }
            
            String customerEmail = customerInfo[0];
            String customerFirstName = customerInfo[1];
            
            // Send email notification (async - non-blocking)
            EmailService emailService = EmailService.getInstance();
            emailService.sendOrderCompletedNotification(customerEmail, customerFirstName, orderId);
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching customer info for email notification: " + e.getMessage());
            // Don't fail the order update if email fails
        }
    }
    
    /**
     * Validate status transition
     * ALLOWED TRANSITIONS:
     * - Pending → Processing
     * - Pending → Completed
     * - Processing → Completed
     * - Any status → Cancelled (admin override)
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equalsIgnoreCase(newStatus)) {
            return false; // No change
        }
        
        currentStatus = currentStatus.toLowerCase();
        newStatus = newStatus.toLowerCase();
        
        // Allow cancellation from any status
        if ("cancelled".equals(newStatus)) {
            return true;
        }
        
        // Valid forward transitions
        switch (currentStatus) {
            case "pending":
                return "processing".equals(newStatus) || "completed".equals(newStatus);
            case "processing":
                return "completed".equals(newStatus);
            case "completed":
                return false; // No transitions from completed
            case "cancelled":
                return false; // No transitions from cancelled
            default:
                return false;
        }
    }

    /**
     * Assign employee to order
     * BUSINESS RULE: Cannot reassign employee if order is Completed (read-only)
     */
    public boolean assignEmployeeToOrder(int orderId, int employeeId) {
        try {
            // Get current order to check status
            Order currentOrder = orderDAO.getById(orderId);
            if (currentOrder == null) {
                System.err.println("❌ Order not found: " + orderId);
                return false;
            }
            
            // BUSINESS RULE: Cannot assign/reassign employee if order is Completed
            if ("Completed".equalsIgnoreCase(currentOrder.getStatus())) {
                System.err.println("❌ Cannot assign employee: Order " + orderId + " is Completed (read-only)");
                return false;
            }
            
            boolean assigned = orderDAO.assignEmployee(orderId, employeeId);
            if (assigned) {
                System.out.println("✅ Employee assigned to order: Order ID " + orderId + " | Employee ID: " + employeeId);
            }
            return assigned;
        } catch (SQLException e) {
            System.err.println("❌ Employee assignment error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all employees for assignment dropdown
     */
    public List<com.DB.databaseproject.model.Employee> getAllEmployees() {
        try {
            return orderDAO.getAllEmployees();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching employees: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get all orders
     */
    public List<Order> getAllOrders() {
        try {
            return orderDAO.getAll();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching orders: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get orders by customer ID
     */
    public List<Order> getOrdersByCustomer(int customerId) {
        try {
            return orderDAO.getByCustomerId(customerId);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching customer orders: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get orders by employee ID
     */
    public List<Order> getOrdersByEmployee(int employeeId) {
        try {
            return orderDAO.getByEmployeeId(employeeId);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching employee orders: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(String status) {
        try {
            return orderDAO.getByStatus(status);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching orders by status: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get active orders (Pending, Assigned, Processing)
     * Excludes completed and canceled orders
     */
    public List<Order> getActiveOrders() {
        try {
            return orderDAO.getActiveOrders();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching active orders: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get archived orders (Completed, Canceled)
     * READ-ONLY orders that are no longer active
     */
    public List<Order> getArchivedOrders() {
        try {
            return orderDAO.getArchivedOrders();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching archived orders: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get order details for a specific order
     */
    public List<OrderDetails> getOrderDetails(int orderId) {
        try {
            return orderDetailsDAO.getByOrderId(orderId);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching order details: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Cancel an order (Customer can only cancel Pending orders)
     * BUSINESS RULES:
     * - Only orders with status 'Pending' can be canceled
     * - Order status will be updated to 'Canceled'
     * - Stock quantities will be restored
     * - Order record is NOT deleted (history preserved)
     * 
     * @param orderId Order ID to cancel
     * @param customerId Customer ID (for validation)
     * @return true if canceled successfully, false otherwise
     */
    public boolean cancelOrder(int orderId, int customerId) {
        try {
            System.out.println("\n═══════════════════════════════════════════════");
            System.out.println("🚫 Canceling Order");
            System.out.println("═══════════════════════════════════════════════");
            System.out.println("Order ID: " + orderId);
            System.out.println("Customer ID: " + customerId);
            
            // Get current order to validate
            Order currentOrder = orderDAO.getById(orderId);
            if (currentOrder == null) {
                System.err.println("❌ Order not found: " + orderId);
                System.out.println("═══════════════════════════════════════════════\n");
                return false;
            }
            
            // Validate customer owns this order
            if (currentOrder.getCustomerId() != customerId) {
                System.err.println("❌ Unauthorized: Order " + orderId + " does not belong to customer " + customerId);
                System.out.println("═══════════════════════════════════════════════\n");
                return false;
            }
            
            String currentStatus = currentOrder.getStatus();
            System.out.println("Current Status: " + currentStatus);
            
            // BUSINESS RULE: Only Pending orders can be canceled
            if (!"Pending".equalsIgnoreCase(currentStatus)) {
                System.err.println("❌ Cannot cancel: Order status is '" + currentStatus + "' (only Pending orders can be canceled)");
                System.out.println("═══════════════════════════════════════════════\n");
                return false;
            }
            
            // Update order status to Canceled
            boolean statusUpdated = orderDAO.updateStatus(orderId, "Canceled");
            if (!statusUpdated) {
                System.err.println("❌ Failed to update order status");
                System.out.println("═══════════════════════════════════════════════\n");
                return false;
            }
            System.out.println("✅ Order status updated to: Canceled");
            
            // Restore stock quantities
            List<OrderDetails> orderDetails = orderDetailsDAO.getByOrderId(orderId);
            System.out.println("Restoring stock for " + orderDetails.size() + " items...");
            
            for (OrderDetails detail : orderDetails) {
                boolean restored = stoneDAO.increaseStock(detail.getStoneId(), detail.getQuantity());
                if (restored) {
                    System.out.println("✅ Stock restored for Stone ID " + detail.getStoneId() + 
                                     " | Increased by: " + detail.getQuantity());
                } else {
                    System.err.println("⚠️ Warning: Failed to restore stock for stone ID: " + detail.getStoneId());
                }
            }
            
            System.out.println("✅ Order canceled successfully: Order ID " + orderId);
            System.out.println("═══════════════════════════════════════════════\n");
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Order cancellation error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("═══════════════════════════════════════════════\n");
            return false;
        }
    }

    /**
     * Delete an order
     */
    public boolean deleteOrder(int orderId) {
        try {
            // First delete order details
            orderDetailsDAO.deleteByOrderId(orderId);
            
            // Then delete order
            boolean deleted = orderDAO.delete(orderId);
            if (deleted) {
                System.out.println("✅ Order deleted: Order ID " + orderId);
            }
            return deleted;
        } catch (SQLException e) {
            System.err.println("❌ Order deletion error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get order count
     */
    public int getOrderCount() {
        try {
            return orderDAO.getCount();
        } catch (SQLException e) {
            System.err.println("❌ Error getting order count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get pending orders count
     */
    public int getPendingOrderCount() {
        try {
            return orderDAO.getPendingCount();
        } catch (SQLException e) {
            System.err.println("❌ Error getting pending order count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get total revenue
     */
    public double getTotalRevenue() {
        try {
            return orderDAO.getTotalRevenue();
        } catch (SQLException e) {
            System.err.println("❌ Error getting total revenue: " + e.getMessage());
            return 0.0;
        }
    }
}

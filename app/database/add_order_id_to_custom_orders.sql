-- Migration: Add Order_ID column to Custom_Orders table
-- This links converted custom orders to their corresponding Orders

-- Add Order_ID column (nullable, since not all custom orders are converted)
ALTER TABLE "Custom_Orders" 
ADD COLUMN "Order_ID" INTEGER;

-- Add foreign key constraint to Orders table
ALTER TABLE "Custom_Orders"
ADD CONSTRAINT "CustomOrders_Order_FK"
FOREIGN KEY ("Order_ID")
REFERENCES "Orders"("Order_ID")
ON DELETE SET NULL;

-- Create index for faster lookups
CREATE INDEX "idx_custom_orders_order_id" ON "Custom_Orders"("Order_ID");

-- Update Status column comment to include new 'Converted' status
COMMENT ON COLUMN "Custom_Orders"."Status" IS 'Values: Pending, Approved, Rejected, Converted';
COMMENT ON COLUMN "Custom_Orders"."Order_ID" IS 'Links to Orders table when custom order is converted to a real order';

-- Display success message
SELECT 'Migration completed: Order_ID column added to Custom_Orders table' AS status;

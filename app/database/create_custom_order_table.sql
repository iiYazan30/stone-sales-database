-- Create Custom_Orders table for Stone Sales Management System
-- This table stores custom stone order requests from customers

CREATE TABLE "Custom_Orders" (
    "Custom_Order_ID" SERIAL PRIMARY KEY,
    "Customer_ID" INTEGER NOT NULL,

    "Stone_Name" VARCHAR(50),
    "Stone_Type" VARCHAR(30),
    "Stone_Description" TEXT,
    "Size" VARCHAR(20),
    "Requested_Quantity" INTEGER NOT NULL,

    "Notes" TEXT,
    "Status" VARCHAR(20) DEFAULT 'Pending',
    "Created_At" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "CustomOrders_Customer_FK"
    FOREIGN KEY ("Customer_ID")
    REFERENCES "Customer"("Customer_ID")
    ON DELETE CASCADE
);

-- Create index for faster queries
CREATE INDEX "idx_custom_orders_customer" ON "Custom_Orders"("Customer_ID");
CREATE INDEX "idx_custom_orders_status" ON "Custom_Orders"("Status");
CREATE INDEX "idx_custom_orders_created" ON "Custom_Orders"("Created_At" DESC);

-- Add comments
COMMENT ON TABLE "Custom_Orders" IS 'Stores custom stone order requests from customers';
COMMENT ON COLUMN "Custom_Orders"."Status" IS 'Values: Pending, Approved, Rejected';
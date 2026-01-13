-- ============================================
-- 1) Enum Type for User Roles
-- ============================================
CREATE TYPE user_role AS ENUM ('Admin','Employee','Customer');

-- ============================================
-- 2) User Table
-- ============================================
CREATE TABLE "User" (
    "User_ID" SERIAL PRIMARY KEY,
    "User_Name" VARCHAR(50) NOT NULL UNIQUE,
    "Password" VARCHAR(255) NOT NULL,
    "Phone_Number" VARCHAR(10) NOT NULL,
    "Email" VARCHAR(100),
    "First_Name" VARCHAR(15),
    "Middle_Name" VARCHAR(15),
    "Last_Name" VARCHAR(15),
    "Role" user_role NOT NULL,
    "Address" VARCHAR(70),

    CHECK ("Phone_Number" ~ '^[0-9]{10}$')
);

-- ============================================
-- 3) Employee Table
-- ============================================
CREATE TABLE "Employee" (
    "Employee_ID" SERIAL PRIMARY KEY,
    "User_ID" INT NOT NULL,

    "Salary" NUMERIC(10,2),
    "Phone_Number" VARCHAR(10) NOT NULL,
    "Address" VARCHAR(70),
    "Date_Hired" DATE,

    FOREIGN KEY ("User_ID") REFERENCES "User"("User_ID"),
    CHECK ("Phone_Number" ~ '^[0-9]{10}$')
);

-- ============================================
-- 4) Customer Table
-- ============================================
CREATE TABLE "Customer" (
    "Customer_ID" SERIAL PRIMARY KEY,
    "User_ID" INT NOT NULL,

    "Phone_Number" VARCHAR(10) NOT NULL,
    "Address" VARCHAR(70),

    FOREIGN KEY ("User_ID") REFERENCES "User"("User_ID"),
    CHECK ("Phone_Number" ~ '^[0-9]{10}$')
);

-- ============================================
-- 5) Stone Table
-- ============================================
CREATE TABLE "Stone" (
    "Stone_ID" SERIAL PRIMARY KEY,

    "Name" VARCHAR(30),
    "Type" VARCHAR(15),
    "Size" VARCHAR(20),
    "Quantity_In_Stock" INT,
    "Price_Per_Unit" NUMERIC(10,2),
    "Image" VARCHAR(255),
    "Description" VARCHAR(255),

    CHECK ("Quantity_In_Stock" >= 0),
    CHECK ("Price_Per_Unit" >= 0)
);

-- ============================================
-- 6) Orders Table
-- ============================================
CREATE TABLE "Orders" (
    "Order_ID" SERIAL PRIMARY KEY,
    "Customer_ID" INT NOT NULL,
    "Employee_ID" INT NOT NULL,

    "Order_Status" VARCHAR(15),
    "Payment" NUMERIC(10,2),
    "Order_Date" DATE,
    "Total_Amount" NUMERIC(10,2),

    FOREIGN KEY ("Customer_ID") REFERENCES "Customer"("Customer_ID"),
    FOREIGN KEY ("Employee_ID") REFERENCES "Employee"("Employee_ID")
);

-- ============================================
-- 7) Order Details Table
-- ============================================
CREATE TABLE "Order_Details" (
    "Order_Detail_ID" SERIAL PRIMARY KEY,
    "Order_ID" INT NOT NULL,
    "Stone_ID" INT NOT NULL,
    "Quantity" INT NOT NULL,
    "Unit_Price" NUMERIC(10,2),

    FOREIGN KEY ("Order_ID") REFERENCES "Orders"("Order_ID"),
    FOREIGN KEY ("Stone_ID") REFERENCES "Stone"("Stone_ID")
);

-- ============================================
-- 8) INSERT Users
-- ============================================
INSERT INTO "User" 
("User_Name", "Password", "Phone_Number", "Email", "First_Name", "Middle_Name", "Last_Name", "Role", "Address")
VALUES
('admin1', 'admin123', '0591111111', 'admin@mail.com', 'Ahmad', 'Ali', 'Salem', 'Admin', 'Ramallah'),
('sara_k', 'saraPass1', '0592222222', 'sara@mail.com', 'Sara', 'Kamal', 'Hassan', 'Customer', 'Nablus'),
('mohd92', 'mohd2024', '0593333333', 'mohd@mail.com', 'Mohammad', 'Omar', 'Qassem', 'Employee', 'Hebron'),
('leen55', 'leenPass11', '0594444444', 'leen@mail.com', 'Leen', 'Amin', 'Jaber', 'Customer', 'Gaza'),
('yousefM', 'yous12345', '0595555555', 'yousef@mail.com', 'Yousef', 'Majed', 'Awad', 'Employee', 'Jerusalem');

-- ============================================
-- 9) INSERT Employees
-- ============================================
INSERT INTO "Employee"
("User_ID", "Salary", "Phone_Number", "Address", "Date_Hired")
VALUES
(1, 2500.00, '0591111111', 'Ramallah', '2023-02-10'),
(3, 3200.00, '0593333333', 'Hebron', '2022-11-01'),
(5, 2800.00, '0595555555', 'Jerusalem', '2024-01-15');

-- ============================================
-- 10) INSERT Customers
-- ============================================
INSERT INTO "Customer"
("User_ID", "Phone_Number", "Address")
VALUES
(2, '0592222222', 'Nablus'),
(4, '0594444444', 'Gaza');

-- ============================================
-- 11) INSERT Stones
-- ============================================
INSERT INTO "Stone"
("Name", "Type", "Size", "Quantity_In_Stock", "Price_Per_Unit", "Image", "Description")
VALUES
('Black Marble', 'Marble', '30x30', 40, 25.50, 'black.jpg', 'High-quality black marble'),
('White Granite', 'Granite', '40x40', 30, 32.00, 'white.jpg', 'Durable white granite'),
('Sandstone', 'Sandstone', '50x50', 50, 15.00, 'sandstone.jpg', 'Smooth sandstone'),
('Basalt Stone', 'Basalt', '25x25', 20, 28.75, 'basalt.jpg', 'Strong basalt stone'),
('Limestone', 'Limestone', '30x60', 60, 18.20, 'lime.jpg', 'Light limestone');

-- ============================================
-- 12) INSERT Orders
-- ============================================
INSERT INTO "Orders"
("Customer_ID", "Employee_ID", "Order_Status", "Payment", "Order_Date", "Total_Amount")
VALUES
(1, 1, 'Completed', 250.00, '2024-02-10', 250.00),
(2, 2, 'Pending', 180.00, '2024-03-01', 180.00),
(1, 3, 'Completed', 320.00, '2024-01-22', 320.00);

-- ============================================
-- 13) INSERT Order Details
-- ============================================
INSERT INTO "Order_Details"
("Order_ID", "Stone_ID", "Quantity", "Unit_Price")
VALUES
(1, 1, 2, 25.50),
(1, 3, 1, 15.00),

(2, 2, 3, 32.00),
(2, 5, 2, 18.20),

(3, 1, 4, 25.50),
(3, 4, 2, 28.75);


















ALTER TABLE "Customer"
ADD COLUMN "First_Name" varchar(15);

ALTER TABLE "Customer"
ADD COLUMN "Middle_Name" varchar(15);

ALTER TABLE "Customer"
ADD COLUMN "Last_Name" varchar(15);




ALTER TABLE "Employee"
ADD COLUMN "First_Name" varchar(15),
ADD COLUMN "Middle_Name" varchar(15),
ADD COLUMN "Last_Name" varchar(15);




UPDATE "Employee" SET 
"First_Name" = 'Ahmad',
"Middle_Name" = 'Ali',
"Last_Name" = 'Salem'
WHERE "Employee_ID" = 1;

UPDATE "Employee" SET 
"First_Name" = 'Mohammad',
"Middle_Name" = 'Omar',
"Last_Name" = 'Qassem'
WHERE "Employee_ID" = 2;

UPDATE "Employee" SET 
"First_Name" = 'Yousef',
"Middle_Name" = 'Majed',
"Last_Name" = 'Awad'
WHERE "Employee_ID" = 3;













ALTER TABLE "Orders"
DROP CONSTRAINT "Orders_Employee_ID_fkey";

ALTER TABLE "Orders"
ADD CONSTRAINT "Orders_Employee_ID_fkey"
FOREIGN KEY ("Employee_ID")
REFERENCES "Employee"("Employee_ID")
ON DELETE SET NULL;



ALTER TABLE "Orders"
ALTER COLUMN "Employee_ID" DROP NOT NULL;




ALTER TABLE "Stone"
DROP COLUMN "Description";




ALTER TABLE "Order_Details"
ALTER COLUMN "Stone_ID" DROP NOT NULL;




CREATE TABLE "Custom_Order" (
    "Custom_Order_ID" SERIAL PRIMARY KEY,
    "Customer_ID" INTEGER NOT NULL,
    "Stone_Name" VARCHAR(50),
    "Stone_Type" VARCHAR(30),
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

ALTER TABLE "Custom_Orders"
ADD COLUMN "Stone_Description" TEXT;








-- ============================================
-- CLEAR ALL DATA (RESET DATABASE CONTENT)
-- ============================================

TRUNCATE TABLE
    "Order_Details",
    "Orders",
    "Employee",
    "Customer",
    "Stone",
    "User"
RESTART IDENTITY
CASCADE;

-- ============================================
-- INSERT USERS
-- ============================================

INSERT INTO "User"
("User_Name", "Password", "Phone_Number", "Email", "First_Name", "Middle_Name", "Last_Name", "Role", "Address")
VALUES
('admin1', 'admin123', '0591111111', 'admin@mail.com', 'Ahmad', 'Ali', 'Salem', 'Admin', 'Ramallah'),
('sara_k', 'saraPass1', '0592222222', 'sara@mail.com', 'Sara', 'Kamal', 'Hassan', 'Customer', 'Nablus'),
('mohd92', 'mohd2024', '0593333333', 'mohd@mail.com', 'Mohammad', 'Omar', 'Qassem', 'Employee', 'Hebron'),
('leen55', 'leenPass11', '0594444444', 'programmer3539@gmail.com', 'Leen', 'Amin', 'Jaber', 'Customer', 'Gaza'),
('yousefM', 'yous12345', '0595555555', 'yousef@mail.com', 'Yousef', 'Majed', 'Awad', 'Employee', 'Jerusalem');

-- ============================================
-- INSERT EMPLOYEES
-- ============================================

INSERT INTO "Employee"
("User_ID", "Salary", "Phone_Number", "Address", "Date_Hired",
 "First_Name", "Middle_Name", "Last_Name")
VALUES
(1, 2500.00, '0591111111', 'Ramallah', '2023-02-10', 'Ahmad', 'Ali', 'Salem'),
(3, 3200.00, '0593333333', 'Hebron', '2022-11-01', 'Mohammad', 'Omar', 'Qassem'),
(5, 2800.00, '0595555555', 'Jerusalem', '2024-01-15', 'Yousef', 'Majed', 'Awad');

-- ============================================
-- INSERT CUSTOMERS
-- ============================================

INSERT INTO "Customer"
("User_ID", "Phone_Number", "Address",
 "First_Name", "Middle_Name", "Last_Name")
VALUES
(2, '0592222222', 'Nablus', 'Sara', 'Kamal', 'Hassan'),
(4, '0594444444', 'Gaza', 'Leen', 'Amin', 'Jaber');

-- ============================================
-- INSERT STONES
-- ============================================

INSERT INTO "Stone"
("Name", "Type", "Size", "Quantity_In_Stock", "Price_Per_Unit", "Image")
VALUES
('Black Marble', 'Marble', '30x30', 40, 25.50, 'C:\Users\moham\eclipse-workspace\blackMarble.jpg'),
('White Granite', 'Granite', '40x40', 30, 32.00, 'C:\Users\moham\eclipse-workspace\whiteGranite.jpg'),
('Sandstone', 'Sandstone', '50x50', 50, 15.00, 'C:\Users\moham\eclipse-workspace\sandstone.jpg'),
('Basalt Stone', 'Basalt', '25x25', 20, 28.75, 'C:\Users\moham\eclipse-workspace\basaltStone.jpeg'),
('Limestone', 'Limestone', '30x60', 60, 18.20, 'C:\Users\moham\eclipse-workspace\limeStone.jpg');

-- ============================================
-- INSERT ORDERS
-- ============================================

INSERT INTO "Orders"
("Customer_ID", "Employee_ID", "Order_Status", "Payment", "Order_Date", "Total_Amount")
VALUES
(1, 1, 'Completed', 250.00, '2024-02-10', 250.00),
(2, 2, 'Pending', 180.00, '2024-03-01', 180.00),
(1, 3, 'Completed', 320.00, '2024-01-22', 320.00);

-- ============================================
-- INSERT ORDER DETAILS
-- ============================================

INSERT INTO "Order_Details"
("Order_ID", "Stone_ID", "Quantity", "Unit_Price")
VALUES
(1, 1, 2, 25.50),
(1, 3, 1, 15.00),

(2, 2, 3, 32.00),
(2, 5, 2, 18.20),

(3, 1, 4, 25.50),
(3, 4, 2, 28.75);


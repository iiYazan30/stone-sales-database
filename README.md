# Stone Sales Management System

DBMS course project: Stone Sales Management System built with **JavaFX + PostgreSQL**
(roles, orders, inventory, constraints, reports).

A university DBMS project implementing a desktop management system for a stone sales business.
The application is built with **JavaFX (GUI)** and **PostgreSQL (database)**, featuring a structured
architecture separating UI, business logic, and database operations.

## Features
- Role-based access: **Admin / Employee / Customer**
- Stone catalog management (**CRUD + images**)
- Order management (place/cancel orders, assign employees, update status)
- Stock validation & data integrity via database constraints
- Custom stone request workflow (approve → convert to order)
- Archived orders + email notifications for completed orders
- Reporting dashboard using **JasperReports**

## Tech Stack
- Java + JavaFX
- PostgreSQL + pgAdmin
- JasperReports (Jaspersoft Studio)


## How to Run

### 1) Prerequisites
Make sure you have the following installed:
- Java JDK 17+
- Maven
- PostgreSQL
- (Optional) IntelliJ IDEA / Eclipse

### 2) Database Setup (PostgreSQL)
1. Create a new PostgreSQL database (example: `stone_sales_db`)
2. Open the SQL script and run it:
   - `app/database/SQL.sql`

(Optional scripts / updates):
- `app/database/create_custom_order_table.sql`
- `app/database/add_order_id_to_custom_orders.sql`

### 3) Configure Database Connection
Update the database connection settings inside the project (e.g. DB URL, username, password).
> If your project has a config file or a class for DB connection, update it there.

Example:
- host: `localhost`
- port: `5432`
- database: `stone_sales_db`
- user/password: your PostgreSQL credentials

### 4) Run the Application
Open a terminal in the `app/` folder and run:

```bash
mvn clean install
mvn javafx:run


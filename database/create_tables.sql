-- 设置会话字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;
SET character_set_results=utf8mb4;
SET character_set_client=utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS car_sales DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE car_sales;

-- 【新增】先禁用外键检查，强制删除所有旧表，防止重复和冲突
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS purchase_order_detail;
DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS inventory_log;
DROP TABLE IF EXISTS permission;
DROP TABLE IF EXISTS system_user;
DROP TABLE IF EXISTS sale_detail;
DROP TABLE IF EXISTS sale_order;
DROP TABLE IF EXISTS purchase_order;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS car_model;
DROP TABLE IF EXISTS manufacturer;
-- 【新增】删除审计日志表（之前触发器里创建的）
DROP TABLE IF EXISTS audit_log;
-- 【新增】删除报表历史表（之前存储过程里创建的）
DROP TABLE IF EXISTS monthly_report_history;
SET FOREIGN_KEY_CHECKS=1;

-- 1. 厂商信息表 (manufacturer)
CREATE TABLE IF NOT EXISTS manufacturer (
    manufacturer_id INT PRIMARY KEY AUTO_INCREMENT,
    manufacturer_name VARCHAR(200) NOT NULL UNIQUE,
    contact_person VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(30) NOT NULL,
    address VARCHAR(1000) NOT NULL,
    cooperation_status ENUM('active', 'inactive', 'suspended') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_manufacturer_status (cooperation_status)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 车型信息表 (car_model)
CREATE TABLE IF NOT EXISTS car_model (
    model_id INT PRIMARY KEY AUTO_INCREMENT,
    manufacturer_id INT NOT NULL,
    model_name VARCHAR(500) NOT NULL,
    year INT NOT NULL,
    engine_type VARCHAR(50) NOT NULL,
    transmission VARCHAR(50) NOT NULL,
    fuel_type VARCHAR(20) NOT NULL,
    guide_price DECIMAL(10, 2) NOT NULL,
    features JSON DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturer(manufacturer_id) ON DELETE CASCADE,
    UNIQUE KEY uk_model_manufacturer_year (manufacturer_id, model_name, year),
    INDEX idx_model_year (year),
    INDEX idx_model_guide_price (guide_price)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. 车辆信息表 (vehicle) - 核心表
CREATE TABLE IF NOT EXISTS vehicle (
    vin VARCHAR(17) PRIMARY KEY, -- 车架号，17位全球唯一
    model_id INT NOT NULL,
    purchase_price DECIMAL(10, 2) NOT NULL,
    sale_price DECIMAL(10, 2) NOT NULL,
    status ENUM('in_stock', 'sold', 'reserved', 'in_transit') NOT NULL DEFAULT 'in_transit',
    warehouse_location VARCHAR(100) DEFAULT NULL,
    purchase_date DATE DEFAULT NULL,
    sale_date DATE DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (model_id) REFERENCES car_model(model_id) ON DELETE CASCADE,
    CHECK (sale_price >= purchase_price),
    INDEX idx_vehicle_status (status),
    INDEX idx_vehicle_purchase_date (purchase_date),
    INDEX idx_vehicle_sale_date (sale_date)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 4. 客户信息表 (customer)
CREATE TABLE IF NOT EXISTS customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_type ENUM('individual', 'enterprise') NOT NULL DEFAULT 'individual',
    name VARCHAR(200) NOT NULL,
    id_number VARCHAR(200) NOT NULL, -- 身份证号或统一信用代码，加密存储
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(200) DEFAULT NULL,
    address VARCHAR(1000) DEFAULT NULL,
    credit_rating ENUM('A', 'B', 'C', 'D') DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_customer_id_number (id_number),
    INDEX idx_customer_type (customer_type),
    INDEX idx_customer_credit_rating (credit_rating)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 5. 采购订单表 (purchase_order)
CREATE TABLE IF NOT EXISTS purchase_order (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    manufacturer_id INT NOT NULL,
    operator_id INT NOT NULL, -- 外键引用system_user
    total_amount DECIMAL(12, 2) NOT NULL,
    status ENUM('pending', 'approved', 'received', 'cancelled') NOT NULL DEFAULT 'pending',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approve_time TIMESTAMP DEFAULT NULL,
    receive_time TIMESTAMP DEFAULT NULL,
    cancel_time TIMESTAMP DEFAULT NULL,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturer(manufacturer_id) ON DELETE CASCADE,
    INDEX idx_order_status (status),
    INDEX idx_order_create_time (create_time)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 6. 销售订单表 (sale_order)
CREATE TABLE IF NOT EXISTS sale_order (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    operator_id INT NOT NULL, -- 外键引用system_user
    total_amount DECIMAL(12, 2) NOT NULL,
    payment_method ENUM('cash', 'loan', 'installment') NOT NULL,
    status ENUM('pending', 'paid', 'delivered', 'cancelled') NOT NULL DEFAULT 'pending',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_time TIMESTAMP DEFAULT NULL,
    delivery_time TIMESTAMP DEFAULT NULL,
    cancel_time TIMESTAMP DEFAULT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    INDEX idx_sale_status (status),
    INDEX idx_sale_create_time (create_time),
    INDEX idx_sale_payment_method (payment_method)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 7. 销售明细表 (sale_detail)
CREATE TABLE IF NOT EXISTS sale_detail (
    detail_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_id INT NOT NULL,
    vin VARCHAR(17) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(5, 2) DEFAULT 0.00,
    quantity INT NOT NULL DEFAULT 1, -- 通常为1，一辆车
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sale_order(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (vin) REFERENCES vehicle(vin) ON DELETE CASCADE,
    UNIQUE KEY uk_sale_vin (sale_id, vin),
    CHECK (discount >= 0 AND discount <= unit_price),
    CHECK (subtotal = unit_price - discount)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 8. 系统用户表 (system_user)
CREATE TABLE IF NOT EXISTS system_user (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL, -- 加密存储
    real_name VARCHAR(200) NOT NULL,
    role ENUM('admin', 'sales', 'warehouse', 'finance', 'manager') NOT NULL,
    status ENUM('active', 'inactive', 'locked') NOT NULL DEFAULT 'active',
    last_login_time TIMESTAMP DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_role (role),
    INDEX idx_user_status (status)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 9. 权限表 (permission)
CREATE TABLE IF NOT EXISTS permission (
    permission_id INT PRIMARY KEY AUTO_INCREMENT,
    role ENUM('admin', 'sales', 'warehouse', 'finance', 'manager') NOT NULL,
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description VARCHAR(1000) DEFAULT NULL,
    UNIQUE KEY uk_role_resource_action (role, resource, action),
    INDEX idx_permission_role (role)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 10. 库存流水表 (inventory_log)
CREATE TABLE IF NOT EXISTS inventory_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    vin VARCHAR(17) NOT NULL,
    action_type ENUM('in', 'out', 'status_change', 'location_change') NOT NULL,
    old_status ENUM('in_stock', 'sold', 'reserved', 'in_transit') DEFAULT NULL,
    new_status ENUM('in_stock', 'sold', 'reserved', 'in_transit') DEFAULT NULL,
    old_location VARCHAR(200) DEFAULT NULL,
    new_location VARCHAR(200) DEFAULT NULL,
    operator_id INT NOT NULL,
    remark VARCHAR(1000) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vin) REFERENCES vehicle(vin) ON DELETE CASCADE,
    FOREIGN KEY (operator_id) REFERENCES system_user(user_id) ON DELETE CASCADE,
    INDEX idx_log_action_type (action_type),
    INDEX idx_log_created_at (created_at)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 11. 操作日志表 (operation_log)
CREATE TABLE IF NOT EXISTS operation_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    table_name VARCHAR(50) NOT NULL,
    record_id VARCHAR(100) NOT NULL,
    old_data JSON DEFAULT NULL,
    new_data JSON DEFAULT NULL,
    ip_address VARCHAR(45) DEFAULT NULL,
    user_agent VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES system_user(user_id) ON DELETE CASCADE,
    INDEX idx_log_operation_type (operation_type),
    INDEX idx_log_table_name (table_name),
    INDEX idx_log_created_at (created_at)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 12. 仓库信息表 (warehouse)
CREATE TABLE IF NOT EXISTS warehouse (
    warehouse_id INT PRIMARY KEY AUTO_INCREMENT,
    warehouse_name VARCHAR(200) NOT NULL,
    address VARCHAR(1000) NOT NULL,
    capacity INT NOT NULL,
    current_stock INT NOT NULL DEFAULT 0,
    manager_name VARCHAR(100) NOT NULL,
    manager_phone VARCHAR(30) NOT NULL,
    status ENUM('active', 'inactive') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_warehouse_name (warehouse_name),
    INDEX idx_warehouse_status (status)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 13. 采购订单明细表 (purchase_order_detail)
CREATE TABLE IF NOT EXISTS purchase_order_detail (
    detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    model_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES purchase_order(order_id) ON DELETE CASCADE,
    FOREIGN KEY (model_id) REFERENCES car_model(model_id) ON DELETE CASCADE,
    UNIQUE KEY uk_order_model (order_id, model_id),
    CHECK (subtotal = unit_price * quantity)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 现在添加之前注释的外键约束
ALTER TABLE purchase_order ADD FOREIGN KEY (operator_id) REFERENCES system_user(user_id) ON DELETE CASCADE;
ALTER TABLE sale_order ADD FOREIGN KEY (operator_id) REFERENCES system_user(user_id) ON DELETE CASCADE;

-- 添加一些示例数据
INSERT INTO manufacturer (manufacturer_name, contact_person, contact_phone, address, cooperation_status) VALUES
('Shanghai Volkswagen', 'Zhang San', '13800138001', 'Shanghai Zhangjiang High-tech Park', 'active'),
('FAW Toyota', 'Li Si', '13800138002', 'Changchun City, Jilin Province', 'active'),
('Guangzhou Honda', 'Wang Wu', '13800138003', 'Guangzhou City, Guangdong Province', 'active');

INSERT INTO car_model (manufacturer_id, model_name, year, engine_type, transmission, fuel_type, guide_price, features) VALUES
(1, 'Passat', 2023, '1.4T', '7-speed dual-clutch', 'Gasoline', 189900.00, '{"seats": 5, "safety": ["ABS", "ESP"], "comfort": ["Auto AC", "Leather Seats"]}'),
(1, 'Tiguan L', 2023, '2.0T', '7-speed dual-clutch', 'Gasoline', 215800.00, '{"seats": 5, "safety": ["ABS", "ESP", "Airbags x6"], "comfort": ["Panoramic Sunroof", "Power Seats"]}'),
(2, 'Corolla', 2023, '1.2T', 'CVT', 'Gasoline', 109800.00, '{"seats": 5, "safety": ["ABS", "ESP"], "comfort": ["Manual AC", "Fabric Seats"]}'),
(2, 'Camry', 2023, '2.0L', 'CVT', 'Gasoline', 179800.00, '{"seats": 5, "safety": ["ABS", "ESP", "Airbags x8"], "comfort": ["Auto AC", "Leather Seats"]}'),
(3, 'Accord', 2023, '1.5T', 'CVT', 'Gasoline', 169800.00, '{"seats": 5, "safety": ["ABS", "ESP", "Airbags x6"], "comfort": ["Auto AC", "Leather Seats"]}');

INSERT INTO system_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$X/hX9.x/x.x/x.x/x.x/x.x/x.x/x', 'System Admin', 'admin', 'active'),
('sales001', '$2a$10$X/hX9.x/x.x/x.x/x.x/x.x/x.x/x', 'Sales Li', 'sales', 'active'),
('warehouse001', '$2a$10$X/hX9.x/x.x/x.x/x.x/x.x/x.x/x', 'Warehouse Wang', 'warehouse', 'active'),
('finance001', '$2a$10$X/hX9.x/x.x/x.x/x.x/x.x/x.x/x', 'Finance Zhang', 'finance', 'active'),
('manager001', '$2a$10$X/hX9.x/x.x/x.x/x.x/x.x/x.x/x', 'Manager Liu', 'manager', 'active');

INSERT INTO permission (role, resource, action, description) VALUES
('admin', 'manufacturer', 'read', 'View manufacturer info'),
('admin', 'manufacturer', 'create', 'Create manufacturer info'),
('admin', 'manufacturer', 'update', 'Update manufacturer info'),
('admin', 'manufacturer', 'delete', 'Delete manufacturer info'),
('sales', 'vehicle', 'read', 'View vehicle info'),
('sales', 'sale_order', 'create', 'Create sale order'),
('sales', 'sale_order', 'update', 'Update sale order'),
('warehouse', 'vehicle', 'read', 'View vehicle info'),
('warehouse', 'inventory_log', 'create', 'Record inventory log'),
('finance', 'sale_order', 'read', 'View sale order'),
('finance', 'payment', 'process', 'Process payment');

-- 添加仓库示例数据
INSERT INTO warehouse (warehouse_name, address, capacity, current_stock, manager_name, manager_phone, status) VALUES
('Shanghai Pudong Warehouse', 'Shanghai Zhangjiang High-tech Park', 500, 0, 'Manager Zhang', '13800138001', 'active'),
('Beijing Chaoyang Warehouse', 'Beijing CBD Business District', 200, 0, 'Manager Li', '13800138002', 'active'),
('Guangzhou Tianhe Warehouse', 'Guangzhou Pearl River New City', 300, 0, 'Manager Wang', '13800138003', 'active');
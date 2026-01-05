-- database/create_tables.sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 注意：删库操作建议放在 init_db.bat 中执行，这里只负责建表逻辑
-- 如果一定要在 SQL 文件里写删库，需要注意当前连接上下文的问题
-- 这里我们保持“只负责表结构”的职责，但确保表结构是最新的

-- 清理旧表 (保留这些 DROP 语句作为双重保险)
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
DROP TABLE IF EXISTS warehouse;
DROP TABLE IF EXISTS car_model;
DROP TABLE IF EXISTS manufacturer;
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS monthly_report_history;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. 厂商信息表
CREATE TABLE IF NOT EXISTS manufacturer (
                                            manufacturer_id INT PRIMARY KEY AUTO_INCREMENT,
                                            manufacturer_name VARCHAR(200) NOT NULL UNIQUE,
    contact_person VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(30) NOT NULL,
    address VARCHAR(1000) NOT NULL,
    cooperation_status ENUM('active', 'inactive', 'suspended') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 车型信息表
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
    UNIQUE KEY uk_model_manufacturer_year (manufacturer_id, model_name, year)
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. 车辆信息表
CREATE TABLE IF NOT EXISTS vehicle (
                                       vin VARCHAR(17) PRIMARY KEY,
    model_id INT NOT NULL,
    purchase_price DECIMAL(10, 2) NOT NULL,
    sale_price DECIMAL(10, 2) NOT NULL,
    status ENUM('in_stock', 'sold', 'reserved', 'in_transit') NOT NULL DEFAULT 'in_transit',
    warehouse_location VARCHAR(100) DEFAULT NULL COMMENT '库位备注(可选)',
    purchase_date DATE DEFAULT NULL,
    sale_date DATE DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (model_id) REFERENCES car_model(model_id) ON DELETE CASCADE,
    INDEX idx_vehicle_status (status)
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 4. 客户信息表
CREATE TABLE IF NOT EXISTS customer (
                                        customer_id INT PRIMARY KEY AUTO_INCREMENT,
                                        customer_type ENUM('individual', 'enterprise') NOT NULL DEFAULT 'individual',
    name VARCHAR(200) NOT NULL,
    id_number VARCHAR(200) NOT NULL UNIQUE,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(200) DEFAULT NULL,
    address VARCHAR(1000) DEFAULT NULL,
    credit_rating ENUM('A', 'B', 'C', 'D') DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 5. 采购订单表
CREATE TABLE IF NOT EXISTS purchase_order (
                                              order_id INT PRIMARY KEY AUTO_INCREMENT,
                                              order_number VARCHAR(50) NOT NULL UNIQUE,
    manufacturer_id INT NOT NULL,
    operator_id INT NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    status ENUM('pending', 'approved', 'received', 'cancelled') NOT NULL DEFAULT 'pending',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturer(manufacturer_id) ON DELETE CASCADE
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 6. 销售订单表
CREATE TABLE IF NOT EXISTS sale_order (
                                          sale_id INT PRIMARY KEY AUTO_INCREMENT,
                                          order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    operator_id INT NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    payment_method ENUM('cash', 'loan', 'installment') NOT NULL,
    status ENUM('pending', 'paid', 'delivered', 'cancelled') NOT NULL DEFAULT 'pending',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_time TIMESTAMP DEFAULT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


-- 7. 销售明细表 (重点修改：加入 operator_id)
CREATE TABLE IF NOT EXISTS sale_detail (
                                           detail_id INT PRIMARY KEY AUTO_INCREMENT,
                                           sale_id INT NOT NULL,
                                           vin VARCHAR(17) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    operator_id INT NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sale_order(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (vin) REFERENCES vehicle(vin) ON DELETE CASCADE,
    -- 注意：这里删除了 operator_id 的外键定义，移到文件末尾执行
    UNIQUE KEY uk_sale_vin (sale_id, vin)
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 8. 系统用户表
CREATE TABLE IF NOT EXISTS system_user (
                                           user_id INT PRIMARY KEY AUTO_INCREMENT,
                                           username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(200) NOT NULL,
    role ENUM('admin', 'sales', 'warehouse', 'finance', 'manager') NOT NULL,
    status ENUM('active', 'inactive', 'locked') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 9. 库存流水表
CREATE TABLE IF NOT EXISTS inventory_log (
                                             log_id INT PRIMARY KEY AUTO_INCREMENT,
                                             vin VARCHAR(17) NOT NULL,
    action_type ENUM('in', 'out', 'status_change', 'location_change') NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20),
    old_location VARCHAR(200),
    new_location VARCHAR(200),
    operator_id INT NOT NULL,
    remark VARCHAR(1000) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vin) REFERENCES vehicle(vin) ON DELETE CASCADE,
    FOREIGN KEY (operator_id) REFERENCES system_user(user_id) ON DELETE CASCADE
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 10. 采购订单明细
CREATE TABLE IF NOT EXISTS purchase_order_detail (
                                                     detail_id INT PRIMARY KEY AUTO_INCREMENT,
                                                     order_id INT NOT NULL,
                                                     model_id INT NOT NULL,
                                                     quantity INT NOT NULL,
                                                     unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES purchase_order(order_id) ON DELETE CASCADE,
    FOREIGN KEY (model_id) REFERENCES car_model(model_id) ON DELETE CASCADE
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 补充外键 (防止上面 CREATE TABLE 时表还没建好报错)
ALTER TABLE purchase_order ADD FOREIGN KEY (operator_id) REFERENCES system_user(user_id);
ALTER TABLE sale_order ADD FOREIGN KEY (operator_id) REFERENCES system_user(user_id);
-- 【新增】修复 sale_detail 的外键依赖
ALTER TABLE sale_detail ADD FOREIGN KEY (operator_id) REFERENCES system_user(user_id);

-- 初始化基础数据
INSERT INTO manufacturer (manufacturer_name, contact_person, contact_phone, address) VALUES
                                                                                         ('上海大众', '张三', '13800138001', '上海'),
                                                                                         ('一汽丰田', '李四', '13800138002', '长春');

INSERT INTO car_model (manufacturer_id, model_name, year, engine_type, transmission, fuel_type, guide_price) VALUES
                                                                                                                 (1, '帕萨特', 2023, '2.0T', 'DCT', '汽油', 180000),
                                                                                                                 (2, '凯美瑞', 2023, '2.5L', 'AT', '汽油', 200000);

INSERT INTO customer (customer_type, name, id_number, phone, email, address, credit_rating) VALUES
                                                                                                ('individual', '王小明', '110101199001011234', '13900139000', 'wangxm@example.com', '北京市朝阳区朝阳路100号', 'A'),
                                                                                                ('individual', '李美丽', '310101199505055678', '13800138000', 'liml@example.com', '上海市浦东新区世纪大道200号', 'B'),
                                                                                                ('enterprise', '宏图物流有限公司', '91110000123456789X', '010-88888888', 'contact@hongtu.com', '北京市海淀区科技园8号楼', 'A');


-- 初始化管理员 (user_id 自动生成，通常为1)
INSERT INTO system_user (username, password, real_name, role) VALUES
    ('admin', '$2a$10$X/hX9.x/x.x/x.x/x.x/x.x/x.x/x', '管理员', 'admin');
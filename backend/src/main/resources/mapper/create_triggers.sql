-- 使用数据库
USE car_sale_system;

-- 触发器1：销售时自动更新车辆状态
-- 触发时机：销售明细表插入后
-- 执行动作：更新对应车辆状态为"sold"，记录出库日期
-- 设计目的：保证数据一致性，减少应用层代码
DELIMITER $$
CREATE OR REPLACE TRIGGER after_sale_detail_insert
AFTER INSERT ON sale_detail
FOR EACH ROW
BEGIN
    -- 更新车辆状态为已售
    UPDATE vehicle 
    SET 
        status = 'sold',
        sale_date = CURRENT_DATE
    WHERE vin = NEW.vin;
    
    -- 记录库存出库流水
    INSERT INTO inventory_log (
        vin, 
        action_type, 
        old_status, 
        new_status, 
        operator_id,
        remark
    ) VALUES (
        NEW.vin, 
        'out', 
        'in_stock', 
        'sold', 
        (SELECT operator_id FROM sale_order WHERE sale_id = NEW.sale_id),
        CONCAT('销售出库，销售订单ID:', NEW.sale_id)
    );
END $$
DELIMITER ;

-- 触发器2：采购入库时自动记录流水
-- 触发时机：车辆表状态更新为"in_stock"时
-- 执行动作：在库存流水表插入入库记录
-- 审计价值：完整记录每辆车的入库轨迹
DELIMITER $$
CREATE OR REPLACE TRIGGER after_vehicle_status_update
AFTER UPDATE ON vehicle
FOR EACH ROW
BEGIN
    -- 仅当状态变更为in_stock时执行
    IF NEW.status = 'in_stock' AND OLD.status != 'in_stock' THEN
        INSERT INTO inventory_log (
            vin, 
            action_type, 
            old_status, 
            new_status, 
            old_location, 
            new_location, 
            operator_id,
            remark
        ) VALUES (
            NEW.vin, 
            'in', 
            OLD.status, 
            NEW.status, 
            OLD.warehouse_location, 
            NEW.warehouse_location, 
            NULL, -- 需要应用层在更新时提供操作人ID
            CONCAT('采购入库，从状态', OLD.status, '变更为', NEW.status)
        );
    END IF;
    
    -- 当状态从in_stock变更为其他状态时
    IF OLD.status = 'in_stock' AND NEW.status != 'in_stock' THEN
        INSERT INTO inventory_log (
            vin, 
            action_type, 
            old_status, 
            new_status, 
            operator_id,
            remark
        ) VALUES (
            NEW.vin, 
            'status_change', 
            OLD.status, 
            NEW.status, 
            NULL, -- 需要应用层在更新时提供操作人ID
            CONCAT('状态变更，从', OLD.status, '变更为', NEW.status)
        );
    END IF;
    
    -- 当仓库位置变更时
    IF OLD.warehouse_location != NEW.warehouse_location THEN
        INSERT INTO inventory_log (
            vin, 
            action_type, 
            old_location, 
            new_location, 
            operator_id,
            remark
        ) VALUES (
            NEW.vin, 
            'location_change', 
            OLD.warehouse_location, 
            NEW.warehouse_location, 
            NULL, -- 需要应用层在更新时提供操作人ID
            '仓库位置变更'
        );
    END IF;
END $$
DELIMITER ;

-- 触发器3：关键数据变更审计日志
-- 触发时机：重要业务表数据变更时
-- 执行动作：记录变更前值、变更后值、操作人、操作时间
-- 安全合规：满足数据审计和合规要求

-- 创建操作日志表（如果不存在）
CREATE TABLE IF NOT EXISTS audit_log (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(50) NOT NULL,
    record_id VARCHAR(100) NOT NULL,
    column_name VARCHAR(50) NOT NULL,
    old_value TEXT DEFAULT NULL,
    new_value TEXT DEFAULT NULL,
    action_type ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    user_id INT DEFAULT NULL,
    user_name VARCHAR(50) DEFAULT NULL,
    ip_address VARCHAR(45) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_table (table_name),
    INDEX idx_audit_record (record_id),
    INDEX idx_audit_time (created_at)
);

-- 为车辆表创建更新审计触发器
DELIMITER $$
CREATE OR REPLACE TRIGGER after_vehicle_update_audit
AFTER UPDATE ON vehicle
FOR EACH ROW
BEGIN
    -- 记录状态变更
    IF OLD.status != NEW.status THEN
        INSERT INTO audit_log (
            table_name, record_id, column_name, old_value, new_value, action_type
        ) VALUES (
            'vehicle', NEW.vin, 'status', OLD.status, NEW.status, 'UPDATE'
        );
    END IF;
    
    -- 记录价格变更
    IF OLD.sale_price != NEW.sale_price THEN
        INSERT INTO audit_log (
            table_name, record_id, column_name, old_value, new_value, action_type
        ) VALUES (
            'vehicle', NEW.vin, 'sale_price', OLD.sale_price, NEW.sale_price, 'UPDATE'
        );
    END IF;
    
    -- 记录仓库位置变更
    IF OLD.warehouse_location != NEW.warehouse_location THEN
        INSERT INTO audit_log (
            table_name, record_id, column_name, old_value, new_value, action_type
        ) VALUES (
            'vehicle', NEW.vin, 'warehouse_location', OLD.warehouse_location, NEW.warehouse_location, 'UPDATE'
        );
    END IF;
END $$
DELIMITER ;

-- 为销售订单表创建更新审计触发器
DELIMITER $$
CREATE OR REPLACE TRIGGER after_sale_order_update_audit
AFTER UPDATE ON sale_order
FOR EACH ROW
BEGIN
    -- 记录状态变更
    IF OLD.status != NEW.status THEN
        INSERT INTO audit_log (
            table_name, record_id, column_name, old_value, new_value, action_type
        ) VALUES (
            'sale_order', NEW.sale_id, 'status', OLD.status, NEW.status, 'UPDATE'
        );
    END IF;
    
    -- 记录支付方式变更
    IF OLD.payment_method != NEW.payment_method THEN
        INSERT INTO audit_log (
            table_name, record_id, column_name, old_value, new_value, action_type
        ) VALUES (
            'sale_order', NEW.sale_id, 'payment_method', OLD.payment_method, NEW.payment_method, 'UPDATE'
        );
    END IF;
    
    -- 记录总金额变更
    IF OLD.total_amount != NEW.total_amount THEN
        INSERT INTO audit_log (
            table_name, record_id, column_name, old_value, new_value, action_type
        ) VALUES (
            'sale_order', NEW.sale_id, 'total_amount', OLD.total_amount, NEW.total_amount, 'UPDATE'
        );
    END IF;
END $$
DELIMITER ;

-- 为采购订单表创建更新审计触发器
DELIMITER $$
CREATE OR REPLACE TRIGGER after_purchase_order_update_audit
AFTER UPDATE ON purchase_order
FOR EACH ROW
BEGIN
    -- 记录状态变更
    IF OLD.status != NEW.status THEN
        INSERT INTO audit_log (
            table_name, record_id, column_name, old_value, new_value, action_type
        ) VALUES (
            'purchase_order', NEW.order_id, 'status', OLD.status, NEW.status, 'UPDATE'
        );
    END IF;
    
    -- 记录总金额变更
    IF OLD.total_amount != NEW.total_amount THEN
        INSERT INTO audit_log (
            table_name, record_id, column_name, old_value, new_value, action_type
        ) VALUES (
            'purchase_order', NEW.order_id, 'total_amount', OLD.total_amount, NEW.total_amount, 'UPDATE'
        );
    END IF;
END $$
DELIMITER ;

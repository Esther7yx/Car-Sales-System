USE car_sale_system;

-- 触发器1: 销售明细插入后，更新车辆状态并记录出库日志
DROP TRIGGER IF EXISTS after_sale_detail_insert;
DELIMITER $$
CREATE TRIGGER after_sale_detail_insert
    AFTER INSERT ON sale_detail
    FOR EACH ROW
BEGIN
    -- 1. 更新车辆状态为已售
    UPDATE vehicle SET status = 'sold', sale_date = CURRENT_DATE WHERE vin = NEW.vin;

    -- 2. 记录出库日志 (这里能获取到订单的操作员ID，所以是准确的)
    INSERT INTO inventory_log (vin, action_type, old_status, new_status, operator_id, remark)
    VALUES (NEW.vin, 'out', 'in_stock', 'sold', (SELECT operator_id FROM sale_order WHERE sale_id = NEW.sale_id), CONCAT('销售出库，订单:', NEW.sale_id));
END $$
DELIMITER ;

-- 触发器2: 车辆状态或位置变更后，自动记录日志
-- 【关键修复】为所有 INSERT 语句补充 operator_id (默认为1)
DROP TRIGGER IF EXISTS after_vehicle_status_update;
DELIMITER $$
CREATE TRIGGER after_vehicle_status_update
    AFTER UPDATE ON vehicle
    FOR EACH ROW
BEGIN
    -- 场景A: 采购入库 (非在库 -> 在库)
    IF NEW.status = 'in_stock' AND OLD.status != 'in_stock' THEN
        INSERT INTO inventory_log (vin, action_type, old_status, new_status, old_location, new_location, operator_id, remark)
        VALUES (NEW.vin, 'in', OLD.status, NEW.status, OLD.warehouse_location, NEW.warehouse_location, 1, '采购入库');
END IF;

-- 场景B: 状态变更 (在库 -> 其他状态，例如被预定或被售出)
-- 注意：如果是销售导致的状态变更，这里也会触发，产生一条 'status_change' 日志，与上面的 'out' 日志并存
IF OLD.status = 'in_stock' AND NEW.status != 'in_stock' THEN
        INSERT INTO inventory_log (vin, action_type, old_status, new_status, operator_id, remark)
        VALUES (NEW.vin, 'status_change', OLD.status, NEW.status, 1, '状态变更');
END IF;

    -- 场景C: 库位变更
    IF OLD.warehouse_location != NEW.warehouse_location THEN
        INSERT INTO inventory_log (vin, action_type, old_location, new_location, operator_id, remark)
        VALUES (NEW.vin, 'location_change', OLD.warehouse_location, NEW.warehouse_location, 1, '位置变更');
END IF;
END $$
DELIMITER ;

-- 触发器3: 审计日志 (保持不变)
DROP TABLE IF EXISTS audit_log;
CREATE TABLE IF NOT EXISTS audit_log (
                                         audit_id INT PRIMARY KEY AUTO_INCREMENT,
                                         table_name VARCHAR(50),
    record_id VARCHAR(100),
    column_name VARCHAR(50),
    old_value TEXT,
    new_value TEXT,
    action_type VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

DROP TRIGGER IF EXISTS after_vehicle_update_audit;
DELIMITER $$
CREATE TRIGGER after_vehicle_update_audit
    AFTER UPDATE ON vehicle
    FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO audit_log (table_name, record_id, column_name, old_value, new_value, action_type) 
        VALUES ('vehicle', NEW.vin, 'status', OLD.status, NEW.status, 'UPDATE');
END IF;
IF OLD.sale_price != NEW.sale_price THEN
        INSERT INTO audit_log (table_name, record_id, column_name, old_value, new_value, action_type) 
        VALUES ('vehicle', NEW.vin, 'sale_price', OLD.sale_price, NEW.sale_price, 'UPDATE');
END IF;
END $$
DELIMITER ;

DROP TRIGGER IF EXISTS after_sale_order_update_audit;
DELIMITER $$
CREATE TRIGGER after_sale_order_update_audit
    AFTER UPDATE ON sale_order
    FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO audit_log (table_name, record_id, column_name, old_value, new_value, action_type) 
        VALUES ('sale_order', NEW.sale_id, 'status', OLD.status, NEW.status, 'UPDATE');
END IF;
END $$
DELIMITER ;

DROP TRIGGER IF EXISTS after_purchase_order_update_audit;
DELIMITER $$
CREATE TRIGGER after_purchase_order_update_audit
    AFTER UPDATE ON purchase_order
    FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO audit_log (table_name, record_id, column_name, old_value, new_value, action_type) 
        VALUES ('purchase_order', NEW.order_id, 'status', OLD.status, NEW.status, 'UPDATE');
END IF;
END $$
DELIMITER ;
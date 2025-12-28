USE car_sale_system;

-- 存储过程1：采购入库处理流程
DROP PROCEDURE IF EXISTS process_purchase_inventory;
DELIMITER $$
CREATE PROCEDURE process_purchase_inventory(
    IN p_order_id INT,
    IN p_vin VARCHAR(17),
    IN p_warehouse_location VARCHAR(100),
    IN p_operator_id INT
)
BEGIN
    DECLARE v_old_status ENUM('in_stock', 'sold', 'reserved', 'in_transit');
    DECLARE v_model_id INT;
    DECLARE v_total_received INT;
    DECLARE v_total_order INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Purchase inventory process failed';
    END;

    START TRANSACTION;

    SELECT status, model_id INTO v_old_status, v_model_id
    FROM vehicle WHERE vin = p_vin FOR UPDATE;

    IF v_old_status IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Vehicle not found';
    END IF;

    UPDATE vehicle 
    SET status = 'in_stock', warehouse_location = p_warehouse_location, purchase_date = CURRENT_DATE
    WHERE vin = p_vin;

    INSERT INTO inventory_log (vin, action_type, old_status, new_status, new_location, operator_id, remark) 
    VALUES (p_vin, 'in', v_old_status, 'in_stock', p_warehouse_location, p_operator_id, CONCAT('Purchase In, OrderID:', p_order_id));

    SELECT COUNT(*) INTO v_total_order FROM purchase_order_detail WHERE order_id = p_order_id;

    SELECT COUNT(DISTINCT v.vin) INTO v_total_received
    FROM purchase_order_detail pod
    INNER JOIN vehicle v ON v.model_id = pod.model_id
    WHERE pod.order_id = p_order_id AND v.status = 'in_stock';

    IF v_total_received >= v_total_order THEN
        UPDATE purchase_order SET status = 'received', receive_time = CURRENT_TIMESTAMP WHERE order_id = p_order_id;
    END IF;

    COMMIT;
    SELECT 'Success' AS result;
END $$
DELIMITER ;

-- 存储过程2：销售订单处理流程
DROP PROCEDURE IF EXISTS process_sale_order;
DELIMITER $$
CREATE PROCEDURE process_sale_order(
    IN p_customer_id INT,
    IN p_operator_id INT,
    IN p_payment_method ENUM('cash', 'loan', 'installment'),
    IN p_sale_items JSON
)
BEGIN
    DECLARE v_sale_id INT;
    DECLARE v_order_number VARCHAR(50);
    DECLARE v_total_amount DECIMAL(12, 2) DEFAULT 0;
    DECLARE v_vin VARCHAR(17);
    DECLARE v_unit_price DECIMAL(10, 2);
    DECLARE v_discount DECIMAL(5, 2);
    DECLARE v_subtotal DECIMAL(10, 2);
    DECLARE v_item_count INT;
    DECLARE v_current_item INT DEFAULT 0;
    DECLARE v_vehicle_status ENUM('in_stock', 'sold', 'reserved', 'in_transit');
    -- 【修复点1】新增变量用于存储错误信息
    DECLARE v_error_msg VARCHAR(255);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Sale order process failed';
    END;

    START TRANSACTION;

    SET v_order_number = CONCAT('SO_', DATE_FORMAT(CURRENT_DATE, '%Y%m%d'), '_', LPAD(FLOOR(RAND() * 10000), 4, '0'));
    SET v_item_count = JSON_LENGTH(p_sale_items);
    
    WHILE v_current_item < v_item_count DO
        SET v_unit_price = JSON_UNQUOTE(JSON_EXTRACT(p_sale_items, CONCAT('$[', v_current_item, '].unit_price')));
        SET v_discount = JSON_UNQUOTE(JSON_EXTRACT(p_sale_items, CONCAT('$[', v_current_item, '].discount')));
        SET v_subtotal = v_unit_price - IFNULL(v_discount, 0);
        SET v_total_amount = v_total_amount + v_subtotal;
        SET v_current_item = v_current_item + 1;
    END WHILE;

    INSERT INTO sale_order (order_number, customer_id, operator_id, total_amount, payment_method, status) 
    VALUES (v_order_number, p_customer_id, p_operator_id, v_total_amount, p_payment_method, 'pending');

    SET v_sale_id = LAST_INSERT_ID();
    SET v_current_item = 0;

    WHILE v_current_item < v_item_count DO
        SET v_vin = JSON_UNQUOTE(JSON_EXTRACT(p_sale_items, CONCAT('$[', v_current_item, '].vin')));
        SET v_unit_price = JSON_UNQUOTE(JSON_EXTRACT(p_sale_items, CONCAT('$[', v_current_item, '].unit_price')));
        SET v_discount = JSON_UNQUOTE(JSON_EXTRACT(p_sale_items, CONCAT('$[', v_current_item, '].discount')));
        SET v_subtotal = v_unit_price - IFNULL(v_discount, 0);

        SELECT status INTO v_vehicle_status FROM vehicle WHERE vin = v_vin FOR UPDATE;

        IF v_vehicle_status != 'in_stock' THEN
            -- 【修复点2】先拼接字符串存入变量，再抛出异常
            SET v_error_msg = CONCAT('Error: Vehicle ', v_vin, ' status abnormal');
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = v_error_msg;
        END IF;

        INSERT INTO sale_detail (sale_id, vin, unit_price, discount, quantity, subtotal) 
        VALUES (v_sale_id, v_vin, v_unit_price, IFNULL(v_discount, 0), 1, v_subtotal);

        SET v_current_item = v_current_item + 1;
    END WHILE;

    COMMIT;
    SELECT v_sale_id AS sale_id, v_order_number AS order_number;
END $$
DELIMITER ;

-- 存储过程3：月度报表生成
DROP PROCEDURE IF EXISTS generate_monthly_report;
DELIMITER $$
CREATE PROCEDURE generate_monthly_report(
    IN p_year INT,
    IN p_month INT
)
BEGIN
    DECLARE v_report_date DATE;
    DECLARE v_start_date DATE;
    DECLARE v_end_date DATE;
    DECLARE v_order_count INT;
    DECLARE v_vehicle_count INT;
    DECLARE v_total_sales DECIMAL(15, 2);
    DECLARE v_total_profit DECIMAL(15, 2);
    DECLARE v_avg_profit_margin DECIMAL(5, 2);
    DECLARE v_current_stock INT;
    DECLARE v_avg_days_in_stock DECIMAL(5, 2);
    DECLARE v_inventory_value DECIMAL(15, 2);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Monthly report generation failed';
    END;

    START TRANSACTION;

    SET v_report_date = STR_TO_DATE(CONCAT(p_year, '-', p_month, '-01'), '%Y-%m-%d');
    SET v_start_date = v_report_date;
    SET v_end_date = LAST_DAY(v_report_date);

    CREATE TABLE IF NOT EXISTS monthly_report_history (
        report_id INT PRIMARY KEY AUTO_INCREMENT,
        report_year INT NOT NULL,
        report_month INT NOT NULL,
        report_date DATE NOT NULL,
        order_count INT NOT NULL,
        vehicle_count INT NOT NULL,
        total_sales DECIMAL(15, 2) NOT NULL,
        total_profit DECIMAL(15, 2) NOT NULL,
        avg_profit_margin DECIMAL(5, 2) NOT NULL,
        current_stock INT NOT NULL,
        avg_days_in_stock DECIMAL(5, 2) NOT NULL,
        inventory_value DECIMAL(15, 2) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        UNIQUE KEY uk_report_year_month (report_year, report_month)
    );

    SELECT 
        COUNT(DISTINCT so.sale_id), COUNT(sd.vin), COALESCE(SUM(sd.subtotal), 0),
        COALESCE(SUM(sd.subtotal - v.purchase_price), 0),
        CASE WHEN SUM(sd.subtotal) > 0 THEN ROUND((SUM(sd.subtotal - v.purchase_price) / SUM(sd.subtotal)) * 100, 2) ELSE 0 END
    INTO v_order_count, v_vehicle_count, v_total_sales, v_total_profit, v_avg_profit_margin
    FROM sale_order so
    LEFT JOIN sale_detail sd ON so.sale_id = sd.sale_id
    LEFT JOIN vehicle v ON sd.vin = v.vin
    WHERE so.status = 'delivered' AND so.create_time BETWEEN v_start_date AND DATE_ADD(v_end_date, INTERVAL 1 DAY);

    SELECT COUNT(*), ROUND(AVG(DATEDIFF(v_report_date, v.purchase_date)), 2), COALESCE(SUM(v.sale_price), 0)
    INTO v_current_stock, v_avg_days_in_stock, v_inventory_value
    FROM vehicle v
    WHERE v.status = 'in_stock' AND v.purchase_date <= v_end_date;

    INSERT INTO monthly_report_history (
        report_year, report_month, report_date, order_count, vehicle_count, total_sales, total_profit, 
        avg_profit_margin, current_stock, avg_days_in_stock, inventory_value
    ) VALUES (
        p_year, p_month, v_report_date, v_order_count, v_vehicle_count, v_total_sales, v_total_profit, 
        v_avg_profit_margin, v_current_stock, v_avg_days_in_stock, v_inventory_value
    ) ON DUPLICATE KEY UPDATE
        report_date = v_report_date, order_count = v_order_count, vehicle_count = v_vehicle_count,
        total_sales = v_total_sales, total_profit = v_total_profit, avg_profit_margin = v_avg_profit_margin,
        current_stock = v_current_stock, avg_days_in_stock = v_avg_days_in_stock, inventory_value = v_inventory_value;

    COMMIT;
    SELECT p_year, p_month, v_order_count, v_total_sales;
END $$
DELIMITER ;
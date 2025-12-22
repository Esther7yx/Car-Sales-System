-- 使用数据库
USE car_sale_system;

-- 视图1：当前库存视图 (current_inventory)
-- 用途：实时查询在库车辆信息
-- 技术特点：多表JOIN，计算在库天数
-- 性能优化：使用覆盖索引加速查询
CREATE OR REPLACE VIEW current_inventory AS
SELECT 
    v.vin,
    c.model_name,
    c.year,
    c.engine_type,
    c.transmission,
    c.fuel_type,
    c.guide_price,
    v.purchase_price,
    v.sale_price,
    v.warehouse_location,
    v.purchase_date,
    DATEDIFF(CURRENT_DATE, v.purchase_date) AS days_in_stock,
    m.manufacturer_name,
    v.created_at,
    v.updated_at
FROM vehicle v
INNER JOIN car_model c ON v.model_id = c.model_id
INNER JOIN manufacturer m ON c.manufacturer_id = m.manufacturer_id
WHERE v.status = 'in_stock'
WITH CHECK OPTION;

-- 视图2：月度销售统计视图 (monthly_sales_statistics)
-- 用途：按月度统计销售业绩
-- 统计维度：订单数、车辆数、销售额、利润额
-- 时间处理：DATE_FORMAT函数分组
CREATE OR REPLACE VIEW monthly_sales_statistics AS
SELECT 
    DATE_FORMAT(so.create_time, '%Y-%m') AS sale_month,
    COUNT(DISTINCT so.sale_id) AS order_count,
    COUNT(sd.vin) AS vehicle_count,
    SUM(sd.subtotal) AS total_sales_amount,
    SUM(sd.subtotal - v.purchase_price) AS total_profit,
    ROUND(AVG(sd.subtotal - v.purchase_price), 2) AS avg_profit_per_vehicle,
    ROUND((SUM(sd.subtotal - v.purchase_price) / SUM(sd.subtotal)) * 100, 2) AS avg_profit_margin
FROM sale_order so
INNER JOIN sale_detail sd ON so.sale_id = sd.sale_id
INNER JOIN vehicle v ON sd.vin = v.vin
WHERE so.status = 'delivered' -- 只统计已交付的订单
GROUP BY DATE_FORMAT(so.create_time, '%Y-%m')
ORDER BY sale_month DESC
WITH CHECK OPTION;

-- 视图3：客户购车历史视图 (customer_purchase_history)
-- 用途：分析客户消费行为和价值
-- 分析指标：购车次数、累计金额、最近购车时间
-- 业务价值：支持客户分级和精准营销
CREATE OR REPLACE VIEW customer_purchase_history AS
SELECT 
    c.customer_id,
    c.customer_type,
    c.name,
    c.phone,
    c.email,
    c.credit_rating,
    COUNT(DISTINCT so.sale_id) AS purchase_count,
    SUM(sd.subtotal) AS total_purchase_amount,
    MAX(so.delivery_time) AS last_purchase_time,
    CASE 
        WHEN COUNT(DISTINCT so.sale_id) >= 3 AND SUM(sd.subtotal) >= 500000 THEN 'VIP'
        WHEN COUNT(DISTINCT so.sale_id) >= 2 AND SUM(sd.subtotal) >= 300000 THEN 'Premium'
        WHEN COUNT(DISTINCT so.sale_id) >= 1 THEN 'Regular'
        ELSE 'New'
    END AS customer_level
FROM customer c
LEFT JOIN sale_order so ON c.customer_id = so.customer_id AND so.status = 'delivered'
LEFT JOIN sale_detail sd ON so.sale_id = sd.sale_id
GROUP BY c.customer_id, c.customer_type, c.name, c.phone, c.email, c.credit_rating
WITH CHECK OPTION;

-- 视图4：车辆利润分析视图 (vehicle_profit_analysis)
-- 用途：分析各车型盈利能力
-- 计算逻辑：销售价-采购价=单辆利润，按车型聚合
-- 排序方式：按总利润降序排列
CREATE OR REPLACE VIEW vehicle_profit_analysis AS
SELECT 
    m.manufacturer_name,
    c.model_name,
    c.year,
    c.engine_type,
    c.fuel_type,
    COUNT(sd.vin) AS sale_count,
    SUM(sd.subtotal - v.purchase_price) AS total_profit,
    ROUND(AVG(sd.subtotal - v.purchase_price), 2) AS avg_profit_per_vehicle,
    ROUND((SUM(sd.subtotal - v.purchase_price) / SUM(sd.subtotal)) * 100, 2) AS avg_profit_margin
FROM car_model c
INNER JOIN manufacturer m ON c.manufacturer_id = m.manufacturer_id
INNER JOIN vehicle v ON c.model_id = v.model_id
INNER JOIN sale_detail sd ON v.vin = sd.vin
INNER JOIN sale_order so ON sd.sale_id = so.sale_id
WHERE so.status = 'delivered'
GROUP BY m.manufacturer_name, c.model_name, c.year, c.engine_type, c.fuel_type
ORDER BY total_profit DESC
WITH CHECK OPTION;

-- 视图5：库存周转分析视图 (inventory_turnover)
-- 用途：评估库存管理效率
-- 关键指标：周转率、平均在库天数、滞销车型识别
-- 业务指导：为采购决策提供数据支持
CREATE OR REPLACE VIEW inventory_turnover AS
SELECT 
    m.manufacturer_name,
    c.model_name,
    c.year,
    COUNT(CASE WHEN v.status = 'in_stock' THEN 1 END) AS current_stock_count,
    COUNT(CASE WHEN v.status = 'sold' AND so.delivery_time >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN 1 END) AS monthly_sales_count,
    CASE 
        WHEN COUNT(CASE WHEN v.status = 'in_stock' THEN 1 END) = 0 THEN NULL
        ELSE ROUND(COUNT(CASE WHEN v.status = 'sold' AND so.delivery_time >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN 1 END) / COUNT(CASE WHEN v.status = 'in_stock' THEN 1 END), 2)
    END AS turnover_rate,
    CASE 
        WHEN COUNT(CASE WHEN v.status = 'in_stock' THEN 1 END) = 0 THEN NULL
        ELSE ROUND(30 / (COUNT(CASE WHEN v.status = 'sold' AND so.delivery_time >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN 1 END) / COUNT(CASE WHEN v.status = 'in_stock' THEN 1 END)), 2)
    END AS avg_days_in_stock,
    CASE 
        WHEN COUNT(CASE WHEN v.status = 'in_stock' THEN 1 END) > 0 AND 
             COUNT(CASE WHEN v.status = 'sold' AND so.delivery_time >= DATE_SUB(CURRENT_DATE, INTERVAL 90 DAY) THEN 1 END) = 0 THEN '滞销'
        WHEN COUNT(CASE WHEN v.status = 'in_stock' THEN 1 END) > 0 AND 
             COUNT(CASE WHEN v.status = 'sold' AND so.delivery_time >= DATE_SUB(CURRENT_DATE, INTERVAL 90 DAY) THEN 1 END) < COUNT(CASE WHEN v.status = 'in_stock' THEN 1 END) * 0.5 THEN '慢销'
        ELSE '正常'
    END AS sales_status
FROM car_model c
INNER JOIN manufacturer m ON c.manufacturer_id = m.manufacturer_id
INNER JOIN vehicle v ON c.model_id = v.model_id
LEFT JOIN sale_detail sd ON v.vin = sd.vin
LEFT JOIN sale_order so ON sd.sale_id = so.sale_id
GROUP BY m.manufacturer_name, c.model_name, c.year
ORDER BY turnover_rate ASC
WITH CHECK OPTION;

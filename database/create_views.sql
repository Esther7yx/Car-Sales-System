USE car_sale_system;

-- ==========================================
-- 1. 仓库管理模块视图 (修复本次报错的关键)
-- ==========================================

-- 视图: 库存车辆统计 (对应 InventoryList 页面)
CREATE OR REPLACE VIEW view_inventory_summary AS
SELECT
    cm.model_id,
    cm.model_name,
    cm.year,
    m.manufacturer_name,
    cm.guide_price,
    COUNT(v.vin) AS stock_quantity,
    COALESCE(SUM(v.purchase_price), 0) AS total_stock_value
FROM car_model cm
         JOIN manufacturer m ON cm.manufacturer_id = m.manufacturer_id
         LEFT JOIN vehicle v ON cm.model_id = v.model_id AND v.status = 'in_stock'
GROUP BY cm.model_id, cm.model_name, cm.year, m.manufacturer_name, cm.guide_price
HAVING stock_quantity > 0;

-- 视图: 仓库明细 (对应 WarehouseDetailList 页面)
-- 仅展示未售出的车辆 (在库、预定、在途)
CREATE OR REPLACE VIEW view_warehouse_details AS
SELECT
    v.vin,
    cm.model_name,
    m.manufacturer_name,
    v.status,
    v.purchase_price,
    v.warehouse_location,
    v.purchase_date AS entry_date,
    DATEDIFF(CURRENT_DATE, v.purchase_date) AS days_in_stock
FROM vehicle v
         JOIN car_model cm ON v.model_id = cm.model_id
         JOIN manufacturer m ON cm.manufacturer_id = m.manufacturer_id
WHERE v.status != 'sold';

-- 视图: 进销存统计 (对应 WarehouseStats 页面)
-- 将采购单和销售单按月合并统计
CREATE OR REPLACE VIEW view_invoicing_stats AS
SELECT
    dates.month_str,
    COALESCE(p.purchase_count, 0) AS purchase_count,
    COALESCE(p.purchase_amount, 0) AS purchase_amount,
    COALESCE(s.sales_count, 0) AS sales_count,
    COALESCE(s.sales_amount, 0) AS sales_amount
FROM
    (
        SELECT DATE_FORMAT(create_time, '%Y-%m') AS month_str FROM purchase_order
        UNION
        SELECT DATE_FORMAT(create_time, '%Y-%m') AS month_str FROM sale_order
    ) dates
        LEFT JOIN (
        SELECT DATE_FORMAT(create_time, '%Y-%m') AS month, COUNT(*) AS purchase_count, SUM(total_amount) AS purchase_amount
        FROM purchase_order WHERE status != 'cancelled' GROUP BY month
    ) p ON dates.month_str = p.month
        LEFT JOIN (
        SELECT DATE_FORMAT(create_time, '%Y-%m') AS month, COUNT(*) AS sales_count, SUM(total_amount) AS sales_amount
        FROM sale_order WHERE status != 'cancelled' GROUP BY month
    ) s ON dates.month_str = s.month
ORDER BY dates.month_str DESC;


-- ==========================================
-- 2. 系统概览与报表模块视图 (保留原有功能)
-- ==========================================

-- 视图: 核心指标概览 (首页数字卡片)
CREATE OR REPLACE VIEW view_dashboard_summary AS
SELECT
    (SELECT COUNT(*) FROM vehicle WHERE status = 'in_stock') as total_stock_count,
    (SELECT COUNT(*) FROM vehicle WHERE status = 'sold') as total_sold_count,
    (SELECT COALESCE(SUM(total_amount), 0) FROM sale_order) as total_revenue,
    (SELECT COUNT(*) FROM customer) as total_customers;

-- 视图: 各品牌库存统计 (首页饼图)
CREATE OR REPLACE VIEW view_stock_stats AS
SELECT
    m.manufacturer_name,
    COUNT(v.vin) as stock_count,
    COALESCE(SUM(v.purchase_price), 0) as total_value
FROM manufacturer m
         LEFT JOIN car_model cm ON m.manufacturer_id = cm.manufacturer_id
         LEFT JOIN vehicle v ON cm.model_id = v.model_id AND v.status = 'in_stock'
GROUP BY m.manufacturer_id, m.manufacturer_name
HAVING stock_count > 0;

-- 视图: 销售趋势 (首页折线图)
CREATE OR REPLACE VIEW view_sales_trend AS
SELECT
    DATE_FORMAT(so.create_time, '%Y-%m') as sale_month,
    COUNT(DISTINCT so.sale_id) as order_count,
    SUM(sd.subtotal) as total_revenue,
    SUM(sd.subtotal - v.purchase_price) as total_profit
FROM sale_order so
         JOIN sale_detail sd ON so.sale_id = sd.sale_id
         JOIN vehicle v ON sd.vin = v.vin
WHERE so.create_time >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
  AND so.status = 'delivered'
GROUP BY sale_month
ORDER BY sale_month ASC;

-- 视图: 月度销售统计 (详细报表)
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
WHERE so.status = 'delivered'
GROUP BY DATE_FORMAT(so.create_time, '%Y-%m')
ORDER BY sale_month DESC;

-- 视图: 客户购车历史
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
GROUP BY c.customer_id, c.customer_type, c.name, c.phone, c.email, c.credit_rating;

-- 视图: 车辆利润分析
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
ORDER BY total_profit DESC;

-- 视图: 库存周转分析
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
ORDER BY turnover_rate ASC;
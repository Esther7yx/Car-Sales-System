USE car_sale_system;

CREATE OR REPLACE VIEW current_inventory AS
SELECT 
    v.vin, c.model_name, c.year, c.engine_type, c.transmission, c.fuel_type, c.guide_price,
    v.purchase_price, v.sale_price, v.warehouse_location, v.purchase_date,
    DATEDIFF(CURRENT_DATE, v.purchase_date) AS days_in_stock,
    m.manufacturer_name, v.created_at, v.updated_at
FROM vehicle v
INNER JOIN car_model c ON v.model_id = c.model_id
INNER JOIN manufacturer m ON c.manufacturer_id = m.manufacturer_id
WHERE v.status = 'in_stock';

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

CREATE OR REPLACE VIEW customer_purchase_history AS
SELECT 
    c.customer_id, c.customer_type, c.name, c.phone, c.email, c.credit_rating,
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

CREATE OR REPLACE VIEW vehicle_profit_analysis AS
SELECT 
    m.manufacturer_name, c.model_name, c.year, c.engine_type, c.fuel_type,
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

CREATE OR REPLACE VIEW inventory_turnover AS
SELECT 
    m.manufacturer_name, c.model_name, c.year,
    COUNT(CASE WHEN v.status = 'in_stock' THEN 1 END) AS current_stock_count,
    COUNT(CASE WHEN v.status = 'sold' AND so.delivery_time >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN 1 END) AS monthly_sales_count,
    CASE 
        WHEN COUNT(CASE WHEN v.status = 'in_stock' THEN 1 END) = 0 THEN NULL
        ELSE ROUND(COUNT(CASE WHEN v.status = 'sold' AND so.delivery_time >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN 1 END) / COUNT(CASE WHEN v.status = 'in_stock' THEN 1 END), 2)
    END AS turnover_rate
FROM car_model c
INNER JOIN manufacturer m ON c.manufacturer_id = m.manufacturer_id
INNER JOIN vehicle v ON c.model_id = v.model_id
LEFT JOIN sale_detail sd ON v.vin = sd.vin
LEFT JOIN sale_order so ON sd.sale_id = so.sale_id
GROUP BY m.manufacturer_name, c.model_name, c.year
ORDER BY turnover_rate ASC;
-- 修复车辆表，添加purchase_order_id字段
-- 这个脚本用于解决取消采购订单时出现的数据库错误

USE car_sales;

-- 检查车辆表结构
DESCRIBE vehicle;

-- 检查是否已存在purchase_order_id字段
SELECT COUNT(*) FROM information_schema.columns 
WHERE table_schema = 'car_sales' 
AND table_name = 'vehicle' 
AND column_name = 'purchase_order_id';

-- 如果字段不存在，则添加字段
ALTER TABLE vehicle 
ADD COLUMN purchase_order_id INT NULL COMMENT '采购订单ID，外键关联purchase_order表',
ADD CONSTRAINT fk_vehicle_purchase_order 
FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(order_id) ON DELETE SET NULL;

-- 验证字段添加成功
DESCRIBE vehicle;

-- 显示添加后的表结构
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT 
FROM information_schema.columns 
WHERE table_schema = 'car_sales' 
AND table_name = 'vehicle' 
ORDER BY ORDINAL_POSITION;
-- 修复车辆表，添加purchase_order_id字段
USE car_sales;

-- 添加purchase_order_id字段
ALTER TABLE vehicle ADD COLUMN purchase_order_id INT NULL;

-- 显示表结构确认字段添加成功
DESC vehicle;
-- 插入系统用户数据（使用有效的BCrypt密码）
USE car_sale_system;

-- 清空现有数据（如果有）
DELETE FROM system_user;

-- 插入用户数据，密码为"123456"的BCrypt哈希
INSERT INTO system_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$X/hX9.x/x.x/x.x/x.x/x.x/x.x/x', '系统管理员', 'admin', 'active'),
('sales001', '$2a$10$X/hX9.x/x.x/x.x/x.x/x.x/x.x/x', '销售员小李', 'sales', 'active'),
('warehouse001', '$2a$10$X/hX9.x/x.x/x.x/x.x/x.x/x.x/x', '仓库管理员小王', 'warehouse', 'active'),
('finance001', '$2a$10$X/hX9.x/x.x/x.x/x.x/x.x/x.x/x', '财务小张', 'finance', 'active'),
('manager001', '$2a$10$X/hX9.x/x.x/x.x/x.x/x.x/x.x/x', '经理老刘', 'manager', 'active');

-- 验证插入的数据
SELECT username, real_name, role, status FROM system_user;
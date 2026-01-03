-- 添加示例客户数据用于测试销售订单功能
INSERT IGNORE INTO customer (customer_id, customer_type, name, id_number, phone, email, address, credit_rating, created_at) VALUES
(3, 'individual', '王五', '110101199001011236', '13800138003', 'wangwu@example.com', '广州市天河区', 'A', NOW()),
(4, 'enterprise', 'ABC公司', '91310101MA1G9BXXXX', '13800138004', 'abc@company.com', '深圳市南山区', 'A', NOW()),
(5, 'enterprise', 'XYZ集团', '91310101MA1G9BYYYY', '13800138005', 'xyz@group.com', '杭州市西湖区', 'A', NOW());
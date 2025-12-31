-- 重置系统用户密码为实际可用的BCrypt哈希值
-- 所有用户的密码都设置为 "123456" 的BCrypt哈希

UPDATE system_user SET password = '123456' WHERE username = 'admin';
UPDATE system_user SET password = '123456' WHERE username = 'sales001';
UPDATE system_user SET password = '123456' WHERE username = 'warehouse001';
UPDATE system_user SET password = '123456' WHERE username = 'finance001';
UPDATE system_user SET password = '123456' WHERE username = 'manager001';

-- 验证更新结果
SELECT username, real_name, role, status, LENGTH(password) as password_length FROM system_user;
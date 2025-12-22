@echo off
REM 数据库初始化脚本
set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 9.5\bin\mysql.exe"
set DB_NAME=car_sale_system
set DB_USER=root
set DB_PASSWORD=123456
set DB_PORT=3307

echo 创建数据库...
%MYSQL_PATH% -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS %DB_NAME% DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo 创建表结构...
%MYSQL_PATH% -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_tables.sql"

echo 创建存储过程...
%MYSQL_PATH% -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_procedures.sql"

echo 创建触发器...
%MYSQL_PATH% -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_triggers.sql"

echo 创建视图...
%MYSQL_PATH% -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_views.sql"

echo 数据库初始化完成！
pause
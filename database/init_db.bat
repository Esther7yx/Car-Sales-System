@echo off
REM 数据库初始化脚本
REM 请确认下行路径是你电脑真实的 mysql.exe 路径
set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set DB_NAME=car_sale_system
set DB_USER=root
REM 请确认下行是你设置的真实密码
set DB_PASSWORD=123456
set DB_PORT=3306

echo 1. Re-creating database (DROP and CREATE)...
REM 使用 -e 执行单行命令：如果存在则删除，然后重新创建
%MYSQL_PATH% --default-character-set=utf8mb4 -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% -e "DROP DATABASE IF EXISTS %DB_NAME%; CREATE DATABASE %DB_NAME% DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo 2. Creating table structure...
REM 注意：create_tables.sql 负责建表，不需要再负责建库
%MYSQL_PATH% --default-character-set=utf8mb4 -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_tables.sql"

echo 3. Creating stored procedures...
%MYSQL_PATH% --default-character-set=utf8mb4 -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_procedures.sql"

echo 4. Creating triggers...
%MYSQL_PATH% --default-character-set=utf8mb4 -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_triggers.sql"

echo 5. Creating views...
%MYSQL_PATH% --default-character-set=utf8mb4 -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_views.sql"

echo Database initialization completed!
pause


@echo off
REM 数据库初始化脚本
REM 请确认下行路径是你电脑真实的 mysql.exe 路径
set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set DB_NAME=car_sale_system
set DB_USER=root
REM 请确认下行是你设置的真实密码
set DB_PASSWORD=Zyx.0507.xyz
set DB_PORT=3306

echo Creating database...
%MYSQL_PATH% --default-character-set=utf8mb4 -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS %DB_NAME% DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo Creating table structure...
%MYSQL_PATH% --default-character-set=utf8mb4 -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_tables.sql"

echo Creating stored procedures...
%MYSQL_PATH% --default-character-set=utf8mb4 -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_procedures.sql"

echo Creating triggers...
%MYSQL_PATH% --default-character-set=utf8mb4 -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_triggers.sql"

echo Creating views...
%MYSQL_PATH% --default-character-set=utf8mb4 -h localhost -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% < "create_views.sql"

echo Database initialization completed!
pause
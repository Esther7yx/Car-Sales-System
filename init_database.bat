@echo off
echo ========================================
echo   汽车销售管理系统 - 数据库初始化
echo ========================================
echo.

REM 检查MySQL客户端
echo [1/4] 检查MySQL客户端...
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ MySQL客户端未安装
    echo 请先安装MySQL Community Server
    echo 下载地址：https://dev.mysql.com/downloads/mysql/
    pause
    exit /b 1
)
echo ✅ MySQL客户端正常

REM 检查数据库连接
echo [2/4] 检查数据库连接...
mysql -u root -p123456 -e "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 数据库连接失败
    echo 可能的原因：
    echo   - MySQL服务未启动
    echo   - root密码不是123456
    echo   - MySQL运行在非3306端口
    echo.
    echo 请检查并修改backend/src/main/resources/application.properties中的数据库配置
    pause
    exit /b 1
)
echo ✅ 数据库连接正常

REM 创建数据库
echo [3/4] 创建数据库...
mysql -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS car_sale_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if %errorlevel% neq 0 (
    echo ❌ 创建数据库失败
    pause
    exit /b 1
)
echo ✅ 数据库创建成功

REM 导入表结构
echo [4/4] 导入表结构和视图...
mysql -u root -p123456 car_sale_system < database\create_tables.sql
if %errorlevel% neq 0 (
    echo ❌ 导入表结构失败
    pause
    exit /b 1
)

mysql -u root -p123456 car_sale_system < database\create_views.sql
if %errorlevel% neq 0 (
    echo ❌ 导入视图失败
    pause
    exit /b 1
)

echo ✅ 数据库初始化完成
echo.
echo 📊 数据库信息：
echo   数据库名：car_sale_system
echo   字符集：utf8mb4
echo   排序规则：utf8mb4_unicode_ci
echo.
echo ⚠️ 注意事项：
echo   - 如果MySQL运行在非3306端口，请修改脚本中的端口号
echo   - 如果root密码不是123456，请修改脚本中的密码
echo   - 首次运行系统前必须执行此初始化脚本
echo.
pause
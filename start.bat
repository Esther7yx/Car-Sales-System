@echo off
echo ========================================
echo   汽车销售管理系统 - 一键启动脚本
echo ========================================
echo.

REM 检查Java环境
echo [1/6] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java未安装，请先安装JDK 17+
    echo 下载地址：https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
echo ✅ Java环境正常

REM 检查Maven环境
echo [2/6] 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven未安装，请先安装Maven 3.6+
    echo 下载地址：https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo ✅ Maven环境正常

REM 检查Node.js环境
echo [3/6] 检查Node.js环境...
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js未安装，请先安装Node.js 18+
    echo 下载地址：https://nodejs.org/
    pause
    exit /b 1
)
echo ✅ Node.js环境正常

REM 检查MySQL服务
echo [4/6] 检查MySQL服务...
netstat -ano | findstr :3307 >nul
if %errorlevel% neq 0 (
    echo ⚠️ MySQL服务未运行在3307端口
    echo 请确保MySQL服务已启动
    echo 如果MySQL运行在其他端口，请修改backend/src/main/resources/application.properties
    echo.
    set /p continue="是否继续启动？(y/n): "
    if /i not "%continue%"=="y" (
        exit /b 1
    )
) else (
    echo ✅ MySQL服务正常
)

REM 启动后端服务
echo [5/6] 启动后端服务...
cd backend
start "汽车销售管理系统 - 后端服务" cmd /k "mvn spring-boot:run"
cd ..

echo 等待后端服务启动...
timeout /t 15 /nobreak >nul

REM 启动前端服务
echo [6/6] 启动前端服务...
cd frontend
start "汽车销售管理系统 - 前端服务" cmd /k "npm run dev"
cd ..

echo.
echo ========================================
echo           系统启动完成！
echo ========================================
echo.
echo 📍 访问地址：
echo   前端界面：http://localhost:5173
echo   后端API：http://localhost:8080/api
echo.
echo 🔑 默认登录账号：
echo   用户名：user
echo   密码：123456
echo.
echo ⚠️ 注意事项：
echo   - 确保MySQL服务已启动
echo   - 首次运行请先执行数据库初始化
echo   - 后端服务启动需要约30秒
echo.
pause
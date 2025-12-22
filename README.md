# 汽车销售管理系统

一个基于Vue3 + Spring Boot + MySQL的汽车销售管理全栈项目。

## 🎯 团队分工

本项目采用四人团队协作开发模式，各角色职责明确：

### 👨‍💼 业务分析师（产品经理）
**职责范围**：需求分析、功能设计、用户体验
- 负责需求文档和原型设计
- 定义业务流程和用户交互
- 验收测试和功能验证

### 🗄️ 数据架构师  
**职责范围**：数据库设计、数据模型、API接口
- 设计数据库表结构和关系
- 定义实体类和DTO对象
- 制定API接口规范

### 💾 数据库管理员
**职责范围**：数据库管理、数据安全、性能监控
- 数据库部署和配置
- 数据安全策略制定
- 性能监控和优化

### 👨‍💻 软件开发工程师
**职责范围**：前后端开发、系统集成、测试部署
- 实现业务逻辑和API接口
- 开发用户界面和交互功能
- 系统集成和部署运维

📖 **详细分工说明请查看：**[团队分工指南.md](./团队分工指南.md)

## 技术栈

### 前端
- **Vue 3** - 渐进式JavaScript框架
- **Element Plus** - UI组件库
- **Vite** - 构建工具
- **Axios** - HTTP客户端
- **Vue Router** - 路由管理
- **Pinia** - 状态管理

### 后端
- **Spring Boot 3.x** - Java后端框架
- **Spring Security** - 安全认证
- **MyBatis Plus** - ORM框架
- **MySQL 8.0** - 数据库
- **Maven** - 项目管理工具

## 项目结构

```
sale/
├── backend/                 # Spring Boot后端
│   ├── src/main/java/      # Java源代码
│   ├── src/main/resources/ # 配置文件
│   └── pom.xml            # Maven配置
├── frontend/               # Vue3前端
│   ├── src/               # Vue源代码
│   ├── package.json       # 依赖配置
│   └── vite.config.js     # Vite配置
├── database/              # 数据库脚本
│   ├── create_tables.sql  # 表结构
│   └── create_views.sql   # 视图定义
└── README.md              # 项目说明
```

## 环境要求

- **JDK 17+** - Java开发环境
- **Maven 3.6+** - Java项目管理
- **Node.js 18+** - 前端运行环境
- **MySQL 8.0+** - 数据库服务

## 快速开始

### 1. 环境准备

#### 安装Java
```bash
# 验证Java安装
java -version
```

#### 安装Maven
1. 下载 [Maven](https://maven.apache.org/download.cgi)
2. 解压到 `C:\Program Files\Maven\`
3. 配置环境变量：
   ```
   M2_HOME=C:\Program Files\Maven\apache-maven-3.9.6
   PATH=%PATH%;%M2_HOME%\bin
   ```
4. 验证安装：`mvn -version`

#### 安装Node.js
1. 下载 [Node.js LTS](https://nodejs.org/)
2. 运行安装程序
3. 验证安装：`node -v` 和 `npm -v`

#### 安装MySQL
1. 下载 [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
2. 安装并设置root密码为 `123456`
3. 启动MySQL服务

### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS car_sale_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入表结构
mysql -u root -p123456 car_sale_system < database/create_tables.sql

# 导入视图定义
mysql -u root -p123456 car_sale_system < database/create_views.sql
```

### 3. 后端启动

```bash
# 进入后端目录
cd backend

# 编译打包
mvn clean package -DskipTests

# 运行应用
java -jar target/car-sale-backend-1.0.0.jar
```

后端服务将运行在：**http://localhost:8080/api**

### 4. 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将运行在：**http://localhost:5173**

## 一键启动脚本

创建 `start.bat` 文件：

```batch
@echo off
echo 启动汽车销售管理系统...

REM 检查MySQL服务
echo 检查MySQL服务...
netstat -ano | findstr :3307 >nul
if errorlevel 1 (
    echo MySQL服务未运行，请先启动MySQL服务
    pause
    exit /b 1
)

REM 启动后端服务
echo 启动后端服务...
cd backend
start "后端服务" cmd /k "mvn spring-boot:run"
cd ..

REM 等待后端启动
timeout /t 10 /nobreak >nul

REM 启动前端服务
echo 启动前端服务...
cd frontend
start "前端服务" cmd /k "npm run dev"
cd ..

echo 系统启动完成！
echo 后端服务：http://localhost:8080/api
echo 前端服务：http://localhost:5173
pause
```

## 功能模块

### 用户管理
- 用户登录/登出
- 权限控制
- 用户信息管理

### 汽车管理
- 汽车信息录入
- 库存管理
- 汽车查询

### 销售管理
- 销售订单管理
- 客户信息管理
- 销售统计

### 厂商管理
- 厂商信息维护
- 合作状态管理

## 默认账号

- **用户名**: `user`
- **密码**: `123456`

## 开发指南

### 后端开发
```bash
# 开发模式启动
cd backend
mvn spring-boot:run

# 打包发布
mvn clean package -DskipTests
```

### 前端开发
```bash
# 开发模式启动
cd frontend
npm run dev

# 构建生产版本
npm run build
```

## 常见问题

### 端口占用
```bash
# 查看端口占用
netstat -ano | findstr :8080

# 终止进程
taskkill /PID <进程ID> /F
```

### 数据库连接失败
检查 `backend/src/main/resources/application.properties` 中的数据库配置。

### 依赖下载慢
使用国内镜像源：

**Maven镜像**（在 `settings.xml` 中配置）：
```xml
<mirror>
    <id>aliyunmaven</id>
    <mirrorOf>*</mirrorOf>
    <name>阿里云公共仓库</name>
    <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

**npm镜像**：
```bash
npm config set registry https://registry.npmmirror.com/
```

## 部署说明

### 生产环境部署
1. 修改 `application.properties` 中的数据库配置
2. 构建前端：`npm run build`
3. 构建后端：`mvn clean package -DskipTests`
4. 部署JAR包到服务器

### Docker部署（可选）
```dockerfile
FROM openjdk:17-jdk-slim
COPY backend/target/car-sale-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 贡献指南

1. Fork 项目
2. 创建功能分支：`git checkout -b feature/新功能`
3. 提交更改：`git commit -am '添加新功能'`
4. 推送分支：`git push origin feature/新功能`
5. 提交 Pull Request

## 许可证

MIT License

## 联系方式

如有问题，请联系项目维护团队。
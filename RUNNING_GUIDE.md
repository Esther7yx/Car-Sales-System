# 汽车销售管理系统运行指南

## 一、环境准备

### 1. 安装JDK（Java Development Kit）

- 版本要求：JDK 11 或以上
- 下载地址：https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
- 安装步骤：
  1. 下载对应操作系统的JDK安装包
  2. 运行安装程序，按照提示完成安装
  3. 配置环境变量：
     - JAVA_HOME: 指向JDK安装目录
     - PATH: 添加 %JAVA_HOME%\bin（Windows）或 $JAVA_HOME/bin（Linux/Mac）
  4. 验证安装：在命令行输入 `java -version`，应显示JDK版本信息

### 2. 安装Maven

- 版本要求：Maven 3.6 或以上
- 下载地址：https://maven.apache.org/download.cgi
- 安装步骤：
  1. 下载Maven压缩包
  2. 解压到指定目录
  3. 配置环境变量：
     - MAVEN_HOME: 指向Maven解压目录
     - PATH: 添加 %MAVEN_HOME%\bin（Windows）或 $MAVEN_HOME/bin（Linux/Mac）
  4. 验证安装：在命令行输入 `mvn -v`，应显示Maven版本信息

### 3. 安装Node.js和npm

- 版本要求：Node.js 14 或以上
- 下载地址：https://nodejs.org/en/download/
- 安装步骤：
  1. 下载对应操作系统的Node.js安装包
  2. 运行安装程序，按照提示完成安装
  3. 验证安装：在命令行输入 `node -v` 和 `npm -v`，应显示Node.js和npm版本信息

### 4. 安装MySQL

- 版本要求：MySQL 5.7 或以上
- 下载地址：https://dev.mysql.com/downloads/mysql/
- 安装步骤：
  1. 下载MySQL安装包
  2. 运行安装程序，按照提示完成安装
  3. 设置root用户密码（建议使用123456以匹配当前配置）
  4. 启动MySQL服务
  5. 验证安装：在命令行输入 `mysql -u root -p`，输入密码后应进入MySQL命令行界面

## 二、数据库初始化

### 1. 创建数据库

```sql
CREATE DATABASE car_sale_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 执行数据库脚本

按顺序执行以下脚本文件：

1. `database/create_tables.sql` - 创建表结构
2. `database/create_views.sql` - 创建视图
3. `database/create_procedures.sql` - 创建存储过程
4. `database/create_triggers.sql` - 创建触发器

执行方式：

- 方式一：在MySQL命令行中执行

  ```sql
  USE car_sale_system;
  SOURCE c:/Users/HP/Desktop/sale/database/create_tables.sql;
  SOURCE c:/Users/HP/Desktop/sale/database/create_views.sql;
  SOURCE c:/Users/HP/Desktop/sale/database/create_procedures.sql;
  SOURCE c:/Users/HP/Desktop/sale/database/create_triggers.sql;
  ```
- 方式二：使用MySQL Workbench或Navicat等图形化工具执行

## 三、后端运行

### 1. 配置检查

检查 `backend/src/main/resources/application.properties` 文件中的数据库连接配置：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/car_sale_system?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456
```

如果您的MySQL密码不是123456，请修改 `spring.datasource.password` 配置项。

### 2. 编译和运行

**方法一：使用Maven命令**

1. 进入后端目录：

   ```bash
   cd c:/Users/HP/Desktop/sale/backend
   ```
2. 编译项目：

   ```bash
   mvn clean compile
   ```
3. 运行项目：

   ```bash
   mvn spring-boot:run
   ```

**方法二：使用IDE**

1. 使用IntelliJ IDEA或Eclipse等IDE打开后端项目
2. 等待Maven依赖下载完成
3. 运行 `com.carsale.CarSaleApplication` 类

### 3. 验证后端启动

后端启动成功后，应显示类似以下信息：

```
2023-xx-xx xx:xx:xx.xxx  INFO 12345 --- [           main] com.carsale.CarSaleApplication           : Started CarSaleApplication in x.xxx seconds (JVM running for x.xxx)
```

此时，后端API可以通过 `http://localhost:8080/api` 访问。

## 四、前端运行

### 1. 安装依赖

1. 进入前端目录：

   ```bash
   cd c:/Users/HP/Desktop/sale/frontend
   ```
2. 安装依赖：

   ```bash
   npm install
   ```

### 2. 运行项目

**方法一：开发模式**

```bash
npm run dev
```

**方法二：生产构建**

```bash
npm run build
npm run preview
```

### 3. 验证前端启动

前端启动成功后，应显示类似以下信息：

```
  VITE v4.x.x  ready in x ms
  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
  ➜  press h to show help
```

此时，可以通过浏览器访问 `http://localhost:3000` 查看前端页面。

## 五、功能测试

### 1. 登录系统

- 访问 `http://localhost:3000`
- 输入用户名和密码登录系统（默认用户名和密码需要在数据库中初始化）

### 2. 测试主要功能

- **厂商管理**：添加、编辑、删除厂商信息，查看厂商列表
- **车型管理**：添加、编辑、删除车型信息，按厂商筛选车型
- **车辆管理**：添加、编辑、删除车辆信息，更新车辆状态（在库、已售、预定）
- **销售管理**：创建销售订单，查看销售记录
- **库存管理**：查看库存状态，管理车辆入库和出库

## 六、常见问题及解决方案

### 1. 数据库连接失败

- 检查MySQL服务是否启动
- 检查数据库连接配置是否正确
- 检查防火墙是否允许MySQL端口（默认3306）访问

### 2. 后端启动失败

- 检查JDK和Maven版本是否符合要求
- 检查数据库是否初始化成功
- 检查依赖是否下载完成

### 3. 前端启动失败

- 检查Node.js和npm版本是否符合要求
- 检查依赖是否安装成功
- 检查前端配置文件是否正确

### 4. 前后端接口调用失败

- 检查后端是否正常启动
- 检查CORS配置是否正确
- 检查API路径是否正确

## 七、项目结构说明

### 后端结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── carsale/
│   │   │           ├── controller/    # 控制器层
│   │   │           ├── entity/        # 实体类
│   │   │           ├── mapper/        # Mapper接口
│   │   │           ├── service/       # 服务层
│   │   │           ├── utils/         # 工具类
│   │   │           └── CarSaleApplication.java  # 应用入口
│   │   └── resources/
│   │       ├── mapper/               # MyBatis XML Mapper文件
│   │       └── application.properties # 配置文件
│   └── test/                         # 测试代码
└── pom.xml                           # Maven配置文件
```

### 前端结构

```
frontend/
├── src/
│   ├── router/                       # 路由配置
│   ├── store/                        # 状态管理
│   ├── utils/                        # 工具函数
│   ├── views/                        # 视图组件
│   ├── App.vue                       # 根组件
│   └── main.js                       # 应用入口
├── index.html                        # HTML模板
├── package.json                      # 项目配置
└── vite.config.js                    # Vite配置
```

## 八、技术栈说明

### 后端

- **Spring Boot**：Java企业级应用开发框架
- **MyBatis Plus**：MyBatis增强工具，简化ORM操作
- **MySQL**：关系型数据库
- **Lombok**：简化Java代码的工具库

### 前端

- **Vue 3**：前端框架
- **Vue Router**：路由管理
- **Pinia**：状态管理
- **Element Plus**：UI组件库
- **Axios**：HTTP客户端
- **Vite**：构建工具

## 九、开发建议

1. **代码规范**

   - 遵循Java和Vue的代码规范
   - 使用统一的命名风格
   - 添加必要的注释
2. **性能优化**

   - 使用索引优化数据库查询
   - 合理使用缓存
   - 减少不必要的网络请求
3. **安全措施**

   - 加强输入验证
   - 使用HTTPS协议
   - 定期更新依赖库
4. **扩展建议**

   - 添加用户权限管理
   - 实现数据统计和报表功能
   - 集成消息通知系统
   - 开发移动端应用

如果在运行过程中遇到任何问题，请参考本指南的常见问题及解决方案部分，或查阅相关技术文档。祝您使用愉快！

# Table 系统说明文档

## 项目简介

Table 系统是一个基于 Spring Boot 框架开发的后台管理系统，主要实现了用户管理、积分管理、操作日志等功能。项目采用分层架构，结构清晰，易于维护和扩展。

## 技术栈

- **后端框架**：Spring Boot 3
- **ORM 框架**：MyBatis-Plus
- **安全框架**：Sa-Token
- **数据库**：MySQL 8
- **构建工具**：Maven

## 主要功能

- 用户登录、注册、信息管理
- 用户积分管理
- 操作日志记录与查询
- 全局异常处理
- 跨域资源共享（CORS）配置
- 数据库初始化与配置

## 目录结构说明

```plaintext
table/
├── mvnw, mvnw.cmd         # Maven Wrapper 脚本
├── pom.xml                # Maven 项目配置文件
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/wzz/table/
│   │   │       ├── auth/                # 权限相关实现
│   │   │       │   └── StpInterfaceImpl.java
│   │   │       ├── config/              # 配置类
│   │   │       ├── controller/          # 控制器（接口层）
│   │   │       ├── DTO/                 # 数据传输对象
│   │   │       ├── exception/           # 全局异常处理
│   │   │       ├── mapper/              # MyBatis-Plus Mapper 接口
│   │   │       ├── pojo/                # 实体类
│   │   │       ├── service/             # 业务接口与实现
│   │   │       ├── utils/               # 工具类
│   │   │       └── TableApplication.java# 启动类
│   │   └── resources/
│   │       ├── application.yaml         # 配置文件
│   │       ├── static/                  # 静态资源
│   │       └── templates/               # 模板文件
│   └── test/
│       └── java/com/wzz/table/          # 测试代码
└── target/                              # 编译输出目录
```

## 主要模块说明

### 1. controller（控制器层）
- `PointsUsersController.java`：积分用户相关接口
- `OperationlogController.java`：操作日志相关接口
- `userController.java`：用户相关接口

### 2. service（服务层）
- `UserService.java`、`PointsUsersService.java`、`OperationlogService.java`：对应的业务接口
- `impl/`：对应的业务实现类

### 3. mapper（数据访问层）
- `UserMapper.java`、`PointsUsersMapper.java`、`OperationlogMapper.java`：MyBatis-Plus Mapper 接口

### 4. pojo（实体类）
- `User.java`、`PointsUsers.java`、`Operationlog.java`：数据库表对应的实体类

### 5. DTO（数据传输对象）
- `UserLoginDto.java`：用户登录数据传输对象
- `UserUpdatePasswordDto.java`：用户修改密码数据传输对象
- `Result.java`：统一返回结果封装

### 6. config（配置类）
- `CorsConfig.java`：跨域配置
- `DatabaseInitConfig.java`：数据库初始化配置
- `MybatisPlusConfig.java`：MyBatis-Plus 配置
- `SaTokenConfigure.java`：Sa-Token 配置

### 7. auth（权限相关）
- `StpInterfaceImpl.java`：Sa-Token 权限接口实现

### 8. exception（异常处理）
- `GlobalExceptionHandler.java`：全局异常处理

### 9. utils（工具类）
- `OperationlogUtil.java`：操作日志工具类

## 启动方式

1. **环境准备**
    - 安装 JDK 17 或以上
    - 安装 Maven
    - 配置好数据库（如 MySQL），并在 `application.yaml` 中修改数据库连接信息

2. **编译与运行**
   ```bash
   ./mvnw clean package
   java -jar target/table-*.jar
   ```
   或直接在 IDE（如 IDEA）中运行 `TableApplication.java`

3. **访问接口**
    - 默认端口为 8080（可在 `application.yaml` 配置）
    - 通过 Postman 或前端页面进行接口测试

## 配置文件说明

- `application.yaml`：包含数据库、端口、MyBatis-Plus、Sa-Token 等相关配置

## 其他说明

- 日志、异常、权限等均有统一处理，便于后续扩展
- 代码结构清晰，便于团队协作和维护
# 医院预约系统

## 项目简介

医院预约系统是一个前后端分离的Web应用，旨在为患者提供便捷的在线挂号、预约管理服务，同时为医院管理人员提供完善的后台管理功能。系统支持用户注册登录、科室查询、医生查询、在线预约、预约管理、医疗导诊等核心功能。

## 功能特性

### 用户端功能
- 用户注册与登录
- 个人信息管理
- 科室查询与浏览
- 医生查询与详情查看
- 在线预约挂号
- 预约记录管理
- 医疗导诊服务

### 管理端功能
- 系统仪表盘
- 用户管理
- 科室管理
- 医生管理
- 预约管理

## 技术栈

### 后端技术
- Spring Boot 2.6.13
- MyBatis Plus 3.5.2
- MySQL 8.0.33
- Redis
- JWT 认证
- Hutool 工具库

### 前端技术
- Vue.js 2.6.14
- Vue Router 3.5.1
- Axios
- Element UI 2.15.6

## 项目结构

```
hospital/
├── backend/                 # 后端项目
│   ├── pom.xml              # Maven 依赖配置
│   ├── src/                 # 源代码目录
│   │   ├── main/java/com/hospital/  # Java 源代码
│   │   └── main/resources/          # 资源文件
│   │       └── init.sql      # 数据库初始化脚本
│   └── target/              # 构建输出目录
└── frontend/                # 前端项目
    ├── .eslintrc.js         # ESLint 配置
    ├── package.json         # NPM 依赖配置
    ├── src/                 # 源代码目录
    │   ├── api/             # API 请求封装
    │   ├── assets/          # 静态资源
    │   ├── components/      # 公共组件
    │   ├── router/          # 路由配置
    │   ├── views/           # 页面组件
    │   └── main.js          # 入口文件
    └── dist/                # 构建输出目录
```

## 环境要求

### 后端环境
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 前端环境
- Node.js 14+
- npm 6+

## 安装与部署

### 后端部署

1. 克隆项目代码
   ```bash
   git clone <项目仓库地址>
   cd hospital/backend
   ```

2. 配置数据库
   - 创建数据库：`hospital_appointment`
   - 执行初始化脚本：`backend/src/main/resources/init.sql`

3. 修改配置文件（根据实际情况调整）
   - 数据库连接信息
   - Redis连接信息
   - JWT密钥配置

4. 打包项目
   ```bash
   mvn clean package
   ```

5. 运行项目
   ```bash
   java -jar target/hospital-appointment-system-1.0.0.jar
   ```

### 前端部署

1. 进入前端目录
   ```bash
   cd hospital/frontend
   ```

2. 安装依赖
   ```bash
   npm install
   ```

3. 开发环境运行
   ```bash
   npm run serve
   ```

4. 生产环境构建
   ```bash
   npm run build
   ```

5. 部署构建产物
   - 将 `dist` 目录部署到 Web 服务器（如 Nginx、Apache）
   - 配置反向代理，将 API 请求转发到后端服务

## 使用说明

### 访问系统
- 前端默认地址：`http://localhost:8080`
- 后端 API 地址：`http://localhost:8081`

### 管理员账号
- 初始管理员账号可通过数据库脚本创建
- 默认用户名：`admin`
- 默认密码：`123456`（请登录后及时修改）

### 系统功能说明

#### 患者端
1. **首页**：系统介绍、快捷服务入口
2. **科室介绍**：浏览医院各科室信息
3. **医生团队**：查看医生列表和详情
4. **在线预约**：选择科室和医生进行预约
5. **预约记录**：查看和管理个人预约信息
6. **医疗导诊**：获取就诊建议和指导

#### 管理员端
1. **仪表盘**：系统概览和统计信息
2. **用户管理**：管理系统用户账号
3. **科室管理**：管理医院科室信息
4. **医生管理**：管理医生信息
5. **预约管理**：查看和处理所有预约请求

## 系统架构

### 后端架构
- 采用 Spring Boot 分层架构
  - Controller 层：处理HTTP请求
  - Service 层：业务逻辑处理
  - Mapper 层：数据访问层
  - Model 层：实体类定义

### 前端架构
- 基于 Vue.js 的单页应用
- 采用 Vuex 进行状态管理
- 使用 Vue Router 实现路由控制
- 集成 Element UI 组件库

## 安全机制

1. **用户认证**：基于 JWT 的身份认证
2. **权限控制**：基于角色的访问控制（RBAC）
3. **数据加密**：密码加密存储，敏感数据传输加密
4. **防SQL注入**：使用参数化查询
5. **跨域请求处理**：配置 CORS 策略

## 注意事项

1. 系统运行需要确保 MySQL 和 Redis 服务正常启动
2. 生产环境部署前请修改默认密码和密钥
3. 系统日志配置可根据实际需求进行调整
4. 建议配置定期数据备份策略

## 许可证

本项目为内部系统，仅供医院内部使用。

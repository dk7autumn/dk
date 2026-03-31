# 水产店销售系统 - 重构版

## 项目架构

### 后端技术栈
- Spring Boot 3.2.0
- Spring Security + JWT 认证
- Spring Data JPA
- MySQL 8.0
- Lombok

### 前端技术栈
- Vue 3
- Vite 5
- Element Plus
- Pinia 状态管理
- Vue Router 4

## 项目结构

```
sale-system/
├── src/main/java/com/dk/salesystem/
│   ├── config/          # 配置类
│   ├── controller/      # REST API 控制器
│   ├── dto/             # 数据传输对象
│   ├── entity/          # 实体类
│   ├── repository/      # 数据访问层
│   ├── security/        # 安全相关（JWT 过滤器）
│   ├── service/         # 业务逻辑层
│   └── util/            # 工具类
├── src/main/resources/
│   ├── db/
│   │   └── schema.sql   # 数据库初始化脚本
│   └── application.yml  # 应用配置
├── frontend/            # Vue3 前端项目
│   ├── src/
│   │   ├── api/         # API 封装
│   │   ├── components/  # 公共组件
│   │   ├── router/      # 路由配置
│   │   ├── store/       # Pinia 状态管理
│   │   ├── views/       # 页面组件
│   │   └── main.js      # 入口文件
│   └── package.json
└── pom.xml
```

## 快速开始

### 1. 数据库准备

1. 安装 MySQL 8.0
2. 创建数据库并执行初始化脚本：

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

或手动执行 `src/main/resources/db/schema.sql` 中的 SQL 语句。

### 2. 后端启动

1. 修改 `src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sale_system?...
    username: your_username
    password: your_password
```

2. 启动后端服务：

```bash
cd /root/sale_system
mvn spring-boot:run
```

后端服务运行在 `http://localhost:8080`

### 3. 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务运行在 `http://localhost:5173`

## 默认账号

- 用户名：`admin`
- 密码：`admin123`

## API 接口

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/info` - 获取当前用户信息

### 用户管理
- `GET /api/user` - 获取用户列表
- `GET /api/user/{id}` - 获取用户详情
- `POST /api/user` - 创建用户
- `PUT /api/user/{id}` - 更新用户
- `DELETE /api/user/{id}` - 删除用户

### 角色管理
- `GET /api/role` - 获取角色列表
- `GET /api/role/{id}` - 获取角色详情
- `POST /api/role` - 创建角色
- `PUT /api/role/{id}` - 更新角色
- `DELETE /api/role/{id}` - 删除角色

### 权限管理
- `GET /api/permission` - 获取权限列表
- `GET /api/permission/menu` - 获取菜单树
- `POST /api/permission` - 创建权限
- `PUT /api/permission/{id}` - 更新权限
- `DELETE /api/permission/{id}` - 删除权限

### 鱼类管理
- `GET /api/fish` - 获取鱼类列表
- `POST /api/fish` - 创建鱼类
- `PUT /api/fish/{id}` - 更新鱼类
- `DELETE /api/fish/{id}` - 删除鱼类

### 销售管理
- `GET /api/sale` - 获取销售记录列表
- `POST /api/sale` - 创建销售记录
- `DELETE /api/sale/{id}` - 删除销售记录
- `GET /api/sale/stats/daily` - 每日统计
- `GET /api/sale/stats/weekly` - 每周统计
- `GET /api/sale/stats/monthly` - 每月统计
- `GET /api/sale/stats/quarterly` - 每季度统计
- `GET /api/sale/stats/yearly` - 每年统计

## 权限设计

### 角色
- **ADMIN** - 超级管理员：拥有所有权限
- **USER** - 普通用户：基础业务权限
- **GUEST** - 访客：只读权限

### 权限编码规范
- 菜单权限：`module:menu`
- 按钮权限：`module:action`
- 接口权限：`module:resource:action`

例如：
- `system:user:menu` - 用户管理菜单
- `system:user:add` - 用户新增按钮
- `fish:query` - 鱼类查询

## 注意事项

1. 首次启动前必须先执行数据库初始化脚本
2. JWT token 有效期为 24 小时
3. 前端开发时确保后端服务已启动
4. 生产环境部署时请修改 JWT 密钥和数据库密码

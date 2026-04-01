# 水产店销售系统

## 项目架构

### 后端技术栈
- Spring Boot 3.2.0
- Spring Security + JWT 认证
- Spring Data JPA
- MySQL 8.0
- Lombok
- MyBatis Plus

### 前端技术栈
- Vue 3
- Vite 5
- Element Plus
- Pinia 状态管理
- Vue Router 4
- ECharts 图表

## 快速开始

### 方式一：Docker 部署（推荐）

```bash
# 一键部署
./auto-deploy.sh
```

部署完成后：
- 后端地址：http://localhost:8080
- 前端地址：http://localhost:5173
- 默认账号：`admin` / `admin123`

### 方式二：本地开发

#### 1. 数据库准备

安装 MySQL 8.0，然后执行初始化脚本：

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

#### 2. 后端启动

```bash
cd /root/sale_system
mvn spring-boot:run
```

后端服务运行在 `http://localhost:8080`

#### 3. 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务运行在 `http://localhost:5173`

## 默认账号

- 用户名：`admin`
- 密码：`admin123`

## 项目结构

```
sale-system/
├── src/main/java/com/dk/salesystem/
│   ├── config/          # 配置类
│   ├── controller/      # REST API 控制器
│   ├── dto/             # 数据传输对象
│   ├── entity/          # 实体类
│   ├── mapper/          # MyBatis Mapper
│   ├── repository/      # 数据访问层
│   ├── security/        # 安全相关（JWT 过滤器）
│   ├── service/         # 业务逻辑层
│   └── util/            # 工具类
├── src/main/resources/
│   ├── db/
│   │   └── schema.sql   # 数据库初始化脚本
│   └── application.yml  # 应用配置
├── frontend/            # Vue3 前端项目
│   ├── dist/            # 生产构建输出
│   ├── src/
│   │   ├── api/         # API 封装
│   │   ├── components/  # 公共组件
│   │   ├── router/      # 路由配置
│   │   ├── store/       # Pinia 状态管理
│   │   ├── views/       # 页面组件
│   │   └── main.js      # 入口文件
│   └── package.json
├── auto-deploy.sh       # 自动化部署脚本
├── Dockerfile           # Docker 构建文件
└── pom.xml
```

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
- `GET /api/sale` - 获取销售记录列表（分页）
- `POST /api/sale` - 创建销售记录
- `DELETE /api/sale/{id}` - 删除销售记录
- `GET /api/sale/stats/daily` - 每日统计
- `GET /api/sale/stats/weekly` - 每周统计
- `GET /api/sale/stats/monthly` - 每月统计
- `GET /api/sale/stats/quarterly` - 每季度统计
- `GET /api/sale/stats/yearly` - 每年统计

## 权限设计

### 角色
| 角色 | 编码 | 说明 |
|------|------|------|
| 超级管理员 | ADMIN | 拥有系统所有权限 |
| 普通用户 | USER | 基础业务权限 |
| 访客 | GUEST | 只读权限 |

### 权限编码规范
- 菜单权限：`module:menu`
- 按钮权限：`module:action`
- 接口权限：`module:resource:action`

例如：
- `system:user:menu` - 用户管理菜单
- `system:user:add` - 用户新增按钮
- `fish:query` - 鱼类查询
- `sale:add` - 销售新增

### 权限列表

| 模块 | 权限名称 | 权限编码 |
|------|---------|---------|
| 系统管理 | 用户管理 | `system:user:menu` |
| | 用户查询 | `system:user:query` |
| | 用户新增 | `system:user:add` |
| | 用户编辑 | `system:user:edit` |
| | 用户删除 | `system:user:delete` |
| | 角色管理 | `system:role:menu` |
| | 角色查询 | `system:role:query` |
| | 角色新增 | `system:role:add` |
| | 角色编辑 | `system:role:edit` |
| | 角色删除 | `system:role:delete` |
| | 权限管理 | `system:permission:menu` |
| | 权限查询 | `system:permission:query` |
| | 权限新增 | `system:permission:add` |
| | 权限编辑 | `system:permission:edit` |
| | 权限删除 | `system:permission:delete` |
| 鱼类管理 | 鱼类管理 | `fish:menu` |
| | 鱼类查询 | `fish:query` |
| | 鱼类新增 | `fish:add` |
| | 鱼类编辑 | `fish:edit` |
| | 鱼类删除 | `fish:delete` |
| 销售管理 | 销售记录 | `sale:menu` |
| | 销售查询 | `sale:query` |
| | 销售新增 | `sale:add` |
| | 销售删除 | `sale:delete` |
| 统计分析 | 统计分析 | `stats:menu` |
| | 统计查询 | `stats:query` |
| 首页 | 首页访问 | `dashboard:query` |

## 数据库设计

### 主要数据表

| 表名 | 说明 |
|------|------|
| `user` | 用户表 |
| `role` | 角色表 |
| `permission` | 权限表 |
| `user_role` | 用户角色关联表 |
| `role_permission` | 角色权限关联表 |
| `fish` | 鱼类信息表 |
| `sale_record` | 销售记录表 |

## 注意事项

1. 首次启动前必须先执行数据库初始化脚本
2. JWT token 有效期为 24 小时
3. 前端开发时确保后端服务已启动
4. 生产环境部署时请修改 JWT 密钥和数据库密码
5. Docker 部署时确保 Docker 服务正常运行

## 更新日志

详见 [CHANGELOG.md](CHANGELOG.md)

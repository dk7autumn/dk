# 版本更新日志

## v1.1.0 (2026-03-31)

### 主要变更

#### 1. 数据持久层迁移：JPA → MyBatis Plus

**背景：**
- 原项目使用 Spring Data JPA 进行数据持久化
- 为获得更灵活的 SQL 控制能力和更好的性能，迁移至 MyBatis Plus

**变更内容：**

1. **依赖调整** (`pom.xml`)
   - 移除：`spring-boot-starter-data-jpa`
   - 新增：`mybatis-plus-spring-boot3-starter` v3.5.5

2. **配置文件调整** (`application.yml`)
   - 移除 JPA 相关配置（`hibernate.ddl-auto`, `show-sql` 等）
   - 新增 MyBatis Plus 配置：
     ```yaml
     mybatis-plus:
       mapper-locations: classpath*:/mapper/**/*.xml
       type-aliases-package: com.dk.salesystem.entity
       configuration:
         map-underscore-to-camel-case: true
         log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
     ```

3. **实体类改造** (`entity/*.java`)
   - 替换 JPA 注解：`@Entity` → `@TableName`
   - 替换主键注解：`@Id @GeneratedValue` → `@TableId(type = IdType.AUTO)`
   - 替换字段注解：`@Column` → `@TableField`
   - 移除生命周期注解：`@PrePersist`, `@PreUpdate`
   - 移除关联关系注解：`@ManyToMany`, `@ManyToOne`, `@JoinTable`
   - 新增关联对象字段标记为 `@TableField(exist = false)`

4. **数据访问层重构**
   - 删除：`repository/*Repository.java` (JPA Repository 接口)
   - 新增：`mapper/*Mapper.java` (MyBatis Mapper 接口)
   - 所有 Mapper 继承 `BaseMapper<T>`，获得基础 CRUD 能力
   - 自定义查询使用 `@Select` 注解

5. **服务层更新** (`service/*.java`)
   - `UserRepository` → `UserMapper`
   - `RoleRepository` → `RoleMapper`
   - `PermissionRepository` → `PermissionMapper`
   - `FishRepository` → `FishMapper`
   - `SaleRecordRepository` → `SaleRecordMapper`
   - 更新 CRUD 方法调用：
     - `repository.save()` → `mapper.insert()` / `mapper.updateById()`
     - `repository.findById()` → `mapper.selectById()`
     - `repository.findAll()` → `mapper.selectList(null)`
     - `repository.deleteById()` → `mapper.deleteById()`

6. **启动类配置**
   - 新增 `@MapperScan("com.dk.salesystem.mapper")` 注解

#### 2. CORS 配置优化

**变更内容：**
- `SecurityConfig.java` 中更新允许的源，增加端口 5174
- 新增 `WebMvcConfig.java` 配置类，实现全局 CORS 配置
- 使用 `allowedOriginPatterns("*")` 提供更灵活的跨域支持

#### 3. 数据库 Schema 完善

**文件：** `src/main/resources/db/schema.sql`

- 完整的数据库初始化脚本
- 包含 RBAC 权限模型所需的所有表结构
- 预置管理员账户（admin/admin123，BCrypt 加密）
- 预置系统权限数据

### 技术栈

- **后端框架：** Spring Boot 3.2.0
- **安全框架：** Spring Security 6 + JWT (io.jsonwebtoken 0.12.3)
- **持久层：** MyBatis Plus 3.5.5
- **数据库：** MySQL 8.0
- **构建工具：** Maven

### 注意事项

1. **数据库初始化：** 首次部署需执行 `schema.sql` 创建表和初始数据
2. **密码加密：** 管理员密码使用 BCrypt 加密，明文 `admin123` 对应密文需通过 BCrypt 生成
3. **Docker 部署：** 使用 `auto-deploy.sh` 脚本可自动完成 MySQL 容器和应用容器部署

### 测试建议

1. 登录测试：使用 admin/admin123 验证登录功能
2. 权限验证：测试 RBAC 权限控制是否生效
3. CRUD 操作：验证各实体增删改查功能
4. 销售统计：验证日期查询和统计功能

---

## v1.0.0 (之前版本)

### 功能特性
- 基础用户管理
- 角色权限管理
- 销售记录管理
- 鱼类信息管理
- JWT 身份验证
- Spring Security 安全防护

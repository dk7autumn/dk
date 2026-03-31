# 版本更新内容 - v1.1.0

## 更新时间
2026-03-31

## 主要变更

### 1. 数据持久层迁移：JPA → MyBatis Plus

#### 背景
原项目使用 Spring Data JPA 进行数据持久化。为获得更灵活的 SQL 控制能力和更好的性能，迁移至 MyBatis Plus。

#### 变更详情

**1.1 依赖调整 (`pom.xml`)**
- 移除：`spring-boot-starter-data-jpa`
- 新增：`mybatis-plus-spring-boot3-starter` v3.5.5

**1.2 配置文件调整 (`application.yml`)**
- 移除 JPA 相关配置（`hibernate.ddl-auto`, `show-sql`, `dialect` 等）
- 新增 MyBatis Plus 配置：
  ```yaml
  mybatis-plus:
    mapper-locations: classpath*:/mapper/**/*.xml
    type-aliases-package: com.dk.salesystem.entity
    configuration:
      map-underscore-to-camel-case: true
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  ```

**1.3 实体类改造**
所有实体类从 JPA 注解迁移到 MyBatis Plus 注解：

| 原 JPA 注解 | 新 MyBatis Plus 注解 |
|------------|---------------------|
| `@Entity` | `@TableName` |
| `@Id @GeneratedValue` | `@TableId(type = IdType.AUTO)` |
| `@Column` | `@TableField` |
| `@PrePersist/@PreUpdate` | 移除 (使用 FieldFill) |
| `@ManyToMany/@ManyToOne` | `@TableField(exist = false)` |

涉及文件：
- `entity/User.java`
- `entity/Role.java`
- `entity/Permission.java`
- `entity/Fish.java`
- `entity/SaleRecord.java`

**1.4 数据访问层重构**
删除 repository 层，新增 mapper 层：

| 原 Repository | 新 Mapper |
|--------------|-----------|
| `UserRepository` | `UserMapper` |
| `RoleRepository` | `RoleMapper` |
| `PermissionRepository` | `PermissionMapper` |
| `FishRepository` | `FishMapper` |
| `SaleRecordRepository` | `SaleRecordMapper` |

所有 Mapper 继承 `BaseMapper<T>`，获得基础 CRUD 能力，自定义查询使用 `@Select` 注解。

**1.5 服务层更新**
- `UserService.java` - 改用 UserMapper
- `RoleService.java` - 改用 RoleMapper
- `PermissionService.java` - 改用 PermissionMapper
- `FishService.java` - 改用 FishMapper
- `SaleService.java` - 改用 SaleRecordMapper
- `AuthService.java` - 改用 UserMapper，修复角色权限加载逻辑

方法调用变更：
```java
// 旧 JPA 写法
repository.save(entity)      →  新写法：mapper.insert(entity) / mapper.updateById(entity)
repository.findById(id)      →  新写法：mapper.selectById(id)
repository.findAll()         →  新写法：mapper.selectList(null)
repository.delete(id)        →  新写法：mapper.deleteById(id)
```

**1.6 控制器层更新**
由于服务层方法返回值从 `Optional<T>` 改为 `T`，更新所有控制器：
- `UserController.java`
- `RoleController.java`
- `PermissionController.java`
- `FishController.java`
- `SaleController.java`

**1.7 启动类配置**
新增 `@MapperScan("com.dk.salesystem.mapper")` 注解

### 2. CORS 配置优化

**文件变更：**
- `SecurityConfig.java` - 更新允许的源，增加端口 5174
- `WebMvcConfig.java` (新增) - 全局 CORS 配置

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

### 3. 修复的 Bug

1. **登录时角色权限为空问题**
   - 原因：`loadUserByUsername` 加载的角色权限未传递到 `login` 方法
   - 修复：在 `login` 方法中复用已加载角色权限的 `UserDetails` 对象

2. **UserMapper SQL 查询问题**
   - 修复 `findRoleIdsByUserId` 从 `SELECT *` 改为 `SELECT role_id`
   - 修复 `findPermissionIdsByRoleIds` 从 `SELECT *` 改为 `SELECT permission_id`

## 技术栈

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.2.0 |
| Spring Security | 6.x |
| MyBatis Plus | 3.5.5 |
| MySQL Connector | 8.x |
| JWT (jjwt) | 0.12.3 |
| Java | 17 |

## 测试验证

### 登录测试
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

预期返回包含用户信息、角色和权限列表：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "refreshToken": "eyJhbGciOiJIUzM4NCJ9...",
    "expiresIn": 86400000,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "超级管理员",
      "email": "admin@example.com",
      "roles": ["ADMIN"],
      "permissions": ["system:menu", "system:user:query", ...]
    }
  }
}
```

### CRUD 功能测试
- 用户管理：查询、添加、编辑、删除
- 角色管理：查询、添加、编辑、删除
- 权限管理：查询、添加、编辑、删除
- 鱼类管理：查询、添加、编辑、删除
- 销售记录：查询、添加、删除
- 统计功能：日/周/月/季/年统计

## 部署说明

### Docker 部署
```bash
# 重新构建镜像
docker build -t sale-system:latest .

# 停止旧容器
docker stop sale-system && docker rm sale-system

# 启动新容器
docker run -d --name sale-system \
  -p 8080:8080 \
  --link sale-mysql:mysql \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/sale_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  --restart unless-stopped \
  sale-system:latest
```

### 使用自动部署脚本
```bash
./auto-deploy.sh
```

## 注意事项

1. **数据库初始化**：首次部署需执行 `src/main/resources/db/schema.sql` 创建表和初始数据
2. **密码加密**：管理员密码使用 BCrypt 加密，明文 `admin123`
3. **缓存清理**：浏览器可能需要清除缓存以确保使用最新的前端代码

## 回滚方案

如需回滚到 JPA 版本：
1. 从 git 历史恢复 `repository/*` 文件
2. 恢复 `entity/*` 中的 JPA 注解
3. 恢复 `application.yml` 中的 JPA 配置
4. 恢复 `pom.xml` 中的 JPA 依赖
5. 重新构建部署

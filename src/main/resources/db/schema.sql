-- 创建数据库
CREATE DATABASE IF NOT EXISTS sale_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sale_system;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码 (加密)',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像 URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色 ID',
    `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    `description` VARCHAR(255) COMMENT '角色描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS `permission` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限 ID',
    `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `code` VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    `type` TINYINT DEFAULT 1 COMMENT '权限类型：1-菜单，2-按钮，3-接口',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父权限 ID',
    `path` VARCHAR(255) COMMENT '前端路由路径',
    `icon` VARCHAR(50) COMMENT '图标',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `role_id` BIGINT NOT NULL COMMENT '角色 ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `role_id` BIGINT NOT NULL COMMENT '角色 ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限 ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 鱼类表
CREATE TABLE IF NOT EXISTS `fish` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '鱼类 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '鱼类名称',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `origin` VARCHAR(100) COMMENT '产地',
    `description` VARCHAR(255) COMMENT '描述',
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鱼类表';

-- 销售记录表
CREATE TABLE IF NOT EXISTS `sale_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '销售记录 ID',
    `fish_id` BIGINT NOT NULL COMMENT '鱼类 ID',
    `quantity` INT NOT NULL COMMENT '数量',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '总金额',
    `sale_date` DATE NOT NULL COMMENT '销售日期',
    KEY `idx_fish_id` (`fish_id`),
    KEY `idx_sale_date` (`sale_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='销售记录表';

-- 初始化数据 - 角色
INSERT INTO `role` (`name`, `code`, `description`, `status`) VALUES
('超级管理员', 'ADMIN', '拥有系统所有权限', 1),
('普通用户', 'USER', '普通用户权限', 1),
('访客', 'GUEST', '只读权限', 1);

-- 初始化数据 - 权限（菜单）
INSERT INTO `permission` (`name`, `code`, `type`, `parent_id`, `path`, `icon`, `sort`, `status`) VALUES
-- 系统管理模块
('系统管理', 'system:menu', 1, 0, '/system', 'setting', 100, 1),
('用户管理', 'system:user:menu', 1, 1, '/system/user', 'user', 1, 1),
('用户查询', 'system:user:query', 3, 2, NULL, NULL, 0, 1),
('用户新增', 'system:user:add', 2, 2, NULL, NULL, 0, 1),
('用户编辑', 'system:user:edit', 2, 2, NULL, NULL, 0, 1),
('用户删除', 'system:user:delete', 2, 2, NULL, NULL, 0, 1),
('角色管理', 'system:role:menu', 1, 1, '/system/role', 'role', 2, 1),
('角色查询', 'system:role:query', 3, 7, NULL, NULL, 0, 1),
('角色新增', 'system:role:add', 2, 7, NULL, NULL, 0, 1),
('角色编辑', 'system:role:edit', 2, 7, NULL, NULL, 0, 1),
('角色删除', 'system:role:delete', 2, 7, NULL, NULL, 0, 1),
('权限管理', 'system:permission:menu', 1, 1, '/system/permission', 'permission', 3, 1),
-- 鱼类管理模块
('鱼类管理', 'fish:menu', 1, 0, '/fish', 'fish', 1, 1),
('鱼类查询', 'fish:query', 3, 13, NULL, NULL, 0, 1),
('鱼类新增', 'fish:add', 2, 13, NULL, NULL, 0, 1),
('鱼类编辑', 'fish:edit', 2, 13, NULL, NULL, 0, 1),
('鱼类删除', 'fish:delete', 2, 13, NULL, NULL, 0, 1),
-- 销售管理模块
('销售记录', 'sale:menu', 1, 0, '/sale', 'sale', 2, 1),
('销售查询', 'sale:query', 3, 18, NULL, NULL, 0, 1),
('销售新增', 'sale:add', 2, 18, NULL, NULL, 0, 1),
('销售删除', 'sale:delete', 2, 18, NULL, NULL, 0, 1),
-- 统计管理模块
('统计分析', 'stats:menu', 1, 0, '/stats', 'chart', 3, 1),
('统计查询', 'stats:query', 3, 22, NULL, NULL, 0, 1);

-- 初始化数据 - 超级管理员账号 (密码为 admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOQbYXB8k8ppO.T4xVJ6hNlnj/YLHkPn7z9FBt7qCz9G', '超级管理员', 'admin@example.com', 1);

-- 关联超级管理员角色
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 关联超级管理员所有权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `permission`;

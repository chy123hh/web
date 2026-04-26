-- ============================================
-- 后台管理服务数据库初始化脚本
-- 数据库: admin_service
-- ============================================

CREATE DATABASE IF NOT EXISTS admin_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE admin_service;

-- 管理员操作日志表
CREATE TABLE IF NOT EXISTS admin_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    admin_id BIGINT NOT NULL COMMENT '管理员用户ID',
    action VARCHAR(50) NOT NULL COMMENT '操作类型',
    target_type VARCHAR(50) COMMENT '操作目标类型（user/task/order）',
    target_id BIGINT COMMENT '操作目标ID',
    detail TEXT COMMENT '操作详情',
    ip_address VARCHAR(50) COMMENT '操作IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_admin_id (admin_id),
    INDEX idx_create_time (create_time),
    INDEX idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员操作日志表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(255) COMMENT '配置说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 插入默认配置
INSERT INTO system_config (config_key, config_value, description) VALUES
('site.name', '校园帮帮送', '站点名称'),
('site.max_tasks_per_user', '5', '每用户最多同时发布任务数'),
('site.min_credit_score', '60', '最低信用分限制'),
('site.auto_ban_threshold', '30', '信用分低于此值自动封禁');

-- 插入测试管理员日志
INSERT INTO admin_log (admin_id, action, target_type, target_id, detail, ip_address) VALUES
(1, 'LOGIN', 'user', 1, '管理员登录后台', '127.0.0.1'),
(1, 'BAN_USER', 'user', 12, '封禁用户：违规发布任务', '127.0.0.1');

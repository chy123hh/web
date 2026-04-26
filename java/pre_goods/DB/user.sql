CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `student_id` VARCHAR(20) NOT NULL COMMENT '学号（登录账号）',
    `password` VARCHAR(100) NOT NULL COMMENT '密码，BCrypt加密',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `points` INT DEFAULT 0 COMMENT '积分余额',
    `credit_score` INT DEFAULT 100 COMMENT '信用分（0-100，低于60限制接单）',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0普通用户 1管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1正常 2封禁',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
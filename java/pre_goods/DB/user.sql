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

-- 用户表测试数据
-- 注意：密码使用 BCrypt 加密，原始密码统一为 "123456"
-- BCrypt 加密后的密码: $2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq

INSERT INTO `user` (`student_id`, `password`, `nickname`, `avatar_url`, `points`, `credit_score`, `role`, `status`, `create_time`, `update_time`) VALUES
                                                                                                                                                      ('2021001001', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '张三', 'https://example.com/avatar/1.jpg', 100, 100, 0, 1, NOW(), NOW()),
                                                                                                                                                      ('2021001002', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '李四', 'https://example.com/avatar/2.jpg', 200, 95, 0, 1, NOW(), NOW()),
                                                                                                                                                      ('2021001003', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '王五', 'https://example.com/avatar/3.jpg', 50, 80, 0, 1, NOW(), NOW()),
                                                                                                                                                      ('2021001004', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '赵六', 'https://example.com/avatar/4.jpg', 500, 100, 1, 1, NOW(), NOW()),
                                                                                                                                                      ('2021001005', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '钱七', 'https://example.com/avatar/5.jpg', 0, 50, 0, 1, NOW(), NOW()),
                                                                                                                                                      ('2021001006', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '孙八', NULL, 150, 90, 0, 2, NOW(), NOW());
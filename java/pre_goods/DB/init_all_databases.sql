-- =====================================================
-- 校园帮帮送微服务数据库初始化脚本
-- 说明：此脚本用于远程创建所有微服务所需的数据库和表
-- 执行方式：mysql -h <host> -u <username> -p < init_all_databases.sql
-- =====================================================

-- =====================================================
-- 1. 创建用户服务数据库 (user_service)
-- =====================================================
CREATE DATABASE IF NOT EXISTS user_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE user_service;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
    `student_id` VARCHAR(20) NOT NULL COMMENT '学号（登录账号）',
    `password` VARCHAR(100) NOT NULL COMMENT '密码，BCrypt 加密',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    `points` INT DEFAULT 0 COMMENT '积分余额',
    `credit_score` INT DEFAULT 100 COMMENT '信用分（0-100，低于 60 限制接单）',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0 普通用户 1 管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1 正常 2 封禁',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入用户测试数据
-- 注意：密码使用 BCrypt 加密，原始密码统一为 "123456"
-- BCrypt 加密后的密码：$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq
-- 先清空已有数据
TRUNCATE TABLE `user`;
INSERT INTO `user` (`student_id`, `password`, `nickname`, `avatar_url`, `points`, `credit_score`, `role`, `status`, `create_time`, `update_time`) VALUES
('2021001001', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '张三', 'https://example.com/avatar/1.jpg', 100, 100, 0, 1, NOW(), NOW()),
('2021001002', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '李四', 'https://example.com/avatar/2.jpg', 200, 95, 0, 1, NOW(), NOW()),
('2021001003', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '王五', 'https://example.com/avatar/3.jpg', 50, 80, 0, 1, NOW(), NOW()),
('2021001004', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '赵六', 'https://example.com/avatar/4.jpg', 500, 100, 1, 1, NOW(), NOW()),
('2021001005', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '钱七', 'https://example.com/avatar/5.jpg', 0, 50, 0, 1, NOW(), NOW()),
('2021001006', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '孙八', NULL, 150, 90, 0, 2, NOW(), NOW());

-- =====================================================
-- 2. 创建任务服务数据库 (task_service)
-- =====================================================
CREATE DATABASE IF NOT EXISTS task_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE task_service;

-- 创建任务表
CREATE TABLE IF NOT EXISTS task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务 ID',
    user_id BIGINT NOT NULL COMMENT '发布用户 ID',
    title VARCHAR(100) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    type VARCHAR(20) COMMENT '任务类型：DELIVERY-代取快递，PURCHASE-代买，OTHER-其他',
    reward DECIMAL(10, 2) COMMENT '任务报酬',
    pickup_location VARCHAR(255) COMMENT '取货地点',
    delivery_location VARCHAR(255) COMMENT '送货地点',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '任务状态：PENDING-待接单，ACCEPTED-已接单，COMPLETED-已完成，CANCELLED-已取消',
    acceptor_id BIGINT COMMENT '接单人 ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deadline DATETIME COMMENT '截止时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_acceptor_id (acceptor_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- 先清空已有数据
TRUNCATE TABLE task;
INSERT INTO task (user_id, title, description, type, reward, pickup_location, delivery_location, status, create_time, update_time) VALUES
(1, '代取快递', '帮忙到菜鸟驿站取一个快递，送到 3 号楼 205 宿舍', 'DELIVERY', 5.00, '菜鸟驿站', '3 号楼 205', 'PENDING', NOW(), NOW()),
(1, '代买奶茶', '帮忙买一杯珍珠奶茶，送到图书馆', 'PURCHASE', 8.00, '奶茶店', '图书馆', 'PENDING', NOW(), NOW()),
(2, '代取外卖', '帮忙到南门取外卖，送到 5 号楼 302', 'DELIVERY', 3.00, '南门', '5 号楼 302', 'ACCEPTED', NOW(), NOW()),
(2, '帮忙打印', '帮忙打印 5 份资料，送到教学楼 A301', 'OTHER', 2.00, '打印店', '教学楼 A301', 'COMPLETED', NOW(), NOW()),
(1, '代取快递', '帮忙到菜鸟驿站取一个快递，送到 3 号楼 205 宿舍', 'DELIVERY', 5.00, '菜鸟驿站', '3 号楼 205', 'PENDING', NOW(), NOW()),
(1, '代买奶茶', '帮忙买一杯珍珠奶茶，送到图书馆', 'PURCHASE', 8.00, '奶茶店', '图书馆', 'PENDING', NOW(), NOW()),
(2, '代取外卖', '帮忙到南门取外卖，送到 5 号楼 302', 'DELIVERY', 3.00, '南门', '5 号楼 302', 'ACCEPTED', NOW(), NOW()),
(2, '帮忙打印', '帮忙打印 5 份资料，送到教学楼 A301', 'OTHER', 2.00, '打印店', '教学楼 A301', 'COMPLETED', NOW(), NOW());

-- =====================================================
-- 3. 创建订单服务数据库 (order_service)
-- =====================================================
CREATE DATABASE IF NOT EXISTS order_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE order_service;

-- 创建订单表
CREATE TABLE IF NOT EXISTS delivery_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单 ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    task_id BIGINT NOT NULL COMMENT '任务 ID',
    taker_id BIGINT NOT NULL COMMENT '接单者 ID',
    publisher_id BIGINT NOT NULL COMMENT '发布者 ID',
    reward_points INT NOT NULL COMMENT '悬赏积分',
    status TINYINT DEFAULT 1 COMMENT '状态：1-已接单 2-已完成 (待确认) 3-已确认 4-已取消',
    complete_proof_url VARCHAR(255) COMMENT '完成凭证图片 URL',
    confirm_time DATETIME COMMENT '确认时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_id (task_id),
    INDEX idx_taker_id (taker_id),
    INDEX idx_publisher_id (publisher_id),
    INDEX idx_status (status),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='跑腿订单表';

-- 先清空已有数据
TRUNCATE TABLE delivery_order;
INSERT INTO delivery_order (order_no, task_id, taker_id, publisher_id, reward_points, status, create_time, update_time) VALUES
('ORD20260428001', 1, 7, 1, 5, 2, NOW(), NOW()),
('ORD20260428002', 2, 8, 1, 8, 3, NOW(), NOW()),
('ORD20260428003', 3, 9, 2, 3, 1, NOW(), NOW()),
('ORD20260428004', 4, 7, 2, 2, 3, NOW(), NOW()),
('ORD20260428005', 5, 8, 1, 5, 1, NOW(), NOW());

-- =====================================================
-- 4. 创建消息服务数据库 (message_service)
-- =====================================================
CREATE DATABASE IF NOT EXISTS message_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE message_service;

-- 创建消息表
CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息 ID',
    sender_id BIGINT NOT NULL COMMENT '发送者 ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者 ID',
    content VARCHAR(500) NOT NULL COMMENT '消息内容',
    type TINYINT DEFAULT 2 COMMENT '消息类型：1 系统消息 2 用户消息',
    status TINYINT DEFAULT 0 COMMENT '状态：0 未读 1 已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    read_time DATETIME NULL COMMENT '阅读时间',
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 先清空已有数据
TRUNCATE TABLE message;
INSERT INTO message (sender_id, receiver_id, content, type, status, create_time) VALUES
(1, 7, '您好，您的任务已被接单', 1, 0, NOW()),
(2, 7, '任务已完成，请确认', 2, 0, NOW()),
(1, 7, '欢迎使用校园帮帮送', 1, 1, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- =====================================================
-- 5. 创建评价服务数据库 (evaluation_service)
-- =====================================================
CREATE DATABASE IF NOT EXISTS evaluation_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE evaluation_service;

-- 创建评价表
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务 ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单 ID',
  `evaluator_id` bigint(20) NOT NULL COMMENT '评价人 ID（发布者）',
  `evaluated_id` bigint(20) NOT NULL COMMENT '被评价人 ID（接单人）',
  `rating` int(11) NOT NULL COMMENT '评分（1-5 星）',
  `content` varchar(500) NOT NULL COMMENT '评价内容',
  `type` int(11) NOT NULL COMMENT '评价类型：1-对接单人评价，2-对发布者评价',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_evaluator_id` (`evaluator_id`),
  KEY `idx_evaluated_id` (`evaluated_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='评价表';

-- 先清空已有数据
TRUNCATE TABLE `evaluation`;
INSERT INTO `evaluation` (`task_id`, `order_id`, `evaluator_id`, `evaluated_id`, `rating`, `content`, `type`, `create_time`, `update_time`, `deleted`) VALUES
(1, 1, 8, 7, 5, '非常靠谱，很快就完成了任务！', 1, NOW(), NOW(), 0),
(1, 1, 7, 8, 4, '发布者很友好，沟通顺畅', 2, NOW(), NOW(), 0),
(2, 2, 8, 9, 4, '服务态度好，就是稍微慢了点', 1, NOW(), NOW(), 0);

-- =====================================================
-- 初始化完成提示
-- =====================================================
SELECT '数据库初始化完成！' AS message;
SELECT '已创建以下数据库：' AS info;
SELECT '1. user_service (用户服务)' AS database_name;
SELECT '2. task_service (任务服务)' AS database_name;
SELECT '3. order_service (订单服务)' AS database_name;
SELECT '4. message_service (消息服务)' AS database_name;
SELECT '5. evaluation_service (评价服务)' AS database_name;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS user_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE user_service;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `student_id` VARCHAR(20) NOT NULL UNIQUE COMMENT '学号（登录账号）',
    `password` VARCHAR(100) NOT NULL COMMENT 'BCrypt加密',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar_url` VARCHAR(255) COMMENT '头像URL',
    `points` INT DEFAULT 0 COMMENT '积分余额',
    `credit_score` INT DEFAULT 100 COMMENT '信用分（0-100，低于60限制接单）',
    `role` TINYINT DEFAULT 0 COMMENT '0普通用户 1管理员',
    `status` TINYINT DEFAULT 1 COMMENT '1正常 2封禁',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 任务表
CREATE TABLE IF NOT EXISTS `task` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
    `title` VARCHAR(100) NOT NULL COMMENT '任务标题',
    `description` TEXT COMMENT '详细描述',
    `reward_points` INT NOT NULL COMMENT '悬赏积分',
    `location` VARCHAR(255) COMMENT '任务地点（如"3号教学楼"）',
    `latitude` DOUBLE COMMENT '纬度（用于附近搜索）',
    `longitude` DOUBLE COMMENT '经度（用于附近搜索）',
    `deadline` DATETIME COMMENT '截止时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1待接单 2已接单 3已完成 4已取消 5已过期',
    `category` TINYINT COMMENT '1快递 2餐饮 3占座 4借书 5其他',
    `image_urls` VARCHAR(1000) COMMENT '凭证图片（逗号分隔）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_publisher_id` (`publisher_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- 跑腿订单表
CREATE TABLE IF NOT EXISTS `delivery_order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    `order_no` VARCHAR(32) UNIQUE COMMENT '订单号',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `taker_id` BIGINT NOT NULL COMMENT '接单者ID',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
    `reward_points` INT NOT NULL COMMENT '接单时锁定的积分',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1已接单 2已完成（待确认）3已确认 4已取消',
    `complete_proof_url` VARCHAR(255) COMMENT '完成凭证图片',
    `confirm_time` DATETIME COMMENT '发布者确认时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '接单时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_task_id` (`task_id`),
    INDEX `idx_taker_id` (`taker_id`),
    INDEX `idx_publisher_id` (`publisher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='跑腿订单表';

-- 消息表
CREATE TABLE IF NOT EXISTS `message` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    `from_user_id` BIGINT COMMENT '发送者（0为系统）',
    `to_user_id` BIGINT NOT NULL COMMENT '接收者',
    `type` TINYINT COMMENT '1系统通知 2任务状态 3评价提醒',
    `title` VARCHAR(100) COMMENT '消息标题',
    `content` VARCHAR(500) COMMENT '消息内容',
    `is_read` TINYINT DEFAULT 0 COMMENT '0未读 1已读',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    INDEX `idx_to_user_id` (`to_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 评价表
CREATE TABLE IF NOT EXISTS `evaluation` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    `order_id` BIGINT NOT NULL UNIQUE COMMENT '订单ID（一个订单只能评价一次）',
    `from_user_id` BIGINT NOT NULL COMMENT '评价人',
    `to_user_id` BIGINT NOT NULL COMMENT '被评价人',
    `score` TINYINT NOT NULL COMMENT '1-5分',
    `content` VARCHAR(255) COMMENT '评价内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_from_user_id` (`from_user_id`),
    INDEX `idx_to_user_id` (`to_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- 插入测试数据
INSERT INTO `user` (`student_id`, `password`, `nickname`, `points`, `credit_score`, `role`, `status`) VALUES
('2021001001', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '张三', 100, 100, 0, 1),
('2021001002', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '李四', 50, 95, 0, 1),
('2021001003', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '管理员', 0, 100, 1, 1);

INSERT INTO `task` (`publisher_id`, `title`, `description`, `reward_points`, `location`, `latitude`, `longitude`, `deadline`, `status`, `category`) VALUES
(1, '代取快递', '帮我取一下快递，在菜鸟驿站，单号SF1234567890', 10, '菜鸟驿站', 30.5928, 114.3055, '2024-12-31 18:00:00', 1, 1),
(1, '代买奶茶', '帮我买一杯珍珠奶茶，少糖少冰，送到3号教学楼', 8, '3号教学楼', 30.5930, 114.3060, '2024-12-20 12:00:00', 1, 2),
(2, '占座', '帮我在图书馆三楼占个座位', 5, '图书馆三楼', 30.5925, 114.3045, '2024-12-19 08:00:00', 2, 3),
(2, '借书', '帮我借一本《Java编程思想》', 3, '图书馆', 30.5925, 114.3045, '2024-12-25 17:00:00', 1, 4);

INSERT INTO `delivery_order` (`order_no`, `task_id`, `taker_id`, `publisher_id`, `reward_points`, `status`) VALUES
('ORD202412010001', 3, 1, 2, 5, 1),
('ORD202412010002', 1, 2, 1, 10, 2);

INSERT INTO `message` (`from_user_id`, `to_user_id`, `type`, `title`, `content`) VALUES
(0, 1, 2, '任务被接单', '您发布的"代取快递"任务已被用户李四接单'),
(0, 2, 2, '接单成功', '您已成功接取"占座"任务'),
(0, 1, 1, '系统通知', '欢迎使用校园帮帮送');

INSERT INTO `evaluation` (`order_id`, `from_user_id`, `to_user_id`, `score`, `content`) VALUES
(1, 2, 1, 5, '服务很好，很准时');

COMMIT;
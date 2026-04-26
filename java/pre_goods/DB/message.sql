-- 创建数据库
CREATE DATABASE IF NOT EXISTS message_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE message_service;

-- 消息表
CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    content VARCHAR(500) NOT NULL COMMENT '消息内容',
    type TINYINT DEFAULT 2 COMMENT '消息类型：1系统消息 2用户消息',
    status TINYINT DEFAULT 0 COMMENT '状态：0未读 1已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    read_time DATETIME NULL COMMENT '阅读时间',
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- 插入测试数据
INSERT INTO message (sender_id, receiver_id, content, type, status, create_time) VALUES
(1, 7, '您好，您的任务已被接单', 1, 0, NOW()),
(2, 7, '任务已完成，请确认', 2, 0, NOW()),
(1, 7, '欢迎使用校园帮帮送', 1, 1, DATE_SUB(NOW(), INTERVAL 1 DAY));

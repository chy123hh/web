CREATE DATABASE IF NOT EXISTS order_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE order_service;

CREATE TABLE IF NOT EXISTS delivery_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    taker_id BIGINT NOT NULL COMMENT '接单者ID',
    publisher_id BIGINT NOT NULL COMMENT '发布者ID',
    reward_points INT NOT NULL COMMENT '悬赏积分',
    status TINYINT DEFAULT 1 COMMENT '状态：1-已接单 2-已完成(待确认) 3-已确认 4-已取消',
    complete_proof_url VARCHAR(255) COMMENT '完成凭证图片URL',
    confirm_time DATETIME COMMENT '确认时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_id (task_id),
    INDEX idx_taker_id (taker_id),
    INDEX idx_publisher_id (publisher_id),
    INDEX idx_status (status),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跑腿订单表';

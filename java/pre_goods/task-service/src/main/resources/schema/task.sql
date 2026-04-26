CREATE DATABASE IF NOT EXISTS task_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE task_service;

CREATE TABLE IF NOT EXISTS task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    user_id BIGINT NOT NULL COMMENT '发布用户ID',
    title VARCHAR(100) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    type VARCHAR(20) COMMENT '任务类型：DELIVERY-代取快递, PURCHASE-代买, OTHER-其他',
    reward DECIMAL(10, 2) COMMENT '任务报酬',
    pickup_location VARCHAR(255) COMMENT '取货地点',
    delivery_location VARCHAR(255) COMMENT '送货地点',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '任务状态：PENDING-待接单, ACCEPTED-已接单, COMPLETED-已完成, CANCELLED-已取消',
    acceptor_id BIGINT COMMENT '接单人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deadline DATETIME COMMENT '截止时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_acceptor_id (acceptor_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- 插入测试数据
INSERT INTO task (user_id, title, description, type, reward, pickup_location, delivery_location, status, create_time, update_time) VALUES
(1, '代取快递', '帮忙到菜鸟驿站取一个快递，送到3号楼205宿舍', 'DELIVERY', 5.00, '菜鸟驿站', '3号楼205', 'PENDING', NOW(), NOW()),
(1, '代买奶茶', '帮忙买一杯珍珠奶茶，送到图书馆', 'PURCHASE', 8.00, '奶茶店', '图书馆', 'PENDING', NOW(), NOW()),
(2, '代取外卖', '帮忙到南门取外卖，送到5号楼302', 'DELIVERY', 3.00, '南门', '5号楼302', 'ACCEPTED', NOW(), NOW()),
(2, '帮忙打印', '帮忙打印5份资料，送到教学楼A301', 'OTHER', 2.00, '打印店', '教学楼A301', 'COMPLETED', NOW(), NOW());

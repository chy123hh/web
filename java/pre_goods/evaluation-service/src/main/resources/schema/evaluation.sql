-- 创建评价服务数据库
CREATE DATABASE IF NOT EXISTS evaluation_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE evaluation_service;

-- 创建评价表
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `evaluator_id` bigint(20) NOT NULL COMMENT '评价人ID（发布者）',
  `evaluated_id` bigint(20) NOT NULL COMMENT '被评价人ID（接单人）',
  `rating` int(11) NOT NULL COMMENT '评分（1-5星）',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 插入测试数据
INSERT INTO `evaluation` (`task_id`, `order_id`, `evaluator_id`, `evaluated_id`, `rating`, `content`, `type`, `create_time`, `update_time`, `deleted`) VALUES
(1, 1, 8, 7, 5, '非常靠谱，很快就完成了任务！', 1, '2026-04-20 15:30:00', '2026-04-20 15:30:00', 0),
(1, 1, 7, 8, 4, '发布者很友好，沟通顺畅', 2, '2026-04-20 16:00:00', '2026-04-20 16:00:00', 0),
(2, 2, 8, 9, 4, '服务态度好，就是稍微慢了点', 1, '2026-04-21 10:00:00', '2026-04-21 10:00:00', 0);

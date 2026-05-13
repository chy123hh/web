-- 管理员服务数据库初始化脚本
-- 注意：admin-service 需要访问多个数据库，这里只创建必要的视图或跨数据库查询

-- 创建用户统计视图（在 user_service 数据库中）
CREATE OR REPLACE VIEW user_stats AS
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN status = 2 THEN 1 END) as banned_users,
    COUNT(CASE WHEN role = 1 THEN 1 END) as admin_users,
    AVG(credit_score) as avg_credit_score,
    SUM(points) as total_points
FROM user;

-- 创建任务统计视图（在 task_service 数据库中）
CREATE OR REPLACE VIEW task_stats AS
SELECT 
    COUNT(*) as total_tasks,
    COUNT(CASE WHEN status = 'PENDING' THEN 1 END) as pending_tasks,
    COUNT(CASE WHEN status = 'ACCEPTED' THEN 1 END) as accepted_tasks,
    COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed_tasks,
    COUNT(CASE WHEN status = 'CANCELLED' THEN 1 END) as cancelled_tasks,
    AVG(reward) as avg_reward,
    SUM(reward) as total_reward
FROM task;

-- 创建订单统计视图（在 order_service 数据库中）
CREATE OR REPLACE VIEW order_stats AS
SELECT 
    COUNT(*) as total_orders,
    COUNT(CASE WHEN status = 1 THEN 1 END) as taken_orders,
    COUNT(CASE WHEN status = 2 THEN 1 END) as completed_orders,
    COUNT(CASE WHEN status = 3 THEN 1 END) as confirmed_orders,
    COUNT(CASE WHEN status = 4 THEN 1 END) as cancelled_orders,
    SUM(reward_points) as total_reward_points
FROM delivery_order;

-- 创建评价统计视图（在 evaluation_service 数据库中）
CREATE OR REPLACE VIEW evaluation_stats AS
SELECT 
    COUNT(*) as total_evaluations,
    AVG(score) as avg_score,
    COUNT(CASE WHEN score = 5 THEN 1 END) as five_star_evaluations,
    COUNT(CASE WHEN score = 4 THEN 1 END) as four_star_evaluations,
    COUNT(CASE WHEN score = 3 THEN 1 END) as three_star_evaluations,
    COUNT(CASE WHEN score = 2 THEN 1 END) as two_star_evaluations,
    COUNT(CASE WHEN score = 1 THEN 1 END) as one_star_evaluations
FROM evaluation;

-- 创建消息统计视图（在 message_service 数据库中）
CREATE OR REPLACE VIEW message_stats AS
SELECT 
    COUNT(*) as total_messages,
    COUNT(CASE WHEN is_read = 0 THEN 1 END) as unread_messages,
    COUNT(CASE WHEN is_read = 1 THEN 1 END) as read_messages,
    COUNT(CASE WHEN type = 1 THEN 1 END) as system_messages,
    COUNT(CASE WHEN type = 2 THEN 1 END) as task_messages,
    COUNT(CASE WHEN type = 3 THEN 1 END) as evaluation_messages
FROM message;
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
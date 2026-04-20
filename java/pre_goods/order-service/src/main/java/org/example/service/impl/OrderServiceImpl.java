package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Result;
import org.example.common.util.JwtUtil;
import org.example.dto.request.CompleteProofRequest;
import org.example.dto.response.OrderResponse;
import org.example.entity.Order;
import org.example.mapper.OrderMapper;
import org.example.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    // Redis锁前缀
    private static final String LOCK_PREFIX = "help:order:lock:";
    // 锁过期时间（秒）
    private static final long LOCK_EXPIRE_TIME = 5;

    @Override
    @Transactional
    public Result takeOrder(Long taskId, Long publisherId, Integer rewardPoints) {
        Long takerId = getCurrentUserId();
        if (takerId == null) {
            return Result.error(401, "用户未登录");
        }

        // 不能接自己发布的任务
        if (takerId.equals(publisherId)) {
            return Result.error(400, "不能接自己发布的任务");
        }

        // 使用Redis分布式锁防止并发接单
        String lockKey = LOCK_PREFIX + taskId;
        String lockValue = UUID.randomUUID().toString();

        try {
            // 尝试获取锁
            Boolean locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockValue, LOCK_EXPIRE_TIME, TimeUnit.SECONDS);

            if (Boolean.FALSE.equals(locked)) {
                return Result.error(400, "该任务正在被其他用户接单，请稍后再试");
            }

            // 检查该任务是否已被接单
            Order existOrder = orderMapper.selectByTaskId(taskId);
            if (existOrder != null && !Order.STATUS_CANCELLED.equals(existOrder.getStatus())) {
                return Result.error(400, "该任务已被接单");
            }

            // 生成订单号
            String orderNo = generateOrderNo();

            // 创建订单
            Order order = Order.builder()
                    .orderNo(orderNo)
                    .taskId(taskId)
                    .takerId(takerId)
                    .publisherId(publisherId)
                    .rewardPoints(rewardPoints)
                    .status(Order.STATUS_TAKEN)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            boolean saved = save(order);
            if (saved) {
                log.info("用户 {} 成功接单，任务ID: {}, 订单号: {}", takerId, taskId, orderNo);
                return Result.success("接单成功", orderNo);
            }
            return Result.error(500, "接单失败");

        } finally {
            // 释放锁（使用Lua脚本确保原子性）
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            redisTemplate.execute(
                    new org.springframework.data.redis.core.script.DefaultRedisScript<>(script, Long.class),
                    List.of(lockKey),
                    lockValue
            );
        }
    }

    @Override
    @Transactional
    public Result cancelOrder(Long orderId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Order order = getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }

        // 只有接单人可以取消订单
        if (!order.getTakerId().equals(userId)) {
            return Result.error(403, "只有接单人可以取消订单");
        }

        // 只能取消已接单的订单
        if (!Order.STATUS_TAKEN.equals(order.getStatus())) {
            return Result.error(400, "只能取消已接单的订单");
        }

        int updated = orderMapper.updateStatus(orderId, Order.STATUS_CANCELLED);
        if (updated > 0) {
            log.info("用户 {} 取消订单，订单ID: {}", userId, orderId);
            return Result.success("取消接单成功", null);
        }
        return Result.error(500, "取消接单失败");
    }

    @Override
    @Transactional
    public Result uploadCompleteProof(Long orderId, CompleteProofRequest requestDto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Order order = getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }

        // 只有接单人可以上传凭证
        if (!order.getTakerId().equals(userId)) {
            return Result.error(403, "只有接单人可以上传完成凭证");
        }

        // 只能上传已接单状态的订单
        if (!Order.STATUS_TAKEN.equals(order.getStatus())) {
            return Result.error(400, "只能为已接单的订单上传凭证");
        }

        int updated = orderMapper.updateCompleteProof(orderId, requestDto.getProofImageUrl(), Order.STATUS_COMPLETED);
        if (updated > 0) {
            log.info("用户 {} 上传完成凭证，订单ID: {}", userId, orderId);
            return Result.success("上传完成凭证成功", null);
        }
        return Result.error(500, "上传完成凭证失败");
    }

    @Override
    @Transactional
    public Result confirmOrder(Long orderId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Order order = getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }

        // 只有发布者可以确认完成
        if (!order.getPublisherId().equals(userId)) {
            return Result.error(403, "只有发布者可以确认完成");
        }

        // 只能确认已完成（待确认）状态的订单
        if (!Order.STATUS_COMPLETED.equals(order.getStatus())) {
            return Result.error(400, "只能确认已上传凭证的订单");
        }

        int updated = orderMapper.updateConfirmStatus(orderId, Order.STATUS_CONFIRMED);
        if (updated > 0) {
            log.info("用户 {} 确认订单完成，订单ID: {}", userId, orderId);
            // TODO: 这里需要调用用户服务进行积分转移
            return Result.success("确认完成成功，积分已转移", null);
        }
        return Result.error(500, "确认完成失败");
    }

    @Override
    public Result listMyTakenOrders() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        List<Order> orders = orderMapper.selectByTakerId(userId);
        List<OrderResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    @Override
    public Result listMyPublishedOrders() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        List<Order> orders = orderMapper.selectByPublisherId(userId);
        List<OrderResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    private Long getCurrentUserId() {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        token = token.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        response.setStatusDesc(getStatusDesc(order.getStatus()));
        return response;
    }

    private String getStatusDesc(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "已接单";
            case 2: return "已完成（待确认）";
            case 3: return "已确认";
            case 4: return "已取消";
            default: return "未知";
        }
    }
}

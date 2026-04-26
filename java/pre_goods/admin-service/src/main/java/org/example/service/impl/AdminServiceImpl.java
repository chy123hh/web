package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.exception.BusinessException;
import org.example.dto.response.*;
import org.example.entity.AdminOrder;
import org.example.entity.AdminTask;
import org.example.entity.AdminUser;
import org.example.mapper.AdminOrderMapper;
import org.example.mapper.AdminTaskMapper;
import org.example.mapper.AdminUserMapper;
import org.example.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminUserMapper adminUserMapper;
    private final AdminTaskMapper adminTaskMapper;
    private final AdminOrderMapper adminOrderMapper;

    @Override
    public DashboardResponse getDashboard() {
        // 用户统计
        LambdaQueryWrapper<AdminUser> userWrapper = new LambdaQueryWrapper<>();
        Long totalUsers = adminUserMapper.selectCount(userWrapper);
        Long activeUsers = adminUserMapper.selectCount(
                new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getStatus, AdminUser.STATUS_NORMAL));
        Long bannedUsers = adminUserMapper.selectCount(
                new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getStatus, AdminUser.STATUS_BANNED));
        Long adminCount = adminUserMapper.selectCount(
                new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getRole, AdminUser.ROLE_ADMIN));

        // 任务统计
        Long totalTasks = adminTaskMapper.selectCount(new LambdaQueryWrapper<>());
        Long pendingTasks = adminTaskMapper.selectCount(
                new LambdaQueryWrapper<AdminTask>().eq(AdminTask::getStatus, AdminTask.STATUS_PENDING));
        Long acceptedTasks = adminTaskMapper.selectCount(
                new LambdaQueryWrapper<AdminTask>().eq(AdminTask::getStatus, AdminTask.STATUS_ACCEPTED));
        Long completedTasks = adminTaskMapper.selectCount(
                new LambdaQueryWrapper<AdminTask>().eq(AdminTask::getStatus, AdminTask.STATUS_COMPLETED));
        Long cancelledTasks = adminTaskMapper.selectCount(
                new LambdaQueryWrapper<AdminTask>().eq(AdminTask::getStatus, AdminTask.STATUS_CANCELLED));

        // 订单统计
        Long totalOrders = adminOrderMapper.selectCount(new LambdaQueryWrapper<>());
        Long completedOrders = adminOrderMapper.selectCount(
                new LambdaQueryWrapper<AdminOrder>().eq(AdminOrder::getStatus, AdminOrder.STATUS_CONFIRMED));
        Long pendingConfirmOrders = adminOrderMapper.selectCount(
                new LambdaQueryWrapper<AdminOrder>().eq(AdminOrder::getStatus, AdminOrder.STATUS_COMPLETED));

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .bannedUsers(bannedUsers)
                .adminCount(adminCount)
                .totalTasks(totalTasks)
                .pendingTasks(pendingTasks)
                .acceptedTasks(acceptedTasks)
                .completedTasks(completedTasks)
                .cancelledTasks(cancelledTasks)
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .pendingConfirmOrders(pendingConfirmOrders)
                .build();
    }

    @Override
    public Page<AdminUserResponse> getUserList(Integer page, Integer size, String keyword, Integer status) {
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                    .like(AdminUser::getStudentId, keyword)
                    .or()
                    .like(AdminUser::getNickname, keyword));
        }
        if (status != null) {
            wrapper.eq(AdminUser::getStatus, status);
        }
        wrapper.orderByDesc(AdminUser::getCreateTime);

        Page<AdminUser> userPage = new Page<>(page, size);
        Page<AdminUser> result = adminUserMapper.selectPage(userPage, wrapper);

        // 转换为响应
        Page<AdminUserResponse> responsePage = new Page<>(page, size, result.getTotal());
        List<AdminUserResponse> records = result.getRecords().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(records);

        return responsePage;
    }

    @Override
    public AdminUserResponse getUserDetail(Long userId) {
        AdminUser user = adminUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        AdminUserResponse response = convertToUserResponse(user);

        // 统计用户发布的任务数
        Long publishedCount = adminTaskMapper.selectCount(
                new LambdaQueryWrapper<AdminTask>().eq(AdminTask::getUserId, userId));
        response.setPublishedTaskCount(publishedCount);

        // 统计用户接单的任务数
        Long acceptedCount = adminTaskMapper.selectCount(
                new LambdaQueryWrapper<AdminTask>().eq(AdminTask::getAcceptorId, userId));
        response.setAcceptedTaskCount(acceptedCount);

        // 统计完成的订单数
        Long completedOrderCount = adminOrderMapper.selectCount(
                new LambdaQueryWrapper<AdminOrder>()
                        .eq(AdminOrder::getTakerId, userId)
                        .eq(AdminOrder::getStatus, AdminOrder.STATUS_CONFIRMED));
        response.setCompletedOrderCount(completedOrderCount);

        return response;
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        AdminUser user = adminUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!AdminUser.STATUS_NORMAL.equals(status) && !AdminUser.STATUS_BANNED.equals(status)) {
            throw new BusinessException(400, "状态值不正确，1-正常 2-封禁");
        }

        LambdaUpdateWrapper<AdminUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AdminUser::getId, userId)
               .set(AdminUser::getStatus, status);

        adminUserMapper.update(null, wrapper);
        String action = AdminUser.STATUS_BANNED.equals(status) ? "封禁" : "解封";
        log.info("管理员{}用户 ID: {}", action, userId);
    }

    @Override
    public Page<AdminTaskResponse> getTaskList(Integer page, Integer size, String status, String keyword) {
        LambdaQueryWrapper<AdminTask> wrapper = new LambdaQueryWrapper<>();

        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(AdminTask::getStatus, status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(AdminTask::getTitle, keyword);
        }
        wrapper.orderByDesc(AdminTask::getCreateTime);

        Page<AdminTask> taskPage = new Page<>(page, size);
        Page<AdminTask> result = adminTaskMapper.selectPage(taskPage, wrapper);

        // 批量获取用户昵称
        Map<Long, String> userNicknameMap = buildUserNicknameMap(
                result.getRecords().stream().map(AdminTask::getUserId).collect(Collectors.toList()));

        // 转换为响应
        Page<AdminTaskResponse> responsePage = new Page<>(page, size, result.getTotal());
        List<AdminTaskResponse> records = result.getRecords().stream()
                .map(task -> convertToTaskResponse(task, userNicknameMap))
                .collect(Collectors.toList());
        responsePage.setRecords(records);

        return responsePage;
    }

    @Override
    public AdminTaskResponse getTaskDetail(Long taskId) {
        AdminTask task = adminTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }

        AdminUser publisher = adminUserMapper.selectById(task.getUserId());
        String publisherNickname = publisher != null ? publisher.getNickname() : "未知用户";

        String acceptorNickname = null;
        if (task.getAcceptorId() != null) {
            AdminUser acceptor = adminUserMapper.selectById(task.getAcceptorId());
            acceptorNickname = acceptor != null ? acceptor.getNickname() : "未知用户";
        }

        // 查询关联订单
        AdminOrder order = adminOrderMapper.selectOne(
                new LambdaQueryWrapper<AdminOrder>().eq(AdminOrder::getTaskId, taskId)
                        .orderByDesc(AdminOrder::getCreateTime)
                        .last("LIMIT 1"));

        return AdminTaskResponse.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .publisherNickname(publisherNickname)
                .title(task.getTitle())
                .description(task.getDescription())
                .type(task.getType())
                .reward(task.getReward())
                .pickupLocation(task.getPickupLocation())
                .deliveryLocation(task.getDeliveryLocation())
                .status(task.getStatus())
                .acceptorId(task.getAcceptorId())
                .acceptorNickname(acceptorNickname)
                .createTime(task.getCreateTime())
                .updateTime(task.getUpdateTime())
                .deadline(task.getDeadline())
                .orderId(order != null ? order.getId() : null)
                .orderNo(order != null ? order.getOrderNo() : null)
                .build();
    }

    @Override
    public void forceCancelTask(Long taskId) {
        AdminTask task = adminTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        if (AdminTask.STATUS_CANCELLED.equals(task.getStatus())) {
            throw new BusinessException(400, "任务已被取消");
        }
        if (AdminTask.STATUS_COMPLETED.equals(task.getStatus())) {
            throw new BusinessException(400, "已完成的任务不能取消");
        }

        LambdaUpdateWrapper<AdminTask> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AdminTask::getId, taskId)
               .set(AdminTask::getStatus, AdminTask.STATUS_CANCELLED);

        adminTaskMapper.update(null, wrapper);
        log.info("管理员强制取消任务 ID: {}", taskId);
    }

    @Override
    public Page<AdminOrderResponse> getOrderList(Integer page, Integer size, Integer status) {
        LambdaQueryWrapper<AdminOrder> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(AdminOrder::getStatus, status);
        }
        wrapper.orderByDesc(AdminOrder::getCreateTime);

        Page<AdminOrder> orderPage = new Page<>(page, size);
        Page<AdminOrder> result = adminOrderMapper.selectPage(orderPage, wrapper);

        // 批量获取用户昵称
        List<Long> userIds = result.getRecords().stream()
                .flatMap(order -> {
                    if (order.getTakerId() != null && order.getPublisherId() != null) {
                        return java.util.stream.Stream.of(order.getTakerId(), order.getPublisherId());
                    } else if (order.getTakerId() != null) {
                        return java.util.stream.Stream.of(order.getTakerId());
                    } else if (order.getPublisherId() != null) {
                        return java.util.stream.Stream.of(order.getPublisherId());
                    }
                    return java.util.stream.Stream.empty();
                })
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> userNicknameMap = buildUserNicknameMap(userIds);

        Page<AdminOrderResponse> responsePage = new Page<>(page, size, result.getTotal());
        List<AdminOrderResponse> records = result.getRecords().stream()
                .map(order -> convertToOrderResponse(order, userNicknameMap))
                .collect(Collectors.toList());
        responsePage.setRecords(records);

        return responsePage;
    }

    @Override
    public AdminOrderResponse getOrderDetail(Long orderId) {
        AdminOrder order = adminOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        String takerNickname = null;
        if (order.getTakerId() != null) {
            AdminUser taker = adminUserMapper.selectById(order.getTakerId());
            takerNickname = taker != null ? taker.getNickname() : "未知用户";
        }

        String publisherNickname = null;
        if (order.getPublisherId() != null) {
            AdminUser publisher = adminUserMapper.selectById(order.getPublisherId());
            publisherNickname = publisher != null ? publisher.getNickname() : "未知用户";
        }

        String taskTitle = null;
        if (order.getTaskId() != null) {
            AdminTask task = adminTaskMapper.selectById(order.getTaskId());
            taskTitle = task != null ? task.getTitle() : "未知任务";
        }

        return AdminOrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .taskId(order.getTaskId())
                .taskTitle(taskTitle)
                .takerId(order.getTakerId())
                .takerNickname(takerNickname)
                .publisherId(order.getPublisherId())
                .publisherNickname(publisherNickname)
                .rewardPoints(order.getRewardPoints())
                .status(order.getStatus())
                .statusDesc(getOrderStatusDesc(order.getStatus()))
                .completeProofUrl(order.getCompleteProofUrl())
                .confirmTime(order.getConfirmTime())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateTime())
                .build();
    }

    // ==================== 私有辅助方法 ====================

    private AdminUserResponse convertToUserResponse(AdminUser user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .studentId(user.getStudentId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .points(user.getPoints())
                .creditScore(user.getCreditScore())
                .role(user.getRole())
                .roleDesc(AdminUser.ROLE_ADMIN.equals(user.getRole()) ? "管理员" : "普通用户")
                .status(user.getStatus())
                .statusDesc(AdminUser.STATUS_BANNED.equals(user.getStatus()) ? "已封禁" : "正常")
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }

    private AdminTaskResponse convertToTaskResponse(AdminTask task, Map<Long, String> userNicknameMap) {
        return AdminTaskResponse.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .publisherNickname(userNicknameMap.getOrDefault(task.getUserId(), "未知用户"))
                .title(task.getTitle())
                .description(task.getDescription())
                .type(task.getType())
                .reward(task.getReward())
                .pickupLocation(task.getPickupLocation())
                .deliveryLocation(task.getDeliveryLocation())
                .status(task.getStatus())
                .acceptorId(task.getAcceptorId())
                .acceptorNickname(task.getAcceptorId() != null
                        ? userNicknameMap.getOrDefault(task.getAcceptorId(), "未知用户")
                        : null)
                .createTime(task.getCreateTime())
                .updateTime(task.getUpdateTime())
                .deadline(task.getDeadline())
                .build();
    }

    private AdminOrderResponse convertToOrderResponse(AdminOrder order, Map<Long, String> userNicknameMap) {
        return AdminOrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .taskId(order.getTaskId())
                .takerId(order.getTakerId())
                .takerNickname(order.getTakerId() != null
                        ? userNicknameMap.getOrDefault(order.getTakerId(), "未知用户")
                        : null)
                .publisherId(order.getPublisherId())
                .publisherNickname(order.getPublisherId() != null
                        ? userNicknameMap.getOrDefault(order.getPublisherId(), "未知用户")
                        : null)
                .rewardPoints(order.getRewardPoints())
                .status(order.getStatus())
                .statusDesc(getOrderStatusDesc(order.getStatus()))
                .completeProofUrl(order.getCompleteProofUrl())
                .confirmTime(order.getConfirmTime())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateTime())
                .build();
    }

    private Map<Long, String> buildUserNicknameMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AdminUser::getId, userIds)
               .select(AdminUser::getId, AdminUser::getNickname);
        List<AdminUser> users = adminUserMapper.selectList(wrapper);
        return users.stream()
                .collect(Collectors.toMap(AdminUser::getId, u -> u.getNickname() != null ? u.getNickname() : "未知用户"));
    }

    private String getOrderStatusDesc(Integer status) {
        if (AdminOrder.STATUS_TAKEN.equals(status)) return "已接单";
        if (AdminOrder.STATUS_COMPLETED.equals(status)) return "已完成（待确认）";
        if (AdminOrder.STATUS_CONFIRMED.equals(status)) return "已确认";
        if (AdminOrder.STATUS_CANCELLED.equals(status)) return "已取消";
        return "未知状态";
    }
}

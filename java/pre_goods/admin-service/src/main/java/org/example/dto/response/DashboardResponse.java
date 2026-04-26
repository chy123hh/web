package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仪表盘统计数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    /** 用户总数 */
    private Long totalUsers;

    /** 正常用户数 */
    private Long activeUsers;

    /** 封禁用户数 */
    private Long bannedUsers;

    /** 管理员数量 */
    private Long adminCount;

    /** 任务总数 */
    private Long totalTasks;

    /** 待接单任务数 */
    private Long pendingTasks;

    /** 进行中任务数 */
    private Long acceptedTasks;

    /** 已完成任务数 */
    private Long completedTasks;

    /** 已取消任务数 */
    private Long cancelledTasks;

    /** 订单总数 */
    private Long totalOrders;

    /** 已完成订单数 */
    private Long completedOrders;

    /** 待确认订单数 */
    private Long pendingConfirmOrders;
}

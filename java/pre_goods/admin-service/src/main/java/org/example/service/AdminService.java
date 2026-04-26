package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.dto.response.*;

/**
 * 后台管理服务接口
 */
public interface AdminService {

    /**
     * 获取仪表盘统计数据
     */
    DashboardResponse getDashboard();

    /**
     * 分页查询用户列表
     */
    Page<AdminUserResponse> getUserList(Integer page, Integer size, String keyword, Integer status);

    /**
     * 获取用户详情
     */
    AdminUserResponse getUserDetail(Long userId);

    /**
     * 封禁/解封用户
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 分页查询任务列表
     */
    Page<AdminTaskResponse> getTaskList(Integer page, Integer size, String status, String keyword);

    /**
     * 获取任务详情
     */
    AdminTaskResponse getTaskDetail(Long taskId);

    /**
     * 强制取消任务
     */
    void forceCancelTask(Long taskId);

    /**
     * 分页查询订单列表
     */
    Page<AdminOrderResponse> getOrderList(Integer page, Integer size, Integer status);

    /**
     * 获取订单详情
     */
    AdminOrderResponse getOrderDetail(Long orderId);
}

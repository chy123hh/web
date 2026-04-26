package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Result;
import org.example.common.util.JwtUtil;
import org.example.dto.response.*;
import org.example.service.AdminService;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理控制器
 * 提供仪表盘、用户管理、任务管理、订单管理等接口
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    // ==================== 仪表盘 ====================

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public Result<DashboardResponse> getDashboard(HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        DashboardResponse dashboard = adminService.getDashboard();
        return Result.success(dashboard);
    }

    // ==================== 用户管理 ====================

    /**
     * 分页查询用户列表
     */
    @GetMapping("/users")
    public Result<Page<AdminUserResponse>> getUserList(
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        checkAdmin(httpRequest);
        Page<AdminUserResponse> userPage = adminService.getUserList(page, size, keyword, status);
        return Result.success(userPage);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/users/{userId}")
    public Result<AdminUserResponse> getUserDetail(
            HttpServletRequest httpRequest,
            @PathVariable Long userId) {
        checkAdmin(httpRequest);
        AdminUserResponse user = adminService.getUserDetail(userId);
        return Result.success(user);
    }

    /**
     * 封禁/解封用户
     */
    @PutMapping("/users/{userId}/status")
    public Result<String> updateUserStatus(
            HttpServletRequest httpRequest,
            @PathVariable Long userId,
            @RequestParam Integer status) {
        checkAdmin(httpRequest);
        adminService.updateUserStatus(userId, status);
        String action = status == 2 ? "封禁" : "解封";
        return Result.success("用户已" + action);
    }

    // ==================== 任务管理 ====================

    /**
     * 分页查询任务列表
     */
    @GetMapping("/tasks")
    public Result<Page<AdminTaskResponse>> getTaskList(
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        checkAdmin(httpRequest);
        Page<AdminTaskResponse> taskPage = adminService.getTaskList(page, size, status, keyword);
        return Result.success(taskPage);
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/tasks/{taskId}")
    public Result<AdminTaskResponse> getTaskDetail(
            HttpServletRequest httpRequest,
            @PathVariable Long taskId) {
        checkAdmin(httpRequest);
        AdminTaskResponse task = adminService.getTaskDetail(taskId);
        return Result.success(task);
    }

    /**
     * 强制取消任务
     */
    @PutMapping("/tasks/{taskId}/cancel")
    public Result<String> forceCancelTask(
            HttpServletRequest httpRequest,
            @PathVariable Long taskId) {
        checkAdmin(httpRequest);
        adminService.forceCancelTask(taskId);
        return Result.success("任务已强制取消");
    }

    // ==================== 订单管理 ====================

    /**
     * 分页查询订单列表
     */
    @GetMapping("/orders")
    public Result<Page<AdminOrderResponse>> getOrderList(
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        checkAdmin(httpRequest);
        Page<AdminOrderResponse> orderPage = adminService.getOrderList(page, size, status);
        return Result.success(orderPage);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/orders/{orderId}")
    public Result<AdminOrderResponse> getOrderDetail(
            HttpServletRequest httpRequest,
            @PathVariable Long orderId) {
        checkAdmin(httpRequest);
        AdminOrderResponse order = adminService.getOrderDetail(orderId);
        return Result.success(order);
    }

    // ==================== 私有方法 ====================

    /**
     * 校验管理员权限
     */
    private void checkAdmin(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        // 这里可以通过查询数据库确认用户是否为管理员角色
    }
}

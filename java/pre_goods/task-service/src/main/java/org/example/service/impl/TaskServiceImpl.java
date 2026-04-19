package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Result;
import org.example.common.util.JwtUtil;
import org.example.dto.request.CreateTaskRequest;
import org.example.dto.request.UpdateTaskRequest;
import org.example.dto.response.TaskResponse;
import org.example.entity.Task;
import org.example.mapper.TaskMapper;
import org.example.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final TaskMapper taskMapper;
    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;

    @Override
    public Result createTask(CreateTaskRequest requestDto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Task task = new Task();
        BeanUtils.copyProperties(requestDto, task);
        task.setUserId(userId);
        task.setStatus(Task.STATUS_PENDING);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());

        boolean saved = save(task);
        if (saved) {
            TaskResponse response = convertToResponse(task);
            return Result.success("任务创建成功", response);
        }
        return Result.error(500, "任务创建失败");
    }

    @Override
    public Result updateTask(Long id, UpdateTaskRequest requestDto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Task task = getById(id);
        if (task == null) {
            return Result.error(404, "任务不存在");
        }

        if (!task.getUserId().equals(userId)) {
            return Result.error(403, "无权修改此任务");
        }

        if (!Task.STATUS_PENDING.equals(task.getStatus())) {
            return Result.error(400, "只能修改待接单状态的任务");
        }

        BeanUtils.copyProperties(requestDto, task);
        task.setUpdateTime(LocalDateTime.now());

        boolean updated = updateById(task);
        if (updated) {
            TaskResponse response = convertToResponse(task);
            return Result.success("任务更新成功", response);
        }
        return Result.error(500, "任务更新失败");
    }

    @Override
    public Result deleteTask(Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Task task = getById(id);
        if (task == null) {
            return Result.error(404, "任务不存在");
        }

        if (!task.getUserId().equals(userId)) {
            return Result.error(403, "无权删除此任务");
        }

        boolean removed = removeById(id);
        if (removed) {
            return Result.success("任务删除成功", null);
        }
        return Result.error(500, "任务删除失败");
    }

    @Override
    public Result getTaskById(Long id) {
        Task task = getById(id);
        if (task == null) {
            return Result.error(404, "任务不存在");
        }
        return Result.success(convertToResponse(task));
    }

    @Override
    public Result listAllTasks() {
        List<Task> tasks = list();
        List<TaskResponse> responses = tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    @Override
    public Result listTasksByStatus(String status) {
        List<Task> tasks = taskMapper.selectByStatus(status);
        List<TaskResponse> responses = tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    @Override
    public Result listMyTasks() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        List<Task> tasks = taskMapper.selectByUserId(userId);
        List<TaskResponse> responses = tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    @Override
    public Result listMyAcceptedTasks() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        List<Task> tasks = taskMapper.selectByAcceptorId(userId);
        List<TaskResponse> responses = tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    @Override
    public Result acceptTask(Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Task task = getById(id);
        if (task == null) {
            return Result.error(404, "任务不存在");
        }

        if (task.getUserId().equals(userId)) {
            return Result.error(400, "不能接自己发布的任务");
        }

        if (!Task.STATUS_PENDING.equals(task.getStatus())) {
            return Result.error(400, "该任务已被接单或已完成");
        }

        int updated = taskMapper.updateStatusAndAcceptor(id, Task.STATUS_ACCEPTED, userId);
        if (updated > 0) {
            return Result.success("任务接单成功", null);
        }
        return Result.error(500, "任务接单失败");
    }

    @Override
    public Result completeTask(Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Task task = getById(id);
        if (task == null) {
            return Result.error(404, "任务不存在");
        }

        if (!Task.STATUS_ACCEPTED.equals(task.getStatus())) {
            return Result.error(400, "只能完成已接单的任务");
        }

        if (!task.getAcceptorId().equals(userId)) {
            return Result.error(403, "只有接单人可以完成任务");
        }

        task.setStatus(Task.STATUS_COMPLETED);
        task.setUpdateTime(LocalDateTime.now());

        boolean updated = updateById(task);
        if (updated) {
            return Result.success("任务完成成功", null);
        }
        return Result.error(500, "任务完成失败");
    }

    @Override
    public Result cancelTask(Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Task task = getById(id);
        if (task == null) {
            return Result.error(404, "任务不存在");
        }

        if (!task.getUserId().equals(userId)) {
            return Result.error(403, "只有发布人可以取消任务");
        }

        if (Task.STATUS_COMPLETED.equals(task.getStatus())) {
            return Result.error(400, "已完成的任务不能取消");
        }

        task.setStatus(Task.STATUS_CANCELLED);
        task.setUpdateTime(LocalDateTime.now());

        boolean updated = updateById(task);
        if (updated) {
            return Result.success("任务取消成功", null);
        }
        return Result.error(500, "任务取消失败");
    }

    private Long getCurrentUserId() {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        token = token.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }

    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        BeanUtils.copyProperties(task, response);
        return response;
    }
}

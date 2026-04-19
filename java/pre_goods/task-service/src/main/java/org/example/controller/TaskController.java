package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Result;
import org.example.dto.request.CreateTaskRequest;
import org.example.dto.request.UpdateTaskRequest;
import org.example.service.TaskService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Tag(name = "任务管理", description = "任务发布、接单、完成、取消等接口")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "创建任务", description = "发布新任务")
    public Result createTask(@RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新任务", description = "修改任务信息（仅限待接单状态）")
    public Result updateTask(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务", description = "删除自己发布的任务")
    public Result deleteTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        return taskService.deleteTask(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取任务详情", description = "根据ID查询任务详情")
    public Result getTaskById(@Parameter(description = "任务ID") @PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有任务", description = "查询所有任务列表")
    public Result listAllTasks() {
        return taskService.listAllTasks();
    }

    @GetMapping("/listByStatus")
    @Operation(summary = "按状态查询任务", description = "根据状态查询任务列表")
    public Result listTasksByStatus(
            @Parameter(description = "任务状态：PENDING-待接单, ACCEPTED-已接单, COMPLETED-已完成, CANCELLED-已取消")
            @RequestParam String status) {
        return taskService.listTasksByStatus(status);
    }

    @GetMapping("/myTasks")
    @Operation(summary = "我发布的任务", description = "查询当前用户发布的所有任务")
    public Result listMyTasks() {
        return taskService.listMyTasks();
    }

    @GetMapping("/myAcceptedTasks")
    @Operation(summary = "我接的任务", description = "查询当前用户接单的所有任务")
    public Result listMyAcceptedTasks() {
        return taskService.listMyAcceptedTasks();
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "接单", description = "接受任务")
    public Result acceptTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        return taskService.acceptTask(id);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "完成任务", description = "标记任务为已完成")
    public Result completeTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        return taskService.completeTask(id);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消任务", description = "取消发布的任务")
    public Result cancelTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        return taskService.cancelTask(id);
    }
}

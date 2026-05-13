package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.Result;
import org.example.dto.request.CreateTaskRequest;
import org.example.dto.request.UpdateTaskRequest;
import org.example.service.TaskService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public Result createTask(@RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}")
    public Result updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    public Result deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id);
    }

    @GetMapping("/{id}")
    public Result getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping("/list")
    public Result listAllTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return taskService.listAllTasks(page, size);
    }

    @GetMapping("/listByStatus")
    public Result listTasksByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return taskService.listTasksByStatus(status, page, size);
    }

    @GetMapping("/myTasks")
    public Result listMyTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return taskService.listMyTasks(page, size);
    }

    @GetMapping("/myAcceptedTasks")
    public Result listMyAcceptedTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return taskService.listMyAcceptedTasks(page, size);
    }

    @PostMapping("/{id}/accept")
    public Result acceptTask(@PathVariable Long id) {
        return taskService.acceptTask(id);
    }

    @PostMapping("/{id}/complete")
    public Result completeTask(@PathVariable Long id) {
        return taskService.completeTask(id);
    }

    @PostMapping("/{id}/cancel")
    public Result cancelTask(@PathVariable Long id) {
        return taskService.cancelTask(id);
    }
}

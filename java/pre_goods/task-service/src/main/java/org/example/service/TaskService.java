package org.example.service;

import org.example.common.dto.Result;
import org.example.dto.request.CreateTaskRequest;
import org.example.dto.request.UpdateTaskRequest;

public interface TaskService {

    Result createTask(CreateTaskRequest request);

    Result updateTask(Long id, UpdateTaskRequest request);

    Result deleteTask(Long id);

    Result getTaskById(Long id);

    Result listAllTasks();

    Result listTasksByStatus(String status);

    Result listMyTasks();

    Result listMyAcceptedTasks();

    Result acceptTask(Long id);

    Result completeTask(Long id);

    Result cancelTask(Long id);
}

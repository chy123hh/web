package org.example.service;

import org.example.common.dto.Result;
import org.example.dto.request.CreateTaskRequest;
import org.example.dto.request.UpdateTaskRequest;

public interface TaskService {

    Result createTask(CreateTaskRequest request);

    Result updateTask(Long id, UpdateTaskRequest request);

    Result deleteTask(Long id);

    Result getTaskById(Long id);

    Result listAllTasks(Integer page, Integer size);

    Result listTasksByStatus(String status, Integer page, Integer size);

    Result listMyTasks(Integer page, Integer size);

    Result listMyAcceptedTasks(Integer page, Integer size);

    Result acceptTask(Long id);

    Result completeTask(Long id);

    Result cancelTask(Long id);
}

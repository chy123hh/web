package org.example.feign.client;

import org.example.common.dto.Result;
import org.example.common.dto.response.TaskResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 任务服务 Feign 客户端接口
 * 用于调用 task-service 提供的 HTTP 接口
 */
@FeignClient(
    name = "task-service",  // 服务名称，从 Nacos 获取
    fallback = TaskClientFallback.class  // 降级处理类
)
public interface TaskClient {

    /**
     * 根据任务 ID 获取任务信息
     *
     * @param taskId 任务 ID
     * @return 任务信息
     */
    @GetMapping("/api/task/{taskId}")
    Result<TaskResponse> getTaskById(@PathVariable("taskId") Long taskId);
}

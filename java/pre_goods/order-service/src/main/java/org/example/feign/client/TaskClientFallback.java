package org.example.feign.client;

import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Result;
import org.example.common.dto.response.TaskResponse;
import org.springframework.stereotype.Component;

/**
 * TaskClient 的降级处理类
 * 当 task-service 不可用时，提供友好的降级响应
 */
@Slf4j
@Component
public class TaskClientFallback implements TaskClient {

    @Override
    public Result<TaskResponse> getTaskById(Long taskId) {
        log.error("调用 task-service 获取任务信息失败，taskId: {}", taskId);
        return Result.error(503, "任务服务暂时不可用，请稍后重试");
    }
}

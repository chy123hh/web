package org.example.feign.client;

import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Result;
import org.example.common.dto.response.UserProfileResponse;
import org.springframework.stereotype.Component;

/**
 * UserClient 的降级处理类
 * 当 user-service 不可用时，提供友好的降级响应
 */
@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public Result<UserProfileResponse> getUserProfile(Long userId) {
        log.error("调用 user-service 获取用户信息失败，userId: {}", userId);
        return Result.error(503, "用户服务暂时不可用，请稍后重试");
    }

    @Override
    public Result<UserProfileResponse> getUserByStudentId(String studentId) {
        log.error("调用 user-service 获取用户信息失败，studentId: {}", studentId);
        return Result.error(503, "用户服务暂时不可用，请稍后重试");
    }
}

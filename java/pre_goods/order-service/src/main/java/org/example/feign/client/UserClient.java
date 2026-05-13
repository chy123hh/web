package org.example.feign.client;

import org.example.common.dto.Result;
import org.example.common.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign 客户端接口
 * 用于调用 user-service 提供的 HTTP 接口
 */
@FeignClient(
    name = "user-service",  // 服务名称，从 Nacos 获取
    fallback = UserClientFallback.class  // 降级处理类
)
public interface UserClient {

    /**
     * 根据用户 ID 获取用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    @GetMapping("/api/user/{userId}/profile")
    Result<UserProfileResponse> getUserProfile(@PathVariable("userId") Long userId);

    /**
     * 根据学号获取用户信息
     *
     * @param studentId 学号
     * @return 用户信息
     */
    @GetMapping("/api/user/student/{studentId}")
    Result<UserProfileResponse> getUserByStudentId(@PathVariable("studentId") String studentId);
}

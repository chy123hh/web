package org.example.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Result;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理类
 * 当服务触发限流或熔断时，返回降级响应
 */
@Slf4j
@Component
public class UserBlockHandler {

    /**
     * 登录方法降级处理
     */
    public static Result loginBlockHandler(BlockException ex) {
        log.warn("登录接口触发限流/熔断: {}", ex.getClass().getSimpleName());
        return Result.error(429, "登录服务繁忙，请稍后重试");
    }

    /**
     * 注册方法降级处理
     */
    public static Result registerBlockHandler(BlockException ex) {
        log.warn("注册接口触发限流/熔断: {}", ex.getClass().getSimpleName());
        return Result.error(429, "注册服务繁忙，请稍后重试");
    }

    /**
     * 获取用户信息降级处理
     */
    public static Result profileBlockHandler(BlockException ex) {
        log.warn("获取用户信息接口触发限流/熔断: {}", ex.getClass().getSimpleName());
        return Result.error(429, "用户服务繁忙，请稍后重试");
    }

    /**
     * 获取用户积分降级处理
     */
    public static Result pointsBlockHandler(BlockException ex) {
        log.warn("获取用户积分接口触发限流/熔断: {}", ex.getClass().getSimpleName());
        return Result.error(429, "积分服务繁忙，请稍后重试");
    }

    /**
     * 获取用户信用降级处理
     */
    public static Result creditBlockHandler(BlockException ex) {
        log.warn("获取用户信用接口触发限流/熔断: {}", ex.getClass().getSimpleName());
        return Result.error(429, "信用服务繁忙，请稍后重试");
    }

    /**
     * 通用降级处理方法
     */
    public static Result defaultBlockHandler(String methodName, BlockException ex) {
        log.warn("用户服务接口 [{}] 触发限流/熔断: {}", methodName, ex.getClass().getSimpleName());
        return Result.error(429, "服务繁忙，请稍后重试");
    }
}

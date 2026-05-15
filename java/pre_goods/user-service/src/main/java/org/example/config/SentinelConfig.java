package org.example.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Sentinel 配置类
 * 配置限流、熔断、降级的全局异常处理器
 */
@Slf4j
@Configuration
public class SentinelConfig {

    /**
     * 配置 Sentinel 的限流/熔断异常处理器
     * 当触发限流或熔断时，返回统一的 JSON 格式响应
     */
    @Bean
    public BlockExceptionHandler sentinelBlockExceptionHandler() {
        return (HttpServletRequest request, HttpServletResponse response, BlockException e) -> {
            log.warn("Sentinel 拦截: uri={}, exception={}", request.getRequestURI(), e.getClass().getSimpleName());

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> result = new HashMap<>();
            result.put("code", 429);
            result.put("message", "请求过于频繁，请稍后重试");
            result.put("data", null);

            if (e instanceof FlowException) {
                result.put("code", 429);
                result.put("message", "服务限流，请稍后重试");
            } else if (e instanceof DegradeException) {
                result.put("code", 503);
                result.put("message", "服务降级，功能暂时不可用");
            }

            response.getWriter().write(
                new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(result)
            );
        };
    }

    /**
     * 配置请求来源解析器
     * 用于区分不同来源的请求，实现更精细的限流控制
     */
    @Bean
    public RequestOriginParser requestOriginParser() {
        return (HttpServletRequest request) -> {
            String origin = request.getHeader("X-Request-Origin");
            if (origin != null && !origin.isEmpty()) {
                return origin;
            }
            return request.getRemoteAddr();
        };
    }
}

package org.example.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 客户端配置
 * 用于在微服务间传递认证信息等请求头
 * 实现服务间调用的 Token 传递
 */
@Slf4j
@Configuration
public class FeignConfig {

    /**
     * 配置请求拦截器，传递认证信息
     * 将网关传递过来的用户信息和 Token 转发给下游服务
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();

                    // 传递用户 ID（网关认证后放入请求头）
                    String userId = request.getHeader("X-User-Id");
                    if (userId != null) {
                        template.header("X-User-Id", userId);
                        log.debug("Feign 传递用户ID: {}", userId);
                    }

                    // 传递完整的 JWT Token（网关添加的 X-User-Token）
                    // 这样下游服务可以独立验证 Token
                    String token = request.getHeader("X-User-Token");
                    if (token != null) {
                        template.header("Authorization", "Bearer " + token);
                        template.header("X-User-Token", token);
                        log.debug("Feign 传递 Token: {}", token.substring(0, Math.min(20, token.length())) + "...");
                    }

                    // 传递用户名
                    String userName = request.getHeader("X-User-Name");
                    if (userName != null) {
                        template.header("X-User-Name", userName);
                    }

                    // 传递请求追踪 ID（用于链路追踪）
                    String requestId = request.getHeader("X-Request-Id");
                    if (requestId != null) {
                        template.header("X-Request-Id", requestId);
                    }

                    log.debug("Feign 请求拦截器完成，请求路径: {}, 用户ID: {}", 
                            template.url(), userId);
                }
            }
        };
    }
}

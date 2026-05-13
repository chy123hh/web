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
 */
@Slf4j
@Configuration
public class FeignConfig {

    /**
     * 配置请求拦截器，传递认证信息
     * 将网关传递过来的用户信息转发给下游服务
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
                    }

                    // 传递 Token（如果需要）
                    String token = request.getHeader("Authorization");
                    if (token != null) {
                        template.header("Authorization", token);
                    }

                    log.debug("Feign 请求拦截器：传递用户信息，userId: {}", userId);
                }
            }
        };
    }
}

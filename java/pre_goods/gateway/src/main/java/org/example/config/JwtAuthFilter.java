package org.example.config;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.common.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证过滤器 - 全局过滤器
 * 从请求头中解析 JWT Token 并验证
 * 在网关层面完成认证，避免每个服务都配置认证过滤器
 */
@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

  @Resource
  private JwtUtil jwtUtil;

  /**
   * 放行的路径列表（不需要认证）
   */
  private static final List<String> SKIP_PATHS = Arrays.asList(
      "/user/login",
      "/user/register",
      "/actuator/health",
      "/favicon.ico");

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();

    // 获取请求路径
    String path = request.getPath().value();

    // 判断是否需要跳过认证
    if (isSkipPath(path)) {
      log.debug("跳过认证路径：{}", path);
      return chain.filter(exchange);
    }

    // 从请求头获取 Authorization
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    // 验证 Token
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.warn("认证失败 - 缺少有效的 Authorization 头，路径：{}", path);
      return unAuthorized(response, "未授权：缺少有效的 Authorization 头");
    }

    String token = authHeader.substring(7);

    try {
      // 解析 Token
      Claims claims = jwtUtil.parseToken(token);
      Long userId = claims.get("userId", Long.class);

      if (userId == null) {
        log.warn("认证失败 - Token 中缺少 userId，路径：{}", path);
        return unAuthorized(response, "未授权：Token 中缺少 userId");
      }

      // 将用户信息传递到下游服务
      ServerHttpRequest mutatedRequest = request.mutate()
          .header("X-User-Id", String.valueOf(userId))
          .header("X-User-Token", token)
          .build();

      log.debug("JWT 认证成功，用户 ID: {}, 路径：{}", userId, path);

      return chain.filter(exchange.mutate().request(mutatedRequest).build());
    } catch (Exception e) {
      log.error("JWT 解析失败：{}, 路径：{}", e.getMessage(), path);
      return unAuthorized(response, "未授权：JWT 解析失败 - " + e.getMessage());
    }
  }

  /**
   * 判断是否需要跳过认证
   *
   * @param path 请求路径
   * @return 是否跳过
   */
  private boolean isSkipPath(String path) {
    return SKIP_PATHS.stream().anyMatch(skipPath -> path.startsWith(skipPath));
  }

  /**
   * 返回未授权响应
   *
   * @param response 响应对象
   * @param message  错误信息
   * @return Mono<Void>
   */
  private Mono<Void> unAuthorized(ServerHttpResponse response, String message) {
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
    String body = "{\"code\":401,\"message\":\"" + message + "\",\"data\":null}";
    return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
  }

  @Override
  public int getOrder() {
    // 设置过滤器优先级，数字越小优先级越高
    // -100 确保在其他过滤器之前执行
    return -100;
  }
}

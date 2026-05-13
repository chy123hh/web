# OpenFeign 服务间调用指南

## ✅ 已完成的配置

### 1. 依赖配置

所有需要服务间调用的服务已添加以下依赖：

```xml
<!-- Spring Cloud OpenFeign (服务间调用) -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- Spring Cloud LoadBalancer (负载均衡) -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

### 2. 父 POM 配置

已添加 Spring Cloud 版本管理：

```xml
<properties>
    <spring-cloud.version>2022.0.4</spring-cloud.version>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- Spring Cloud 依赖管理 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

---

## 📋 Feign Client 使用示例

### Order Service 调用 User Service

#### 1. Feign Client 接口

```java
@FeignClient(
    name = "user-service",  // 服务名称，从 Nacos 获取
    fallback = UserClientFallback.class  // 降级处理类
)
public interface UserClient {

    @GetMapping("/api/user/{userId}/profile")
    Result<UserProfileResponse> getUserProfile(@PathVariable("userId") Long userId);

    @GetMapping("/api/user/student/{studentId}")
    Result<UserProfileResponse> getUserByStudentId(@PathVariable("studentId") String studentId);
}
```

#### 2. 降级处理类

```java
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
```

#### 3. 启动类配置

```java
@SpringBootApplication
@EnableFeignClients(basePackages = "org.example.feign.client")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

#### 4. Feign 配置类

```java
@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // 传递用户 ID（网关认证后放入请求头）
                String userId = request.getHeader("X-User-Id");
                if (userId != null) {
                    template.header("X-User-Id", userId);
                }

                // 传递 Token
                String token = request.getHeader("Authorization");
                if (token != null) {
                    template.header("Authorization", token);
                }
            }
        };
    }
}
```

#### 5. 在 Service 中使用

```java
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final UserClient userClient;  // 注入 Feign Client

    @Override
    public Result takeOrder(Long taskId, Long publisherId, Integer rewardPoints) {
        // 调用 User Service 获取发布者信息
        Result<UserProfileResponse> userResult = userClient.getUserProfile(publisherId);
        
        if (userResult.getCode() != 200) {
            return Result.error(500, "获取用户信息失败");
        }
        
        UserProfileResponse publisher = userResult.getData();
        
        // 验证用户积分等逻辑
        if (publisher.getPoints() < rewardPoints) {
            return Result.error(400, "发布者积分不足");
        }
        
        // ... 其他业务逻辑
        return Result.success("接单成功");
    }
}
```

---

## 🔧 Feign 配置说明

### application.yml 配置

```yaml
# Feign 配置
feign:
  client:
    config:
      default:  # 默认配置
        connectTimeout: 5000  # 连接超时时间（毫秒）
        readTimeout: 10000    # 读取超时时间（毫秒）
        loggerLevel: BASIC    # 日志级别
  compression:
    request:
      enabled: true  # 启用请求压缩
      mime-types: text/xml,application/xml,application/json
      min-request-size: 2048
    response:
      enabled: true  # 启用响应压缩

# 负载均衡器配置
spring:
  cloud:
    loadbalancer:
      enabled: true
      ribbon:
        enabled: false  # 禁用 Ribbon
```

### 日志级别说明

| 级别 | 说明 |
|------|------|
| `NONE` | 不记录任何日志 |
| `BASIC` | 记录请求方法和 URL，以及响应状态码和执行时间 |
| `HEADERS` | 在 BASIC 基础上增加请求和响应头 |
| `FULL` | 记录完整的请求和响应信息（包括 body） |

---

## 📊 服务间调用关系图

```
┌─────────────────┐
│   Gateway       │
│   (8080)        │
└────────┬────────┘
         │
         │ 请求（带 X-User-Id 头）
         ↓
┌─────────────────┐
│  Order Service  │
│   (8082)        │
│                 │
│  ┌──────────┐   │
│  │ Feign    │   │
│  │ Client   │   │
│  └────┬─────┘   │
└───────┼─────────┘
        │
        │ Feign 调用（自动传递 X-User-Id）
        ↓
┌─────────────────┐
│   User Service  │
│   (8081)        │
│                 │
│  ┌──────────┐   │
│  │ 用户信息 │   │
│  └──────────┘   │
└─────────────────┘
```

---

## 🎯 最佳实践

### 1. Feign Client 命名

```java
// ✅ 推荐：清晰表达服务功能
@FeignClient(name = "user-service", fallback = UserClientFallback.class)
public interface UserClient { }

// ❌ 不推荐：命名不清晰
@FeignClient(name = "abc-service")
public interface AbcClient { }
```

### 2. 降级处理

```java
// ✅ 推荐：提供友好的降级响应
@Slf4j
@Component
public class UserClientFallback implements UserClient {
    @Override
    public Result<UserProfileResponse> getUserProfile(Long userId) {
        log.error("调用失败，userId: {}", userId);
        return Result.error(503, "服务暂时不可用");
    }
}

// ❌ 不推荐：抛出异常或返回 null
public class UserClientFallback implements UserClient {
    @Override
    public Result<UserProfileResponse> getUserProfile(Long userId) {
        throw new RuntimeException("调用失败");  // 不友好
    }
}
```

### 3. 请求头传递

```java
// ✅ 推荐：传递必要的认证信息
@Bean
public RequestInterceptor requestInterceptor() {
    return template -> {
        HttpServletRequest request = getRequest();
        String userId = request.getHeader("X-User-Id");
        if (userId != null) {
            template.header("X-User-Id", userId);
        }
    };
}
```

### 4. 超时配置

```yaml
# ✅ 推荐：根据业务设置合理的超时时间
feign:
  client:
    config:
      default:
        connectTimeout: 5000   # 5 秒连接超时
        readTimeout: 10000     # 10 秒读取超时

# ❌ 不推荐：超时时间过短或过长
feign:
  client:
    config:
      default:
        connectTimeout: 500    # 500ms 太短
        readTimeout: 60000     # 60 秒太长
```

---

## 🔍 常见问题排查

### 1. Feign Client 无法注入

**错误信息**: `NoSuchBeanDefinitionException`

**解决方案**:
- ✅ 检查启动类是否有 `@EnableFeignClients`
- ✅ 检查 `basePackages` 是否包含 Feign Client 所在包
- ✅ 检查 Feign Client 接口是否有 `@FeignClient` 注解

### 2. 服务调用失败

**错误信息**: `FeignException$ServiceUnavailable`

**解决方案**:
- ✅ 检查目标服务是否已注册到 Nacos
- ✅ 检查服务名是否正确
- ✅ 检查网络是否通畅
- ✅ 查看降级日志

### 3. 请求头未传递

**问题**: 下游服务无法获取用户信息

**解决方案**:
- ✅ 配置 `RequestInterceptor`
- ✅ 检查网关是否正确设置请求头
- ✅ 检查 Feign 配置是否生效

---

## 📖 参考资料

- [Spring Cloud OpenFeign 官方文档](https://spring.io/projects/spring-cloud-openfeign)
- [Spring Cloud LoadBalancer](https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#spring-cloud-loadbalancer)
- [OpenFeign GitHub](https://github.com/OpenFeign/feign)

---

**配置完成时间**: 2026-05-13  
**配置版本**: v1.0  
**配置状态**: ✅ 完成

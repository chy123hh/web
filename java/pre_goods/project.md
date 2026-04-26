## 项目
### pre_goods
* 统一父工程
* 用于管理各个模块
    ```xml
    <modules>
        <module>common</module>// 模块1
        <module>user-service</module>// 模块2
    </modules>
    ```
* 所有模块的父工程
* 管理子中的maven依赖
  ```xml
  <dependencyManagement>// 用于管理依赖版本，在子模块中可以不指定模块版本
    <dependencies>
        <!-- 还可以管理第三方库版本 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus</artifactId>
            <version>3.5.7</version>
        </dependency>
    </dependencies>
  </dependencyManagement>
  ```
* dependencies //强制导入依赖
### common

用于管理统一的工具
* 继承父工程
    ```xml
    <parent>
        <groupId>org.example</groupId>
        <artifactId>pre_goods</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <artifactId>common</artifactId>
    ```
#### Result

统一的返回结果
* 通过Result类统一返回结果
   ```java
   public class Result<T> {
       private int code;
       private String message;
       private T data;
       public Result(int code, String message, T data) {
           this.code = code;
           this.message = message;
           this.data = data;
       }
   }
   ```
#### 全局异常处理类
1. 创建一个类统一处理异常
    ```java
    @ControllerAdvice // 统一处理异常
    public class GlobalExceptionHandler {
        @ExceptionHandler(Exception.class) // 处理异常的类型
        public ResponseEntity<Result<Object>> handleException(Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Result.error(500, e.getMessage(), null));
        }
    }
    ```
* 通过 @ControllerAdvice 来统一处理异常
* @ExceptionHandler 来处理异常
* ResponseEntity 来返回结果
2. 自定义异常类型
    ```java
    public class CustomException extends RuntimeException {
        public CustomException(Integer code,String message) {
            super(message);
            this.message = message;
            this.code = 1001;
        }
    }
   ```
* 要实现构造函数
#### 工具类
1. jwt登录认证
    * 创建token
    * 验证token
    * 解析token
    * 验证token是否有效
### user-service

#### Security 认证配置
1. 设置放行接口：登录和注册无需认证
2. 配置 Spring Security 拦截规则
3. 配置 JWT 拦截器在用户名密码认证过滤器之前
4. 在 JWT 拦截器中创建认证对象并设置认证信息
```java
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

UsernamePasswordAuthenticationToken authentication =
    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
SecurityContextHolder.getContext().setAuthentication(authentication);
```

#### 接口列表

##### 1. 用户注册
- **接口**: `POST /user/register`
- **描述**: 使用学号、密码、昵称注册新用户
- **请求参数**:
```json
{
  "studentId": "2024003",    // 学号，必填，6-20字符
  "password": "123456",      // 密码，必填，6-50字符
  "nickname": "测试用户"      // 昵称，选填，最大50字符
}
```
- **响应结果**:
```json
// 成功
{
  "code": 200,
  "message": "success",
  "data": 10    // 新用户ID
}

// 失败 - 学号已存在
{
  "code": 2001,
  "message": "学号已存在",
  "data": null
}
```

##### 2. 用户登录
- **接口**: `POST /user/login`
- **描述**: 通过学号和密码登录，返回 JWT token
- **请求参数**:
```json
{
  "studentId": "2024003",    // 学号，必填
  "password": "123456"       // 密码，必填
}
```
- **响应结果**:
```json
// 成功
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 10,
    "studentId": "2024003",
    "nickname": "测试用户"
  }
}

// 失败 - 密码错误
{
  "code": 2001,
  "message": "学号或密码错误",
  "data": null
}
```

##### 3. 获取个人资料
- **接口**: `GET /user/profile`
- **描述**: 获取当前登录用户的详细资料
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 10,
    "studentId": "2024003",
    "nickname": "测试用户",
    "avatarUrl": "http://example.com/avatar.jpg",
    "points": 0,               // 积分余额
    "creditScore": 100,        // 信用分
    "role": 0,                 // 角色：0普通用户 1管理员
    "status": 1,               // 状态：1正常 2封禁
    "createTime": "2026-04-19T16:03:44"
  }
}
```

##### 4. 更新个人资料
- **接口**: `PUT /user/profile`
- **描述**: 更新用户昵称和头像
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```json
{
  "nickname": "更新后的昵称",        // 昵称，选填，最大50字符
  "avatarUrl": "http://example.com/avatar.jpg"  // 头像URL，选填，最大255字符
}
```
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": "用户资料更新成功"
}
```

##### 5. 查询积分余额
- **接口**: `GET /user/points`
- **描述**: 获取当前用户的积分余额
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 10,
    "points": 0
  }
}
```

##### 6. 查询信用分
- **接口**: `GET /user/credit`
- **描述**: 获取当前用户的信用分及是否可接单
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 10,
    "creditScore": 100,      // 信用分
    "canTakeOrder": true     // 是否可以接单（信用分 >= 60）
  }
}
```
### task-service

任务服务模块，提供任务发布、接单、完成、取消等功能。

#### 任务状态说明
| 状态 | 说明 |
|------|------|
| PENDING | 待接单 |
| ACCEPTED | 已接单 |
| COMPLETED | 已完成 |
| CANCELLED | 已取消 |

#### 任务类型说明
| 类型 | 说明 |
|------|------|
| DELIVERY | 代取快递 |
| PURCHASE | 代买 |
| OTHER | 其他 |

#### 接口列表

##### 1. 创建任务
- **接口**: `POST /task`
- **描述**: 发布新任务
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```json
{
  "title": "帮我取快递",           // 任务标题，必填
  "description": "在菜鸟驿站",      // 任务描述，选填
  "type": "DELIVERY",              // 任务类型：DELIVERY/PURCHASE/OTHER，必填
  "reward": 5.00,                  // 任务报酬，必填
  "pickupLocation": "菜鸟驿站",     // 取货地点，选填
  "deliveryLocation": "3号楼201",  // 送货地点，必填
  "deadline": "2026-04-20T18:00:00" // 截止时间，选填
}
```
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": 1    // 新任务ID
}
```

##### 2. 更新任务
- **接口**: `PUT /task/{id}`
- **描述**: 修改任务信息（仅限待接单状态的任务）
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```json
{
  "title": "更新后的标题",          // 任务标题，选填
  "description": "更新后的描述",     // 任务描述，选填
  "type": "PURCHASE",              // 任务类型，选填
  "reward": 10.00,                 // 任务报酬，选填
  "pickupLocation": "超市",         // 取货地点，选填
  "deliveryLocation": "5号楼301",  // 送货地点，选填
  "deadline": "2026-04-21T20:00:00" // 截止时间，选填
}
```
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": "任务更新成功"
}
```

##### 3. 删除任务
- **接口**: `DELETE /task/{id}`
- **描述**: 删除自己发布的任务
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": "任务删除成功"
}
```

##### 4. 获取任务详情
- **接口**: `GET /task/{id}`
- **描述**: 根据ID查询任务详情
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 10,
    "title": "帮我取快递",
    "description": "在菜鸟驿站",
    "type": "DELIVERY",
    "reward": 5.00,
    "pickupLocation": "菜鸟驿站",
    "deliveryLocation": "3号楼201",
    "status": "PENDING",
    "acceptorId": null,
    "createTime": "2026-04-19T16:30:00",
    "updateTime": "2026-04-19T16:30:00",
    "deadline": "2026-04-20T18:00:00"
  }
}
```

##### 5. 获取所有任务
- **接口**: `GET /task/list`
- **描述**: 查询所有任务列表
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 10,
      "title": "帮我取快递",
      "description": "在菜鸟驿站",
      "type": "DELIVERY",
      "reward": 5.00,
      "pickupLocation": "菜鸟驿站",
      "deliveryLocation": "3号楼201",
      "status": "PENDING",
      "acceptorId": null,
      "createTime": "2026-04-19T16:30:00",
      "updateTime": "2026-04-19T16:30:00",
      "deadline": "2026-04-20T18:00:00"
    }
  ]
}
```

##### 6. 按状态查询任务
- **接口**: `GET /task/listByStatus`
- **描述**: 根据状态查询任务列表
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**: `status=PENDING` (状态：PENDING/ACCEPTED/COMPLETED/CANCELLED)
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 10,
      "title": "帮我取快递",
      "status": "PENDING"
    }
  ]
}
```

##### 7. 查询我发布的任务
- **接口**: `GET /task/myTasks`
- **描述**: 查询当前用户发布的所有任务
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 10,
      "title": "帮我取快递",
      "status": "PENDING"
    }
  ]
}
```

##### 8. 查询我接的任务
- **接口**: `GET /task/myAcceptedTasks`
- **描述**: 查询当前用户接单的所有任务
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "userId": 11,
      "title": "帮我买饭",
      "status": "ACCEPTED",
      "acceptorId": 10
    }
  ]
}
```

##### 9. 接单
- **接口**: `POST /task/{id}/accept`
- **描述**: 接受任务（任务状态变为ACCEPTED）
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": "接单成功"
}
```

##### 10. 完成任务
- **接口**: `POST /task/{id}/complete`
- **描述**: 标记任务为已完成（任务状态变为COMPLETED）
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": "任务完成成功"
}
```

##### 11. 取消任务
- **接口**: `POST /task/{id}/cancel`
- **描述**: 取消发布的任务（任务状态变为CANCELLED）
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": "任务取消成功"
}
```

#### 状态码说明
| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 2001 | 学号已存在 / 学号或密码错误 |
| 401 | 未登录或 token 无效 |
| 500 | 服务器内部错误 |

### order-service

订单服务模块，提供接单、取消接单、上传完成凭证、确认完成等功能。

#### 订单状态说明
| 状态 | 说明 |
|------|------|
| 1 | 已接单 |
| 2 | 已完成（待确认） |
| 3 | 已确认 |
| 4 | 已取消 |

#### 接口列表

##### 1. 接单
- **接口**: `POST /order/take/{taskId}`
- **描述**: 接受任务并创建订单（使用Redis分布式锁防止并发接单）
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```
publisherId: 8      // 发布者ID，必填
rewardPoints: 10    // 悬赏积分，必填
```
- **响应结果**:
```json
// 成功
{
  "code": 200,
  "message": "success",
  "data": "ORD1745112345678ABCD"    // 订单号
}

// 失败 - 不能接自己的任务
{
  "code": 400,
  "message": "不能接自己发布的任务",
  "data": null
}

// 失败 - 任务已被接单
{
  "code": 400,
  "message": "该任务已被接单",
  "data": null
}

// 失败 - 并发冲突
{
  "code": 400,
  "message": "该任务正在被其他用户接单，请稍后再试",
  "data": null
}
```

##### 2. 取消接单
- **接口**: `PUT /order/cancel/{orderId}`
- **描述**: 取消已接的订单（仅限接单人且订单状态为已接单）
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
// 成功
{
  "code": 200,
  "message": "success",
  "data": "取消接单成功"
}

// 失败 - 无权操作
{
  "code": 403,
  "message": "只有接单人可以取消订单",
  "data": null
}

// 失败 - 状态不允许
{
  "code": 400,
  "message": "只能取消已接单的订单",
  "data": null
}
```

##### 3. 上传完成凭证
- **接口**: `POST /order/complete-proof/{orderId}`
- **描述**: 接单人上传任务完成的凭证图片
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
```json
{
  "proofImageUrl": "http://example.com/proof.jpg"    // 凭证图片URL，必填
}
```
- **响应结果**:
```json
// 成功
{
  "code": 200,
  "message": "success",
  "data": "上传完成凭证成功"
}

// 失败 - 无权操作
{
  "code": 403,
  "message": "只有接单人可以上传完成凭证",
  "data": null
}
```

##### 4. 确认完成
- **接口**: `PUT /order/confirm/{orderId}`
- **描述**: 发布者确认任务完成，积分转移给接单人
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
// 成功
{
  "code": 200,
  "message": "success",
  "data": "确认完成成功，积分已转移"
}

// 失败 - 无权操作
{
  "code": 403,
  "message": "只有发布者可以确认完成",
  "data": null
}

// 失败 - 状态不允许
{
  "code": 400,
  "message": "只能确认已上传凭证的订单",
  "data": null
}
```

##### 5. 查询我接的订单
- **接口**: `GET /order/my-taken`
- **描述**: 查询当前用户接单的所有订单
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "orderNo": "ORD1745112345678ABCD",
      "taskId": 1,
      "takerId": 7,
      "publisherId": 8,
      "rewardPoints": 10,
      "status": 1,
      "statusDesc": "已接单",
      "completeProofUrl": null,
      "confirmTime": null,
      "createTime": "2026-04-20T10:30:00",
      "updateTime": "2026-04-20T10:30:00"
    }
  ]
}
```

##### 6. 查询我发布的订单
- **接口**: `GET /order/my-published`
- **描述**: 查询当前用户发布的任务对应的接单记录
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "orderNo": "ORD1745112345678ABCD",
      "taskId": 1,
      "takerId": 7,
      "publisherId": 8,
      "rewardPoints": 10,
      "status": 2,
      "statusDesc": "已完成（待确认）",
      "completeProofUrl": "http://example.com/proof.jpg",
      "confirmTime": null,
      "createTime": "2026-04-20T10:30:00",
      "updateTime": "2026-04-20T10:35:00"
    }
  ]
}
```

#### 技术特性

1. **Redis分布式锁**: 使用 `help:order:lock:{taskId}` 作为锁key，防止同一任务被多人同时接单
2. **订单号生成**: 格式为 `ORD` + 时间戳 + 4位随机字符
3. **状态流转**: 已接单 → 已完成(待确认) → 已确认
4. **权限控制**: 
   - 只有接单人可以取消订单和上传凭证
   - 只有发布者可以确认完成
   - 不能接自己发布的任务

### evaluation-service

评价服务模块，提供任务完成后的评价功能，支持双向评价（发布者评价接单人、接单人评价发布者）。

#### 评价说明
| 类型 | 说明 |
|------|------|
| 1 | 对接单人评价（发布者评价） |
| 2 | 对发布者评价（接单人评价） |

#### 接口列表

##### 1. 创建评价
- **接口**: `POST /evaluation`
- **描述**: 对完成的订单进行评价
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```json
{
  "taskId": 1,              // 任务ID，必填
  "orderId": 1,             // 订单ID，必填
  "evaluatedId": 8,         // 被评价人ID，必填
  "rating": 5,              // 评分（1-5星），必填
  "content": "非常棒！",     // 评价内容，必填
  "type": 1                 // 评价类型：1-对接单人评价，2-对发布者评价，必填
}
```
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": 4    // 评价ID
}
```

##### 2. 获取评价详情
- **接口**: `GET /evaluation/{id}`
- **描述**: 根据ID查询评价详情
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "taskId": 1,
    "orderId": 1,
    "evaluatorId": 8,
    "evaluatedId": 7,
    "rating": 5,
    "content": "非常靠谱，很快就完成了任务！",
    "type": 1,
    "typeDesc": "对接单人评价",
    "createTime": "2026-04-20T15:30:00"
  }
}
```

##### 3. 查询我收到的评价
- **接口**: `GET /evaluation/received`
- **描述**: 查询当前用户收到的所有评价
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "taskId": 1,
      "orderId": 1,
      "evaluatorId": 8,
      "evaluatedId": 7,
      "rating": 5,
      "content": "非常靠谱，很快就完成了任务！",
      "type": 1,
      "typeDesc": "对接单人评价",
      "createTime": "2026-04-20T15:30:00"
    }
  ]
}
```

##### 4. 查询我发布的评价
- **接口**: `GET /evaluation/given`
- **描述**: 查询当前用户发布的所有评价
- **请求头**: `Authorization: Bearer {token}`

##### 5. 查询任务的评价
- **接口**: `GET /evaluation/task/{taskId}`
- **描述**: 查询某个任务的所有评价
- **响应结果**: 评价列表

##### 6. 获取用户平均评分
- **接口**: `GET /evaluation/average-rating/{userId}`
- **描述**: 获取用户的平均评分
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": 4.5    // 平均评分
}
```

#### 技术特性

1. **双向评价系统**: 支持发布者和接单人互相评价
2. **防重复评价**: 同一用户对同一订单的同一类型只能评价一次
3. **评分统计**: 支持计算用户平均评分
4. **逻辑删除**: 评价支持软删除，数据可恢复
5. **参数校验**: 使用 `@Valid` 注解进行请求参数校验（评分1-5星）

#### 开发难点与解决方案

##### 难点1：JWT Token 过期导致接口认证失败
**问题描述**: 
- 使用旧的 Token 访问需要认证的接口时，出现 `JWT signature does not match locally computed signature` 错误
- 根本原因：Token 已过期或失效，JWT 库在校验签名时抛出 `SignatureException`
- 现象：不需要 Token 的公开接口（GET /evaluation/{id}、GET /evaluation/task/{taskId}、GET /evaluation/average-rating/{userId}）均正常返回，但需要认证的接口全部返回 500

**解决方案**:
- 重新登录 user-service 获取新的有效 Token
- 使用新 Token 后所有 6 个接口全部测试通过
- 经验教训：部署前应检查 Token 有效期，JWT 验证失败优先排查 Token 是否过期
- 建议：增加 Token 过期时的友好提示（返回 401 而非 500），提升用户体验
```yaml
jwt:
  secret: campus-help-secret-key-for-jwt-token-2024
  expire: 604800000  # 7天，注意及时刷新
```

##### 难点2：common 模块工具类无法注入
**问题描述**:
- `JwtUtil` 等工具类在 common 模块中，但 evaluation-service 无法自动注入
- 原因：Spring Boot 默认只扫描当前模块的包，不扫描依赖模块的包

**解决方案**:
- 创建 `CommonConfig` 配置类，显式扫描 common 模块的包：
  ```java
  @Configuration
  @ComponentScan(basePackages = {"org.example.common"})
  public class CommonConfig {
  }
  ```
- 确保 common 模块中的类使用 `@Component` 或 `@Service` 等注解

##### 难点3：参数校验依赖缺失
**问题描述**:
- 使用 `@Valid` 注解进行参数校验时报错，校验不生效
- 原因：Spring Boot 3.x 中 `spring-boot-starter-validation` 不再包含在 web starter 中

**解决方案**:
- 在 pom.xml 中显式添加 validation 依赖：
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
  ```
- 在请求 DTO 中使用 Jakarta 校验注解：
  ```java
  @NotNull(message = "评分不能为空")
  @Min(value = 1, message = "评分最低为1星")
  @Max(value = 5, message = "评分最高为5星")
  private Integer rating;
  ```

##### 难点4：防止重复评价
**问题描述**:
- 同一用户可能对同一订单多次提交评价，导致数据重复
- 需要保证评价的唯一性

**解决方案**:
- 在创建评价前，使用复合条件查询是否已存在评价：
  ```java
  LambdaQueryWrapper<Evaluation> queryWrapper = new LambdaQueryWrapper<>();
  queryWrapper.eq(Evaluation::getTaskId, request.getTaskId())
             .eq(Evaluation::getOrderId, request.getOrderId())
             .eq(Evaluation::getEvaluatorId, evaluatorId)
             .eq(Evaluation::getType, request.getType())
             .eq(Evaluation::getDeleted, 0);
  
  Long count = evaluationMapper.selectCount(queryWrapper);
  if (count > 0) {
      throw new BusinessException(400, "您已经评价过该订单");
  }
  ```
- 使用 `taskId + orderId + evaluatorId + type` 作为唯一性判断条件

### admin-service

后台管理服务模块，提供仪表盘统计、用户管理、任务管理、订单管理等管理功能。

#### 接口列表

所有接口均需管理员权限，请求头需携带 `Authorization: Bearer {token}`。

##### 1. 获取仪表盘数据
- **接口**: `GET /admin/dashboard`
- **描述**: 获取系统全局统计数据，涵盖用户、任务、订单三大维度
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalUsers": 10,          // 用户总数
    "activeUsers": 9,          // 正常用户数
    "bannedUsers": 1,          // 封禁用户数
    "adminCount": 1,           // 管理员数量
    "totalTasks": 5,           // 任务总数
    "pendingTasks": 2,         // 待接单任务数
    "acceptedTasks": 1,        // 进行中任务数
    "completedTasks": 1,       // 已完成任务数
    "cancelledTasks": 1,       // 已取消任务数
    "totalOrders": 5,          // 订单总数
    "completedOrders": 1,      // 已完成订单数
    "pendingConfirmOrders": 1  // 待确认订单数
  }
}
```

##### 2. 分页查询用户列表
- **接口**: `GET /admin/users`
- **描述**: 分页查询所有用户，支持关键词搜索和状态筛选
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```
page=1              // 页码，默认1
size=10             // 每页条数，默认10
keyword=2024001     // 搜索关键词（学号或昵称），选填
status=2            // 用户状态筛选：1-正常 2-封禁，选填
```
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 7,
        "studentId": "2024001",
        "nickname": "ce",
        "avatarUrl": null,
        "points": 100,
        "creditScore": 95,
        "role": 1,
        "roleDesc": "管理员",
        "status": 1,
        "statusDesc": "正常",
        "createTime": "2026-04-19T16:03:44",
        "updateTime": "2026-04-19T16:03:44",
        "publishedTaskCount": null,
        "acceptedTaskCount": null,
        "completedOrderCount": null,
        "averageRating": null
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

##### 3. 获取用户详情
- **接口**: `GET /admin/users/{userId}`
- **描述**: 查看用户详细信息，含发布任务数、接单任务数、完成订单数等统计
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 7,
    "studentId": "2024001",
    "nickname": "ce",
    "avatarUrl": null,
    "points": 100,
    "creditScore": 95,
    "role": 1,
    "roleDesc": "管理员",
    "status": 1,
    "statusDesc": "正常",
    "createTime": "2026-04-19T16:03:44",
    "updateTime": "2026-04-19T16:03:44",
    "publishedTaskCount": 3,   // 发布的任务数量
    "acceptedTaskCount": 1,     // 接单的任务数量
    "completedOrderCount": 1,   // 完成的订单数量
    "averageRating": null
  }
}
```

##### 4. 封禁/解封用户
- **接口**: `PUT /admin/users/{userId}/status`
- **描述**: 管理员封禁或解封指定用户
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```
status=2    // 用户状态：1-正常(解封) 2-封禁
```
- **响应结果**:
```json
// 封禁成功
{
  "code": 200,
  "message": "success",
  "data": "用户已封禁"
}

// 解封成功
{
  "code": 200,
  "message": "success",
  "data": "用户已解封"
}

// 失败 - 用户不存在
{
  "code": 404,
  "message": "用户不存在",
  "data": null
}

// 失败 - 状态值不合法
{
  "code": 400,
  "message": "状态值不正确，1-正常 2-封禁",
  "data": null
}
```

##### 5. 分页查询任务列表
- **接口**: `GET /admin/tasks`
- **描述**: 分页查询所有任务，支持状态筛选和标题搜索
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```
page=1                // 页码，默认1
size=10               // 每页条数，默认10
status=PENDING        // 任务状态筛选：PENDING/ACCEPTED/COMPLETED/CANCELLED，选填
keyword=快递          // 搜索关键词（任务标题），选填
```
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 8,
        "publisherNickname": "用户A",
        "title": "帮我取快递",
        "description": "在菜鸟驿站",
        "type": "DELIVERY",
        "reward": 5.00,
        "pickupLocation": "菜鸟驿站",
        "deliveryLocation": "3号楼201",
        "status": "PENDING",
        "acceptorId": null,
        "acceptorNickname": null,
        "createTime": "2026-04-19T16:30:00",
        "updateTime": "2026-04-19T16:30:00",
        "deadline": "2026-04-20T18:00:00",
        "orderId": null,
        "orderNo": null
      }
    ],
    "total": 5,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

##### 6. 获取任务详情
- **接口**: `GET /admin/tasks/{taskId}`
- **描述**: 查看任务详细信息，含发布者昵称、接单人昵称、关联订单信息
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 8,
    "publisherNickname": "用户A",
    "title": "帮我取快递",
    "description": "在菜鸟驿站",
    "type": "DELIVERY",
    "reward": 5.00,
    "pickupLocation": "菜鸟驿站",
    "deliveryLocation": "3号楼201",
    "status": "ACCEPTED",
    "acceptorId": 7,
    "acceptorNickname": "ce",
    "createTime": "2026-04-19T16:30:00",
    "updateTime": "2026-04-19T16:35:00",
    "deadline": "2026-04-20T18:00:00",
    "orderId": 1,
    "orderNo": "ORD1745112345678ABCD"
  }
}
```

##### 7. 强制取消任务
- **接口**: `PUT /admin/tasks/{taskId}/cancel`
- **描述**: 管理员强制取消指定任务（任何状态均可，已完成除外）
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
// 成功
{
  "code": 200,
  "message": "success",
  "data": "任务已强制取消"
}

// 失败 - 任务已被取消
{
  "code": 400,
  "message": "任务已被取消",
  "data": null
}

// 失败 - 已完成的任务不能取消
{
  "code": 400,
  "message": "已完成的任务不能取消",
  "data": null
}

// 失败 - 任务不存在
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

##### 8. 分页查询订单列表
- **接口**: `GET /admin/orders`
- **描述**: 分页查询所有订单，支持状态筛选
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```
page=1     // 页码，默认1
size=10    // 每页条数，默认10
status=1   // 订单状态筛选：1-已接单 2-已完成(待确认) 3-已确认 4-已取消，选填
```
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderNo": "ORD1745112345678ABCD",
        "taskId": 1,
        "taskTitle": null,
        "takerId": 7,
        "takerNickname": "ce",
        "publisherId": 8,
        "publisherNickname": "用户A",
        "rewardPoints": 10,
        "status": 1,
        "statusDesc": "已接单",
        "completeProofUrl": null,
        "confirmTime": null,
        "createTime": "2026-04-20T10:30:00",
        "updateTime": "2026-04-20T10:30:00"
      }
    ],
    "total": 5,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

##### 9. 获取订单详情
- **接口**: `GET /admin/orders/{orderId}`
- **描述**: 查看订单详细信息，含接单人昵称、发布者昵称、任务标题
- **请求头**: `Authorization: Bearer {token}`
- **响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "orderNo": "ORD1745112345678ABCD",
    "taskId": 1,
    "taskTitle": "帮我取快递",
    "takerId": 7,
    "takerNickname": "ce",
    "publisherId": 8,
    "publisherNickname": "用户A",
    "rewardPoints": 10,
    "status": 2,
    "statusDesc": "已完成（待确认）",
    "completeProofUrl": "http://example.com/proof.jpg",
    "confirmTime": null,
    "createTime": "2026-04-20T10:30:00",
    "updateTime": "2026-04-20T10:35:00"
  }
}
```

#### 用户状态说明
| 状态 | 说明 |
|------|------|
| 1 | 正常 |
| 2 | 已封禁 |

#### 用户角色说明
| 角色 | 说明 |
|------|------|
| 0 | 普通用户 |
| 1 | 管理员 |

#### 技术特性

1. **跨数据库查询**: 主数据源连接 `user_service`，通过 `@TableName(schema = "xxx")` 注解实现跨库查询 `task_service.task` 和 `order_service.delivery_order`
2. **MyBatis-Plus 分页**: 使用 `PaginationInnerInterceptor` 插件实现统一分页，所有列表接口均返回 `Page<T>` 分页对象
3. **批量用户昵称查询**: `buildUserNicknameMap` 方法批量获取用户昵称，避免列表查询中的 N+1 问题
4. **管理员权限校验**: 所有接口均需从 JWT Token 中解析用户身份进行管理员校验
5. **防重复操作**: 封禁/解封和强制取消操作均包含状态合法性校验

#### 开发难点与解决方案

##### 难点1：跨数据库实体映射
**问题描述**:
- admin-service 主数据源连接 `user_service` 数据库，但任务数据在 `task_service` 数据库，订单数据在 `order_service` 数据库
- MyBatis-Plus 默认只查询主数据源的表

**解决方案**:
- 在实体类上使用 `@TableName` 注解的 `schema` 属性指定数据库名：
  ```java
  // 任务实体 — 映射 task_service.task
  @TableName(value = "task", schema = "task_service")
  public class AdminTask { ... }
  
  // 订单实体 — 映射 order_service.delivery_order
  @TableName(value = "delivery_order", schema = "order_service")
  public class AdminOrder { ... }
  ```
- 前提：所有数据库位于同一 MySQL 实例，且主数据源账号拥有跨库查询权限

##### 难点2：列表查询的 N+1 问题
**问题描述**:
- 任务列表和订单列表中每条记录都需要关联查询用户昵称
- 逐条查询会导致大量独立 SQL，性能低下

**解决方案**:
- 实现 `buildUserNicknameMap` 方法，先收集所有 userId，再批量查询：
  ```java
  private Map<Long, String> buildUserNicknameMap(List<Long> userIds) {
      if (userIds.isEmpty()) return Collections.emptyMap();
      wrapper.in(AdminUser::getId, userIds)
             .select(AdminUser::getId, AdminUser::getNickname);
      List<AdminUser> users = adminUserMapper.selectList(wrapper);
      return users.stream()
              .collect(Collectors.toMap(AdminUser::getId, 
                      u -> u.getNickname() != null ? u.getNickname() : "未知用户"));
  }
  ```
- 将 N 次查询优化为 1 次批量查询，列表接口性能显著提升

##### 难点3：JWT Token 校验与通用模块依赖
**问题描述**:
- admin-service 需要解析 JWT Token 获取用户身份
- `JwtUtil` 工具类在 common 模块中，Spring Boot 默认不扫描依赖模块

**解决方案**:
- 创建 `CommonConfig` 配置类，显式扫描 common 模块：
  ```java
  @Configuration
  @ComponentScan(basePackages = {"org.example.common"})
  public class CommonConfig { }
  ```
- 在 Controller 中注入 `JwtUtil` 进行 Token 解析和权限校验


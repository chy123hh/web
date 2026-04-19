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



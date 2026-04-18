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

#### 登录
##### security拦截
1. 设置放行接口登录和注册
2. 配置security拦截
3. 配置jwt拦截在密码用户拦截之前
4. 配置jwt拦截器
5. 在jwt拦截器中创建认证对象并设置认证信息
* ` .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);`
* `UsernamePasswordAuthenticationToken authentication =
  new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
  // 设置认证信息，完成认证后续过滤器就可以通过了
  SecurityContextHolder.getContext().setAuthentication(authentication);`
##### 接口描述
1. 前端通过输入学号和密码来登录
   POST user/login

```json
   {
  "studentId": "string",
  "password": "string"
}
```

```json

{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjcsImlhdCI6MTc3NjQ5OTUxMSwiZXhwIjoxNzc3MTA0MzExfQ.54MkAP1YNQpspMywajRjvclnDGZf80io9RP-nTWdwdg",
    "userId": 7,
    "studentId": "123",
    "nickname": "ce"
  }
}
```




# 校园帮帮送 - 开发要求文档 v1.0

## 一、项目概述

### 1.1 项目名称

**校园帮帮送**（Pre Goods Express）—— 校园跑腿互助平台

### 1.2 业务目标

为在校大学生提供一个发布/接单跑腿任务的平台，支持代取快递、代买餐饮、占座、借书等场景。用户通过积分或小额悬赏完成任务，形成互帮互助的校园生态。

### 1.3 核心业务流程

1. 用户 A 发布任务（设置悬赏积分、截止时间、任务地点）
2. 用户 B 浏览任务列表并接单
3. 用户 B 完成任务后上传凭证（如照片）
4. 用户 A 确认完成，系统转移悬赏积分给 B
5. 双方可互相评价，影响信用分

### 1.4 技术架构原则

- **单体起步**：使用 Spring Boot 多模块，后续可拆分为微服务
- **异步解耦**：跨模块操作通过 RabbitMQ 通信
- **配置外部化**：使用 Nacos 管理配置（单体阶段仅用作配置中心）
- **容器化部署**：提供 Docker Compose 一键启动开发环境

## 二、功能模块划分

| 模块名     | 功能列表                                                     | 对外接口前缀         | 依赖模块            |
| ---------- | ------------------------------------------------------------ | -------------------- | ------------------- |
| common     | 公共工具、异常、常量、JWT、Redis/MQ封装                      | -                    | 无                  |
| user       | 注册/登录、个人资料、积分余额、信用分                        | `/api/v1/user`       | common              |
| task       | 发布任务、编辑任务、取消任务、任务详情、任务列表（分页筛选） | `/api/v1/task`       | user, common        |
| order      | 接单、取消接单、上传完成凭证、确认完成、订单列表             | `/api/v1/order`      | user, task, common  |
| message    | 站内信通知（任务被接、任务完成、系统提醒）                   | `/api/v1/message`    | user, common        |
| search     | 任务搜索（关键词+地点+悬赏范围）                             | `/api/v1/search`     | task, common        |
| evaluation | 评价（完成订单后互评）、信用分计算                           | `/api/v1/evaluation` | user, order, common |
| admin      | 管理员后台（任务管理、用户封禁、数据统计）                   | `/api/v1/admin`      | user, task, order   |

**开发顺序建议**：  
common → user → task → order → message → search → evaluation → admin

## 三、数据库设计

### 3.1 数据库名

`campus_help`

### 3.2 表结构

#### 用户表 `user`

| 字段         | 类型         | 约束             | 说明                            |
| ------------ | ------------ | ---------------- | ------------------------------- |
| id           | bigint       | PK, auto         | 用户ID                          |
| student_id   | varchar(20)  | unique, not null | 学号（登录账号）                |
| password     | varchar(100) | not null         | BCrypt加密                      |
| nickname     | varchar(50)  |                  | 昵称                            |
| avatar_url   | varchar(255) |                  | 头像URL                         |
| points       | int          | default 0        | 积分余额                        |
| credit_score | int          | default 100      | 信用分（0-100，低于60限制接单） |
| role         | tinyint      | default 0        | 0普通用户 1管理员               |
| status       | tinyint      | default 1        | 1正常 2封禁                     |
| create_time  | datetime     |                  | 注册时间                        |
| update_time  | datetime     |                  | 更新时间                        |

#### 任务表 `task`

| 字段          | 类型          | 约束            | 说明                                    |
| ------------- | ------------- | --------------- | --------------------------------------- |
| id            | bigint        | PK, auto        | 任务ID                                  |
| publisher_id  | bigint        | not null, index | 发布者ID                                |
| title         | varchar(100)  | not null        | 任务标题                                |
| description   | text          |                 | 详细描述                                |
| reward_points | int           | not null        | 悬赏积分                                |
| location      | varchar(255)  |                 | 任务地点（如“3号教学楼”）               |
| latitude      | double        |                 | 纬度（用于附近搜索）                    |
| longitude     | double        |                 | 经度（用于附近搜索）                    |
| deadline      | datetime      |                 | 截止时间                                |
| status        | tinyint       | not null        | 1待接单 2已接单 3已完成 4已取消 5已过期 |
| category      | tinyint       |                 | 1快递 2餐饮 3占座 4借书 5其他           |
| image_urls    | varchar(1000) |                 | 凭证图片（逗号分隔）                    |
| create_time   | datetime      |                 | 发布时间                                |
| update_time   | datetime      |                 | 更新时间                                |

#### 跑腿订单表 `delivery_order`

| 字段               | 类型         | 约束            | 说明                                     |
| ------------------ | ------------ | --------------- | ---------------------------------------- |
| id                 | bigint       | PK, auto        | 订单ID                                   |
| order_no           | varchar(32)  | unique          | 订单号                                   |
| task_id            | bigint       | not null, index | 任务ID                                   |
| taker_id           | bigint       | not null, index | 接单者ID                                 |
| publisher_id       | bigint       | not null, index | 发布者ID                                 |
| reward_points      | int          | not null        | 接单时锁定的积分                         |
| status             | tinyint      | not null        | 1已接单 2已完成（待确认）3已确认 4已取消 |
| complete_proof_url | varchar(255) |                 | 完成凭证图片                             |
| confirm_time       | datetime     |                 | 发布者确认时间                           |
| create_time        | datetime     |                 | 接单时间                                 |
| update_time        | datetime     |                 | 更新时间                                 |

#### 消息表 `message`

| 字段         | 类型         | 约束            | 说明                          |
| ------------ | ------------ | --------------- | ----------------------------- |
| id           | bigint       | PK, auto        | 消息ID                        |
| from_user_id | bigint       |                 | 发送者（0为系统）             |
| to_user_id   | bigint       | not null, index | 接收者                        |
| type         | tinyint      |                 | 1系统通知 2任务状态 3评价提醒 |
| title        | varchar(100) |                 | 消息标题                      |
| content      | varchar(500) |                 | 消息内容                      |
| is_read      | tinyint      | default 0       | 0未读 1已读                   |
| create_time  | datetime     |                 | 发送时间                      |

#### 评价表 `evaluation`

| 字段         | 类型         | 约束             | 说明                           |
| ------------ | ------------ | ---------------- | ------------------------------ |
| id           | bigint       | PK, auto         | 评价ID                         |
| order_id     | bigint       | not null, unique | 订单ID（一个订单只能评价一次） |
| from_user_id | bigint       | not null         | 评价人                         |
| to_user_id   | bigint       | not null         | 被评价人                       |
| score        | tinyint      | not null         | 1-5分                          |
| content      | varchar(255) |                  | 评价内容                       |
| create_time  | datetime     |                  | 评价时间                       |

### 3.3 Elasticsearch 任务索引映射

```json
{
  "task_index": {
    "mappings": {
      "properties": {
        "id": {"type": "long"},
        "title": {"type": "text", "analyzer": "ik_max_word"},
        "description": {"type": "text", "analyzer": "ik_max_word"},
        "reward_points": {"type": "integer"},
        "location": {"type": "text", "analyzer": "ik_smart"},
        "category": {"type": "integer"},
        "status": {"type": "integer"},
        "latitude": {"type": "double"},
        "longitude": {"type": "geo_point"},
        "deadline": {"type": "date"},
        "create_time": {"type": "date"}
      }
    }
  }
}

## 四、技术集成详细要求

### 4.1 必须使用的技术及版本

| 技术                 | 版本       | 用途                           |
| -------------------- | ---------- | ------------------------------ |
| Spring Boot          | 3.2.x      | 基础框架                       |
| Spring Cloud Alibaba | 2022.0.0.0 | Nacos 配置中心（服务发现预留） |
| MySQL                | 8.0+       | 关系数据库                     |
| Redis                | 7.0+       | 缓存 + 分布式锁                |
| RabbitMQ             | 3.12+      | 异步消息                       |
| Elasticsearch        | 8.10+      | 搜索                           |
| Docker               | 24+        | 容器化                         |
| Nacos                | 2.2.3      | 配置中心                       |

### 4.2 Maven 多模块结构

```text
pre_goods(pom)
├── common
├── user-service
├── task-service
├── order-service
├── message-service
├── search-service
├── evaluation-service
├── admin-service
└── starter (聚合启动)
```

每个子模块都是独立的 Spring Boot 可运行 Jar（单体时统一由 starter 聚合扫描）。

4.3 关键配置约定
统一 API 前缀：/api/v1/模块名/...

统一返回格式：{ code: 200, msg: "success", data: ... }

统一异常码：100x（参数）、200x（业务）、300x（系统）

JWT 过期时间：7 天，存放在 Redis 中（key: token:userId）

Redis 键前缀：help:模块:... 例如 help:task:lock:${taskId}

### 4.4 RabbitMQ 事件定义

| 交换机              | 队列                     | 路由键             | 事件         | 触发场景                             |
| ------------------- | ------------------------ | ------------------ | ------------ | ------------------------------------ |
| task.exchange       | task.created.queue       | task.created       | 任务发布     | 发布任务后，通知 search 模块建立索引 |
| task.exchange       | task.updated.queue       | task.updated       | 任务更新     | 任务状态变更时更新 ES                |
| order.exchange      | order.taken.queue        | order.taken        | 任务被接单   | 发送站内信给发布者                   |
| order.exchange      | order.completed.queue    | order.completed    | 任务完成确认 | 转移积分、发送通知、更新 ES          |
| evaluation.exchange | evaluation.created.queue | evaluation.created | 评价完成     | 更新被评价者的信用分                 |
### 4.5 Redis 分布式锁使用场景

- **接单操作**：防止同一任务被多人同时接单
  - Key: `help:task:lock:${taskId}`，超时 5 秒

- **确认完成任务**：防止重复发放积分
  - Key: `help:order:confirm:${orderId}`，超时 5 秒

代码示例（必须使用 Lua 脚本或 Redisson，推荐 `StringRedisTemplate.opsForValue().setIfAbsent()` 配合超时）

### 4.6 Seata 预留（单体阶段不启用）

- 引入依赖但不启用：`seata.enabled=false`
- 在可能跨模块事务的方法上注释 `@GlobalTransactional`（后续启用），单体阶段用 `@Transactional` 替代

## 五、核心接口定义（REST API）

### 5.1 用户模块

| 方法 | 路径                    | 说明         | 请求体                            | 响应              |
| ---- | ----------------------- | ------------ | --------------------------------- | ----------------- |
| POST | `/api/v1/user/register` | 注册         | `{studentId, password, nickname}` | 用户ID            |
| POST | `/api/v1/user/login`    | 登录         | `{studentId, password}`           | `{token, userId}` |
| GET  | `/api/v1/user/profile`  | 获取个人信息 | Header: Authorization             | 用户详情          |
| PUT  | `/api/v1/user/profile`  | 修改资料     | `{nickname, avatarUrl}`           | 成功标志          |
| GET  | `/api/v1/user/points`   | 查询积分余额 | Header                            | 积分              |
| GET  | `/api/v1/user/credit`   | 查询信用分   | Header                            | 信用分            |

### 5.2 任务模块

| 方法   | 路径                        | 说明                     | 请求体                                                                                  | 响应     |
| ------ | --------------------------- | ------------------------ | --------------------------------------------------------------------------------------- | -------- |
| POST   | `/api/v1/task/publish`      | 发布任务                 | `{title, description, rewardPoints, location, latitude, longitude, deadline, category}` | 任务ID   |
| PUT    | `/api/v1/task/{taskId}`     | 编辑任务（仅待接单状态） | 同上                                                                                    | 成功标志 |
| DELETE | `/api/v1/task/{taskId}`     | 取消任务（仅待接单状态） | -                                                                                       | 成功标志 |
| GET    | `/api/v1/task/{taskId}`     | 任务详情                 | -                                                                                       | 任务对象 |
| GET    | `/api/v1/task/list`         | 任务列表（分页、筛选）   | Query: status, category, page, size                                                     | 分页数据 |
| GET    | `/api/v1/task/my-published` | 我发布的任务             | Header                                                                                  | 列表     |

### 5.3 订单模块（接单）

| 方法 | 路径                                     | 说明                       | 请求体            | 响应     |
| ---- | ---------------------------------------- | -------------------------- | ----------------- | -------- |
| POST | `/api/v1/order/take/{taskId}`            | 接单                       | -                 | 订单号   |
| PUT  | `/api/v1/order/cancel/{orderId}`         | 取消接单                   | -                 | 成功标志 |
| POST | `/api/v1/order/complete-proof/{orderId}` | 上传完成凭证               | `{proofImageUrl}` | 成功标志 |
| PUT  | `/api/v1/order/confirm/{orderId}`        | 发布者确认完成             | -                 | 积分变动 |
| GET  | `/api/v1/order/my-taken`                 | 我接的订单                 | Query: status     | 列表     |
| GET  | `/api/v1/order/my-published`             | 我发布的订单对应的接单记录 | -                 | 列表     |

### 5.4 搜索模块

| 方法 | 路径                   | 说明         | 请求参数                                                              | 响应                       |
| ---- | ---------------------- | ------------ | --------------------------------------------------------------------- | -------------------------- |
| GET  | `/api/v1/search/tasks` | 全文搜索任务 | keyword, category, minPoints, maxPoints, lat, lng, radius, page, size | 分页任务ID列表（附带高亮） |

### 5.5 消息模块

| 方法 | 路径                               | 说明             | 响应     |
| ---- | ---------------------------------- | ---------------- | -------- |
| GET  | `/api/v1/message/unread-count`     | 未读消息数       | 整数     |
| GET  | `/api/v1/message/list`             | 消息列表（分页） | 分页消息 |
| PUT  | `/api/v1/message/read/{messageId}` | 标记已读         | 成功标志 |

### 5.6 评价模块

| 方法 | 路径                               | 说明                 | 请求体                                | 响应     |
| ---- | ---------------------------------- | -------------------- | ------------------------------------- | -------- |
| POST | `/api/v1/evaluation/create`        | 对订单进行评价       | `{orderId, toUserId, score, content}` | 评价ID   |
| GET  | `/api/v1/evaluation/user/{userId}` | 查看某用户收到的评价 | Query: page, size                     | 评价列表 |
## 六、关键业务流程时序（开发必读）

### 6.1 接单流程（需分布式锁）

### 6.2 确认完成并转移积分（最终一致性）

**注意**：积分转移必须保证原子性，单体阶段使用 `@Transactional`；后续微服务阶段替换为 Seata。

## 七、开发环境与部署要求

### 7.1 Docker Compose 配置（根目录 docker-compose.yml）

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: campus_help
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  rabbitmq:
    image: rabbitmq:3.12-management-alpine
    ports:
      - "5672:5672"
      - "15672:15672"

  elasticsearch:
    image: elasticsearch:8.10.2
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
    volumes:
      - es_data:/usr/share/elasticsearch/data

  nacos:
    image: nacos/nacos-server:v2.2.3
    environment:
      - MODE=standalone
    ports:
      - "8848:8848"

volumes:
  mysql_data:
  es_data:
```

### 7.2 配置文件要求

使用 `application.yml` 作为公共配置

环境配置：application-dev.yml（本地）、application-prod.yml（生产）

Nacos 配置中心：Data ID 为 campus-help.yml，Group 为 DEFAULT_GROUP

单体阶段：`spring.cloud.nacos.discovery.enabled=false`（不注册服务）



## 八、代码规范与质量要求

### 8.1 包命名规范
- **每个模块内分层**：`controller`、`service`、`repository`、`dto`、`entity`、`config`

### 8.2 接口定义规范（为微服务准备）

每个模块的核心业务接口需定义在 common 模块中：

```java
// common 模块
public interface TaskServiceApi {
    TaskDto getTaskById(Long taskId);
    boolean updateTaskStatus(Long taskId, Integer status);
}

// task-service 模块实现
@Service
public class TaskServiceImpl implements TaskServiceApi { ... }

// 其他模块使用时通过 Spring 注入 TaskServiceApi（单体直接调用实现）
```

### 8.3 日志要求

- 使用 **SLF4J + Logback**
- 关键操作（发布、接单、确认完成）必须记录 `INFO` 日志
- 异常必须记录 `ERROR` 日志并包含堆栈

### 8.4 单元测试要求

- 每个 service 核心方法至少一个单元测试（使用 **JUnit 5 + Mockito**）
- 使用 `@SpringBootTest` 进行集成测试

## 九、论文撰写支撑材料（供后续使用）

作为产品经理，我要求开发过程中保留以下产出，用于您的毕业论文：

- **系统架构图**（分层架构 + 微服务演进图）
- **E-R 图**（所有表的关联关系）
- **业务流程图**（发布任务、接单、完成确认）
- **核心代码片段**（分布式锁、MQ 监听、ES 查询）
- **部署截图**（Docker 容器运行状态、接口测试 Swagger 页面）
- **性能测试数据**（并发接单下 Redis 锁效果）

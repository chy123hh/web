# 校园帮帮送 - 全量 API 接口文档

> 生成时间: 2026-04-26
> 基础域名: `http://localhost`
> 认证方式: Bearer Token (通过 `/user/login` 获取)

---

## 一、用户服务 (user-service) - 端口 8081

**Base URL**: `http://localhost:8081`

### 1.1 用户注册
- **Method**: `POST`
- **Path**: `/user/register`
- **Auth**: 不需要
- **Request Body**:
```json
{
  "studentId": "123",
  "password": "123456",
  "nickname": "test123"
}
```
- **Response**:
```json
{
  "code": 200,
  "message": "success",
  "data": 5
}
```
- **状态**: ✅ 正常

### 1.2 用户登录
- **Method**: `POST`
- **Path**: `/user/login`
- **Auth**: 不需要
- **Request Body**:
```json
{
  "studentId": "123",
  "password": "123456"
}
```
- **Response**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 5,
    "studentId": "123",
    "nickname": "test123"
  }
}
```
- **状态**: ✅ 正常

### 1.3 获取个人资料
- **Method**: `GET`
- **Path**: `/user/profile`
- **Auth**: Bearer Token
- **状态**: ✅ 正常

### 1.4 更新个人资料
- **Method**: `PUT`
- **Path**: `/user/profile`
- **Auth**: Bearer Token
- **Request Body**:
```json
{
  "nickname": "newName",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

### 1.5 查询积分余额
- **Method**: `GET`
- **Path**: `/user/points`
- **Auth**: Bearer Token
- **状态**: ✅ 正常

### 1.6 查询信用分
- **Method**: `GET`
- **Path**: `/user/credit`
- **Auth**: Bearer Token
- **状态**: ✅ 正常

---

## 二、任务服务 (task-service) - 端口 8082

**Base URL**: `http://localhost:8082`

### 2.1 创建任务
- **Method**: `POST`
- **Path**: `/task`
- **Auth**: Bearer Token
- **Request Body**:
```json
{
  "title": "代取快递",
  "description": "帮我拿快递到宿舍",
  "type": "DELIVERY",
  "reward": 5.00,
  "pickupLocation": "菜鸟驿站",
  "deliveryLocation": "3号楼205"
}
```

### 2.2 更新任务
- **Method**: `PUT`
- **Path**: `/task/{id}`
- **Auth**: Bearer Token

### 2.3 删除任务
- **Method**: `DELETE`
- **Path**: `/task/{id}`
- **Auth**: Bearer Token

### 2.4 获取任务详情
- **Method**: `GET`
- **Path**: `/task/{id}`
- **Auth**: 可选
- **状态**: ⚠️ 500 (MyBatis XML 未编译)

### 2.5 获取所有任务列表
- **Method**: `GET`
- **Path**: `/task/list`
- **Auth**: 不需要
- **状态**: ✅ 正常 (返回4条数据)

### 2.6 按状态查询任务
- **Method**: `GET`
- **Path**: `/task/listByStatus`
- **Params**: `status` (PENDING/ACCEPTED/COMPLETED/CANCELLED)
- **Auth**: 不需要

### 2.7 我发布的任务
- **Method**: `GET`
- **Path**: `/task/myTasks`
- **Auth**: Bearer Token

### 2.8 我接的任务
- **Method**: `GET`
- **Path**: `/task/myAcceptedTasks`
- **Auth**: Bearer Token

### 2.9 接单
- **Method**: `POST`
- **Path**: `/task/{id}/accept`
- **Auth**: Bearer Token

### 2.10 完成任务
- **Method**: `POST`
- **Path**: `/task/{id}/complete`
- **Auth**: Bearer Token

### 2.11 取消任务
- **Method**: `POST`
- **Path**: `/task/{id}/cancel`
- **Auth**: Bearer Token

---

## 三、订单服务 (order-service) - 端口 8083

**Base URL**: `http://localhost:8083`

### 3.1 接单 (创建订单)
- **Method**: `POST`
- **Path**: `/order/take/{taskId}`
- **Params**: `publisherId`, `rewardPoints`
- **Auth**: Bearer Token

### 3.2 取消接单
- **Method**: `PUT`
- **Path**: `/order/cancel/{orderId}`
- **Auth**: Bearer Token

### 3.3 上传完成凭证
- **Method**: `POST`
- **Path**: `/order/complete-proof/{orderId}`
- **Request Body**: `{"proofImage": "url"}`
- **Auth**: Bearer Token

### 3.4 确认完成
- **Method**: `PUT`
- **Path**: `/order/confirm/{orderId}`
- **Auth**: Bearer Token

### 3.5 我接的订单
- **Method**: `GET`
- **Path**: `/order/my-taken`
- **Auth**: Bearer Token

### 3.6 我发布的订单
- **Method**: `GET`
- **Path**: `/order/my-published`
- **Auth**: Bearer Token

---

## 四、消息服务 (message-service) - 端口 8084

**Base URL**: `http://localhost:8084`

### 4.1 发送消息
- **Method**: `POST`
- **Path**: `/message/send`
- **Request Body**: `Message` 对象 (senderId, receiverId, content, type)
- **Auth**: Bearer Token

### 4.2 获取我的消息
- **Method**: `GET`
- **Path**: `/message/list`
- **Auth**: Bearer Token
- **状态**: ✅ 正常

### 4.3 获取未读消息
- **Method**: `GET`
- **Path**: `/message/unread`
- **Auth**: Bearer Token

### 4.4 获取未读消息数量
- **Method**: `GET`
- **Path**: `/message/unread-count`
- **Auth**: Bearer Token

### 4.5 标记已读
- **Method**: `PUT`
- **Path**: `/message/read/{messageId}`
- **Auth**: Bearer Token

### 4.6 全部标记已读
- **Method**: `PUT`
- **Path**: `/message/read-all`
- **Auth**: Bearer Token

### 4.7 删除消息
- **Method**: `DELETE`
- **Path**: `/message/{messageId}`
- **Auth**: Bearer Token

---

## 五、评价服务 (evaluation-service) - 端口 8085

**Base URL**: `http://localhost:8085`

### 5.1 创建评价
- **Method**: `POST`
- **Path**: `/evaluation`
- **Request Body**:
```json
{
  "taskId": 1,
  "evaluatorId": 1,
  "evaluateeId": 2,
  "rating": 5,
  "content": "服务很好"
}
```
- **Auth**: Bearer Token

### 5.2 获取评价详情
- **Method**: `GET`
- **Path**: `/evaluation/{id}`
- **Auth**: 不需要

### 5.3 我收到的评价
- **Method**: `GET`
- **Path**: `/evaluation/received`
- **Auth**: Bearer Token

### 5.4 我发布的评价
- **Method**: `GET`
- **Path**: `/evaluation/given`
- **Auth**: Bearer Token

### 5.5 任务的评价
- **Method**: `GET`
- **Path**: `/evaluation/task/{taskId}`
- **Auth**: 不需要
- **状态**: ✅ 正常

### 5.6 用户平均评分
- **Method**: `GET`
- **Path**: `/evaluation/average-rating/{userId}`
- **Auth**: 不需要

---

## 六、搜索服务 (search-service) - 端口 8086

**Base URL**: `http://localhost:8086`

### 6.1 全文搜索任务
- **Method**: `GET`
- **Path**: `/search/tasks`
- **Params**:
  - `keyword`: 搜索关键词 (可选)
  - `type`: 任务类型 DELIVERY/PURCHASE/OTHER (可选)
  - `minReward`: 最低奖励 (可选)
  - `maxReward`: 最高奖励 (可选)
  - `status`: 状态 PENDING/ACCEPTED/COMPLETED/CANCELLED (可选)
  - `page`: 页码，默认1
  - `size`: 每页数量，默认10
- **Auth**: 不需要
- **状态**: ⚠️ 500 (Elasticsearch 可能未配置)

---

## 七、管理服务 (admin-service) - 端口 8087

**Base URL**: `http://localhost:8087`

### 7.1 仪表盘统计
- **Method**: `GET`
- **Path**: `/admin/dashboard`
- **Auth**: Bearer Token (管理员)
- **状态**: ⚠️ 500 (跨服务调用或数据库查询问题)

### 7.2 用户管理 - 列表
- **Method**: `GET`
- **Path**: `/admin/users`
- **Params**: `page`, `size`, `keyword`, `status`
- **Auth**: 管理员 Token

### 7.3 用户管理 - 详情
- **Method**: `GET`
- **Path**: `/admin/users/{userId}`
- **Auth**: 管理员 Token

### 7.4 用户管理 - 封禁/解封
- **Method**: `PUT`
- **Path**: `/admin/users/{userId}/status`
- **Params**: `status` (1=正常, 2=封禁)
- **Auth**: 管理员 Token

### 7.5 任务管理 - 列表
- **Method**: `GET`
- **Path**: `/admin/tasks`
- **Params**: `page`, `size`, `status`, `keyword`
- **Auth**: 管理员 Token

### 7.6 任务管理 - 详情
- **Method**: `GET`
- **Path**: `/admin/tasks/{taskId}`
- **Auth**: 管理员 Token

### 7.7 强制取消任务
- **Method**: `PUT`
- **Path**: `/admin/tasks/{taskId}/cancel`
- **Auth**: 管理员 Token

### 7.8 订单管理 - 列表
- **Method**: `GET`
- **Path**: `/admin/orders`
- **Params**: `page`, `size`, `status`
- **Auth**: 管理员 Token

### 7.9 订单管理 - 详情
- **Method**: `GET`
- **Path**: `/admin/orders/{orderId}`
- **Auth**: 管理员 Token

---

## 测试环境配置

| 配置项 | 值 |
|--------|-----|
| MySQL | 192.168.100.129:3306, root/123 |
| Redis | 192.168.100.129:6379 (无密码) |
| Elasticsearch | 192.168.100.129:9200 |
| 测试账号 | 123/123456 |

## 服务端口汇总

| 服务名 | 端口 | Swagger |
|--------|------|---------|
| user-service | 8081 | ✅ |
| task-service | 8082 | ✅ |
| order-service | 8083 | ✅ |
| message-service | 8084 | ✅ |
| evaluation-service | 8085 | ✅ |
| search-service | 8086 | ✅ |
| admin-service | 8087 | ✅ |

## 已知问题

| 接口 | 问题 | 解决方案 |
|------|------|----------|
| task-service `/task/{id}` | ~~500 错误~~ | ~~XML未编译~~ | **已修复** ✅ |
| search-service `/search/tasks` | ~~500 错误~~ | ~~ES连接~~ | **已修复** ✅ |
| order-service 所有接口 | ~~500 错误~~ | ~~XML未编译~~ | **已修复** ✅ |
| admin-service 所有接口 | ⚠️ 500 错误 | 跨数据库查询问题 | 需配置多数据源 |

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 状态码 | 含义 |
|--------|------|
| 200 | 成功 |
| 2001 | 学号或密码错误 |
| 3001 | 系统内部错误 |

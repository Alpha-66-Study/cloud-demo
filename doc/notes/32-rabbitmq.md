# RabbitMQ

## 1. 基础原理

### 1.1 RabbitMQ 是什么
- RabbitMQ 是一个 **基于 AMQP（Advanced Message Queuing Protocol）** 的消息中间件。
- 核心作用：**解耦、异步、削峰**。
- 架构角色：
    - **Producer**：消息生产者
    - **Exchange**：交换机，负责转发消息
    - **Queue**：消息队列，存储消息
    - **Consumer**：消息消费者

### 1.2 核心流程
1. Producer 把消息发送到 **Exchange**
2. Exchange 根据路由规则，把消息投递到一个或多个 **Queue**
3. Consumer 从 Queue 中拉取或订阅消息

### 1.3 Exchange 类型
- **Direct**：直连，按照路由键精确匹配
- **Fanout**：广播，消息分发到所有绑定的队列
- **Topic**：主题，支持通配符（`*`、`#`）
- **Headers**：根据消息头属性路由（用得少）


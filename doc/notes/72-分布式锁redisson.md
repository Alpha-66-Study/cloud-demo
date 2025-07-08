# Redisson 分布式锁使用与原理文档

## 一、Redisson 分布式锁简介

Redisson 是一个功能强大的 Redis Java 客户端，实现了丰富的分布式结构，包括分布式锁、布隆过滤器、队列、信号量等。

在分布式环境下，多个应用实例需要协调执行某些任务时，分布式锁成为必要机制。Redisson 基于 Redis 的原子性操作，提供了简单易用的锁 API。

---

## 二、使用场景举例

- 分布式定时任务，只允许一个节点执行
- 抢购、秒杀等高并发控制
- 分布式状态流转或同步过程控制
- 并发控制资源访问（如限流、库存、账户余额操作等）

---

## 三、Redisson 分布式锁核心原理

### 1. Redis SET NX EX 实现锁

Redisson 本质上封装了 Redis 的如下命令：

```bash
SET lock_key unique_value NX EX expire_time
```
- NX：仅当 Key 不存在时设置（保证原子性）
- EX：设置过期时间，避免死锁
- unique_value 用于确保只有当前客户端能解锁

### 2. RedissonClient + RLock 实现可重入锁

Redisson 提供了 RLock 接口，支持：
- 可重入锁
- 自动续期机制（默认 watchdog 模式）
- 公平锁、读写锁、信号量等

package com.demo.product.task;

import com.demo.product.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public abstract class DistributedTask {

  private final RedisLockService redisLockService;

  public abstract String taskName();

  public abstract Duration lockDuration(); // 任务最大执行时间

  public abstract void doExecute();

  public void run() {
    String lockKey = "lock:task:" + taskName();
    boolean locked = redisLockService.tryLock(lockKey, lockDuration().getSeconds());
    if (!locked) {
      log.info("任务已锁定：{}", taskName());
      return;
    }
    try {
      doExecute();
    } catch (Exception e) {
      log.error("任务执行异常：{}", e.getMessage());
    } finally {
      redisLockService.unlock(lockKey);
    }
  }

}

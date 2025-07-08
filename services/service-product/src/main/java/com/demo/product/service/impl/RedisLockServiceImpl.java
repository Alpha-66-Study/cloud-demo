package com.demo.product.service.impl;

import com.demo.product.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisLockServiceImpl implements RedisLockService {

  private final RedissonClient redissonClient;

  @Override
  public boolean tryLock(String lockKey, long leaseSeconds) {
    RLock lock = redissonClient.getLock(lockKey);
    try {
      return lock.tryLock(0, leaseSeconds, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  @Override
  public void unlock(String lockKey) {
    RLock lock = redissonClient.getLock(lockKey);
    if (lock.isHeldByCurrentThread()) {
      lock.unlock();
    }
  }

}

package com.demo.product.service;

public interface RedisLockService {

  boolean tryLock(String lockKey, long leaseSeconds);

  void unlock(String lockKey);

}

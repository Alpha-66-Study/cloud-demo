package com.demo.product.task;

import com.demo.product.service.RedisLockService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ProductTask extends DistributedTask {

  public ProductTask(RedisLockService redisLockService) {
    super(redisLockService);
  }

  @Override
  public String taskName() {
    return "更新产品库存任务";
  }

  @Override
  public Duration lockDuration() {
    return Duration.ofSeconds(55);
  }

  @Override
  public void doExecute() {
    System.out.println("正在更新产品库存...");
  }

  @Scheduled(cron = "0 * * * * ?")
  public void schedule() {
    this.run();
  }

}

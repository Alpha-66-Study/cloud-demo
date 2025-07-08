package com.demo.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

  @Value("${spring.data.redis.host}")
  private String host;
  @Value("${spring.data.redis.port}")
  private int port;

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    String redisUrl = "redis://" + host + ":" + port;
    config.useSingleServer()
        .setAddress(redisUrl)
        .setDatabase(0);
    return Redisson.create(config);
  }

}

package com.demo.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 开启定时任务功能
@EnableDiscoveryClient //开启服务发现功能
@SpringBootApplication
public class ProductMainApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductMainApplication.class, args);
  }
}

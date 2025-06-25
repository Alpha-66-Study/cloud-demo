package com.demo.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.demo.order.bean.Order;
import com.demo.order.properties.OrderProperties;
import com.demo.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final OrderProperties orderProperties;

  @GetMapping("/config")
  public String config() {
    return "order.timeout=" + orderProperties.getTimeout() + "； " +
        "order.auto-confirm=" + orderProperties.getAutoConfirm() + "；" +
        "order.db-url=" + orderProperties.getDbUrl();
  }

  @GetMapping("/create")
  public Order createOrder(@RequestParam("userId") Long userId,
                           @RequestParam("productId") Long productId) {
    return orderService.createOrder(productId, userId);
  }

  @GetMapping("/spike")
  @SentinelResource(value = "spike-order", fallback = "spikeFallback")
  public Order spike(@RequestParam(value = "userId", required = false) Long userId,
                     @RequestParam(value = "productId", defaultValue = "1000") Long productId) {
    Order order = orderService.createOrder(productId, userId);
    order.setId(Long.MAX_VALUE);
    return order;
  }

  public Order spikeFallback(Long userId, Long productId, Throwable exception) {
    System.out.println("spikeFallback....");
    Order order = new Order();
    order.setId(productId);
    order.setUserId(userId);
    order.setAddress("异常信息：" + exception.getClass());
    return order;
  }

  @GetMapping("/writeDb")
  public String writeDb() {
    log.info("writeDb...");
    return "writeDb success....";
  }

  @GetMapping("/readDb")
  public String readDb() {
    log.info("readDb...");
    return "readDb success....";
  }

}

package com.demo.business.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "seata-order")
public interface OrderFeignClient {

  /**
   * 创建订单
   */
  @GetMapping("/create")
  String create(@RequestParam("userId") String userId,
                @RequestParam("commodityCode") String commodityCode,
                @RequestParam("count") int orderCount);

}

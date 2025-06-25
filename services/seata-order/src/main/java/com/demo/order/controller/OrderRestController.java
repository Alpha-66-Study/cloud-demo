package com.demo.order.controller;

import com.demo.order.bean.OrderTbl;
import com.demo.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderRestController {

  private final OrderService orderService;

  /**
   * 创建订单
   */
  @GetMapping("/create")
  public String create(@RequestParam("userId") String userId,
                       @RequestParam("commodityCode") String commodityCode,
                       @RequestParam("count") int orderCount) {
    OrderTbl tbl = orderService.create(userId, commodityCode, orderCount);
    return "order create success = 订单id：【" + tbl.getId() + "】";
  }

}

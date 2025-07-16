package com.demo.business.controller;

import com.demo.business.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PurchaseRestController {

  private final BusinessService businessService;

  /**
   * 购买
   */
  @GetMapping("/purchase")
  public String purchase(@RequestParam("userId") String userId,
                         @RequestParam("commodityCode") String commodityCode,
                         @RequestParam("count") int orderCount) {
    businessService.purchase(userId, commodityCode, orderCount);
    return "business purchase success";
  }
}

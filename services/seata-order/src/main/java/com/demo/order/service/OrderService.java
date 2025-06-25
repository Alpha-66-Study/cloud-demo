package com.demo.order.service;

import com.demo.order.bean.OrderTbl;

public interface OrderService {
  /**
   * 创建订单
   */
  OrderTbl create(String userId, String commodityCode, int orderCount);
}

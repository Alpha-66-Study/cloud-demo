package com.demo.order.service;

import com.demo.order.bean.Order;

public interface OrderService {

  Order createOrder(Long productId, Long userId);
}

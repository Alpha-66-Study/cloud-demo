package com.demo.business.service.impl;

import com.demo.business.feign.OrderFeignClient;
import com.demo.business.feign.StorageFeignClient;
import com.demo.business.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

  private final StorageFeignClient storageFeignClient;
  private final OrderFeignClient orderFeignClient;

  @GlobalTransactional
  @Override
  public void purchase(String userId, String commodityCode, int orderCount) {
    //1. 扣减库存
    storageFeignClient.deduct(commodityCode, orderCount);

    //2. 创建订单
    orderFeignClient.create(userId, commodityCode, orderCount);
  }

}

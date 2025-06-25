package com.demo.business.service;

public interface BusinessService {

  /**
   * 采购
   */
  void purchase(String userId, String commodityCode, int orderCount);
}

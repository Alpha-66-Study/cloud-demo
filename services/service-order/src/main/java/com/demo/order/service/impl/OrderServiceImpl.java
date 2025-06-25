package com.demo.order.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.demo.order.bean.Order;
import com.demo.order.feign.ProductFeignClient;
import com.demo.order.service.OrderService;
import com.demo.product.bean.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final DiscoveryClient discoveryClient;
  private final RestTemplate restTemplate;
  private final LoadBalancerClient loadBalancerClient;
  private final ProductFeignClient productFeignClient;

  @SentinelResource(value = "createOrder", blockHandler = "createOrderFallback")
  @Override
  public Order createOrder(Long productId, Long userId) {
    Product product = productFeignClient.getProductById(productId);
    Order order = new Order();
    order.setId(1L);
    order.setTotalAmount(product.getPrice().multiply(new BigDecimal(product.getNum())));
    order.setUserId(userId);
    order.setNickName("张三");
    order.setAddress("北京");
    order.setProductList(List.of(product));
    return order;
  }

  public Order createOrderFallback(Long productId, Long userId, BlockException e) {
    Order order = new Order();
    order.setId(0L);
    order.setTotalAmount(new BigDecimal("0"));
    order.setUserId(userId);
    order.setNickName("未知用户");
    order.setAddress("异常信息：" + e.getClass());
    return order;
  }

  private Product getProductFromRemoteWithLoadBalanceAnnotation(Long productId) {
    String url = "http://service-product/product/" + productId;
    return restTemplate.getForObject(url, Product.class);
  }

  private Product getProductFromRemoteWithLoadBalance(Long productId) {
    ServiceInstance choose = loadBalancerClient.choose("service-product");
    String url = "http://" + choose.getHost() + ":" + choose.getPort() + "/product/" + productId;
    return restTemplate.getForObject(url, Product.class);
  }

  private Product getProductFromRemote(Long productId) {
    List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
    ServiceInstance instance = instances.get(0);
    String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/product/" + productId;
    log.info("远程请求：{}", url);
    return restTemplate.getForObject(url, Product.class);
  }

}

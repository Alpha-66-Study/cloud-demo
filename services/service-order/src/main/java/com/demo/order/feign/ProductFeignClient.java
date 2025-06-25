package com.demo.order.feign;

import com.demo.order.feign.fallback.ProductFeignClientFallback;
import com.demo.product.bean.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-product", fallback = ProductFeignClientFallback.class) // feign客户端
public interface ProductFeignClient {

  @GetMapping("/product/{id}")
  Product getProductById(@PathVariable("id") Long id);

}

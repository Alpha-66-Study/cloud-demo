package com.demo.product.controller;

import com.demo.product.bean.Product;
import com.demo.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping("/product/{id}")
  public Product getProduct(@PathVariable("id") Long productId,
                            HttpServletRequest request) {
    String header = request.getHeader("X-Token");
    System.out.println("hello .... token=【" + header + "】");
    Product product = productService.getProductById(productId);
//        int i = 10/0;
//        try {
//            TimeUnit.SECONDS.sleep(2);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    return product;
  }
}

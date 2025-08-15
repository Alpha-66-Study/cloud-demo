package com.demo.bitcoinj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BitcoinjApplication {

  public static void main(String[] args) {
    SpringApplication.run(BitcoinjApplication.class, args);
  }

}

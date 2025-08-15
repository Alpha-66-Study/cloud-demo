package com.demo.bitcoinj;

import com.demo.bitcoinj.service.BitcoinRpcService;
import org.bitcoinj.core.Block;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BitcoinjApplicationTests {

  @Autowired
  private BitcoinRpcService bitcoinRpcService;

  @Test
  void getBlockByHash() {
    Block block = bitcoinRpcService.getBlockByHash("000000000000000000002ea3e4972aa473be0580d696fe28e69d5b4be76209e9");
    System.out.println(block.getHash());
  }

}

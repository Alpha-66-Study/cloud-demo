package com.demo.bitcoinj.controller;

import com.demo.bitcoinj.model.LargeTransfer;
import com.demo.bitcoinj.service.BitcoinMonitorService;
import com.demo.bitcoinj.service.BitcoinRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController("/api/bitcoin")
@RequiredArgsConstructor
public class BitcoinMonitorController {

  private final BitcoinRpcService bitcoinRpcService;
  private final BitcoinMonitorService monitorService;

  @GetMapping("/latest-height")
  public ResponseEntity<Map<String, Object>> getLatestBlockHeight() {
    long localLatestBlockHeight = bitcoinRpcService.getLocalLatestBlockHeight();
    long height = bitcoinRpcService.getLatestBlockHeight();
    Map<String, Object> response = Map.of(
        "localLatestBlockHeight", localLatestBlockHeight,
        "latestBlockHeight", height
    );
    return ResponseEntity.ok(response);
  }

  @GetMapping("/large-transfers")
  public ResponseEntity<List<LargeTransfer>> getAllLargeTransfers() {
    List<LargeTransfer> transfers = monitorService.getAllLargeTransfers();
    return ResponseEntity.ok(transfers);
  }

  @GetMapping("/large-transfers/{txHash}")
  public ResponseEntity<LargeTransfer> getLargeTransferByTxHash(@PathVariable String txHash) {
    LargeTransfer transfer = monitorService.getLargeTransferByTxHash(txHash);
    if (transfer != null) {
      return ResponseEntity.ok(transfer);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/scan/block/{blockHash}")
  public ResponseEntity<String> scanBlock(@PathVariable String blockHash) {
    try {
      monitorService.scanBlockByHash(blockHash);
      return ResponseEntity.ok("区块 " + blockHash + " 扫描完成");
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("扫描失败: " + e.getMessage());
    }
  }

  @GetMapping("/scan/height/{height}")
  public ResponseEntity<String> scanBlockByHeight(@PathVariable int height) {
    try {
      monitorService.scanBlockByHeight(height);
      return ResponseEntity.ok("区块高度 " + height + " 扫描完成");
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("扫描失败: " + e.getMessage());
    }
  }

}

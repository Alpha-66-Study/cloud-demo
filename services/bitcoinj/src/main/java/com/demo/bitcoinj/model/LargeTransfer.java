package com.demo.bitcoinj.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LargeTransfer {

  private String txHash;
  private String blockHash;
  private int blockHeight;
  private LocalDateTime timestamp;
  private BigDecimal totalInput;
  private BigDecimal totalOutput;
  private String fromAddress;
  private String toAddress;
  private BigDecimal transferAmount;

}

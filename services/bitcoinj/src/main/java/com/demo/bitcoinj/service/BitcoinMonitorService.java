package com.demo.bitcoinj.service;

import com.demo.bitcoinj.model.LargeTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.base.*;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.crypto.ECKey;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptPattern;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class BitcoinMonitorService {

  private final BitcoinRpcService bitcoinRpcService;

  private static final BigDecimal LARGE_TRANSFER_THRESHOLD = BigDecimal.ONE;
  private final Map<String, LargeTransfer> largeTransfers = new ConcurrentHashMap<>();
  private final Set<String> scannedBlocks = ConcurrentHashMap.newKeySet();

  public List<LargeTransfer> getAllLargeTransfers() {
    return new ArrayList<>(largeTransfers.values());
  }

  public LargeTransfer getLargeTransferByTxHash(String txHash) {
    return largeTransfers.get(txHash);
  }

  @Scheduled(initialDelay = 60000, fixedDelay = 60000)
  public void scanLatestBlocks() {
    try {
      int height = bitcoinRpcService.getLocalLatestBlockHeight();
      String hash = bitcoinRpcService.getLocalLatestBlockHash();
      scanBlockInternal(height, hash);
    } catch (Exception e) {
      log.error("扫描最新区块时发生错误", e);
    }
  }

  public void scanBlockByHash(String blockHash) {
    try {
      int height = bitcoinRpcService.getLocalBlockHeightByHash(blockHash);
      scanBlockInternal(height, blockHash);
    } catch (Exception e) {
      log.error("扫描区块 {} 时发生错误", blockHash, e);
    }
  }

  public void scanBlockByHeight(int height) {
    try {
      String hash = bitcoinRpcService.getLocalBlockHash(height);
      scanBlockInternal(height, hash);
    } catch (Exception e) {
      log.error("扫描区块高度 {} 时发生错误", height, e);
    }
  }

  private void scanBlockInternal(int height, String blockHash) {
    if (height == -1 || blockHash == null) {
      return;
    }
    if (scannedBlocks.contains(blockHash)) {
      return;
    }
    scanBlock(height, blockHash);
    cleanOldScannedBlocks();
  }

  private void cleanOldScannedBlocks() {
    if (scannedBlocks.size() > 1000) {
      List<String> blockList = new ArrayList<>(scannedBlocks);
      scannedBlocks.clear();
      scannedBlocks.addAll(blockList.subList(Math.max(0, blockList.size() - 100), blockList.size()));
      log.info("已清理扫描记录，保留最近100个区块");
    }
  }

  private void scanBlock(int blockHeight, String blockHash) {
    try {
      log.info("扫描区块: {} 哈希: {}", blockHeight, blockHash);
      Block block = bitcoinRpcService.getBlockByHash(blockHash);
      if (block == null) {
        return;
      }
      List<Transaction> transactions = block.getTransactions();
      if (transactions == null || transactions.isEmpty()) {
        return;
      }
      // 构建当前区块的交易映射（用于 UTXO 查找）
      Map<Sha256Hash, Transaction> txMap = new HashMap<>();
      for (Transaction tx : transactions) {
        txMap.put(tx.getTxId(), tx);
      }
      for (Transaction tx : transactions) {
        analyzeTransaction(tx, block, blockHeight, blockHash, txMap);
      }
      scannedBlocks.add(blockHash);
      log.info("扫描区块完成: {} 哈希: {}", blockHeight, blockHash);
    } catch (Exception e) {
      log.error("扫描区块 {} 失败", blockHash, e);
    }
  }

  private void analyzeTransaction(Transaction tx, Block block, int blockHeight, String blockHash, Map<Sha256Hash, Transaction> utxoMap) {
    try {
      // 跳过coinbase交易
      if (tx.isCoinBase()) {
        return;
      }

      Map<String, Long> outputs = parseOutputs(tx);
      long totalOutput = outputs.values().stream().mapToLong(Long::longValue).sum();
      if (outputs.isEmpty() || totalOutput < LARGE_TRANSFER_THRESHOLD.multiply(BigDecimal.valueOf(100_000_000L)).longValue()) {
        return;
      }
      Map<String, Long> inputs = parseInputs(tx, utxoMap);
      if (inputs.isEmpty()) {
        return;
      }

      // 尝试多种策略找到转账
      List<TransferInfo> candidates = findAllTransferCandidates(inputs, outputs);

      // 检查每个候选是否为大额转账
      boolean foundLargeTransfer = false;
      for (TransferInfo transfer : candidates) {
        if (isLargeTransfer(transfer.amount)) {
          saveLargeTransfer(tx, block, blockHeight, blockHash, transfer, inputs, outputs);
          foundLargeTransfer = true;
        }
      }

      // 特殊情况：总输出很大但没找到合适配对时的兜底策略
      if (!foundLargeTransfer) {
        handleMissedLargeTransfer(tx, block, blockHeight, blockHash, inputs, outputs, totalOutput);
      }

    } catch (Exception e) {
      log.error("分析交易 {} 出错", tx.getTxId(), e);
    }
  }

  /**
   * 处理可能遗漏的大额转账
   */
  private void handleMissedLargeTransfer(Transaction tx, Block block, int blockHeight, String blockHash,
                                         Map<String, Long> inputs, Map<String, Long> outputs, long totalOutput) {
    // 找到最大的输出作为兜底
    String maxOutputAddress = outputs.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);

    String maxInputAddress = inputs.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);

    if (maxOutputAddress != null && maxInputAddress != null &&
        !maxOutputAddress.equals(maxInputAddress) &&
        isLargeTransfer(outputs.get(maxOutputAddress))) {

      TransferInfo fallbackTransfer = new TransferInfo(
          maxInputAddress,
          maxOutputAddress,
          outputs.get(maxOutputAddress),
          "FALLBACK"
      );
      saveLargeTransfer(tx, block, blockHeight, blockHash, fallbackTransfer, inputs, outputs);
    }
  }

  /**
   * 找到所有可能的转账候选
   */
  private List<TransferInfo> findAllTransferCandidates(Map<String, Long> inputs, Map<String, Long> outputs) {
    List<TransferInfo> candidates = new ArrayList<>();

    // 策略1: 移除找零后的最大配对
    Map<String, Long> filteredOutputs = removeChangeOutputs(inputs, outputs);
    candidates.addAll(findTransfersByMaxAmount(inputs, filteredOutputs, "FILTERED"));

    // 策略2: 原始输出的最大配对（防止找零算法过于激进）
    candidates.addAll(findTransfersByMaxAmount(inputs, outputs, "ORIGINAL"));

    // 策略3: 聚合交易（多输入到单输出）
    candidates.addAll(findAggregationTransfers(inputs, outputs));

    // 策略4: 分发交易（单输入到多输出）
    candidates.addAll(findDistributionTransfers(inputs, outputs));

    return deduplicateCandidates(candidates);
  }

  /**
   * 按最大金额配对
   */
  private List<TransferInfo> findTransfersByMaxAmount(Map<String, Long> inputs, Map<String, Long> outputs, String strategy) {
    List<TransferInfo> candidates = new ArrayList<>();

    if (inputs.isEmpty() || outputs.isEmpty()) {
      return candidates;
    }

    String maxInputAddress = inputs.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);

    String maxOutputAddress = outputs.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);

    if (maxInputAddress != null && maxOutputAddress != null &&
        !maxInputAddress.equals(maxOutputAddress)) {
      long amount = outputs.get(maxOutputAddress);
      candidates.add(new TransferInfo(maxInputAddress, maxOutputAddress, amount, strategy));
    }

    return candidates;
  }

  /**
   * 找聚合转账（多个输入到一个输出）
   */
  private List<TransferInfo> findAggregationTransfers(Map<String, Long> inputs, Map<String, Long> outputs) {
    List<TransferInfo> candidates = new ArrayList<>();

    String outputAddress = outputs.keySet().iterator().next();
    long outputAmount = outputs.values().iterator().next();

    // 使用贡献最大的输入地址作为代表
    String mainInputAddress = inputs.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);

    if (mainInputAddress != null && !mainInputAddress.equals(outputAddress)) {
      candidates.add(new TransferInfo(mainInputAddress, outputAddress, outputAmount, "AGGREGATION"));
    }

    return candidates;
  }

  /**
   * 找分发转账（一个输入到多个输出）
   */
  private List<TransferInfo> findDistributionTransfers(Map<String, Long> inputs, Map<String, Long> outputs) {
    List<TransferInfo> candidates = new ArrayList<>();

    if (inputs.size() == 1) {
      String inputAddress = inputs.keySet().iterator().next();

      // 为每个大额输出创建候选
      for (Map.Entry<String, Long> output : outputs.entrySet()) {
        if (!output.getKey().equals(inputAddress) && isLargeTransfer(output.getValue())) {
          candidates.add(new TransferInfo(inputAddress, output.getKey(), output.getValue(), "DISTRIBUTION"));
        }
      }
    }

    return candidates;
  }

  /**
   * 去重候选
   */
  private List<TransferInfo> deduplicateCandidates(List<TransferInfo> candidates) {
    Map<String, TransferInfo> unique = new LinkedHashMap<>();

    for (TransferInfo candidate : candidates) {
      String key = candidate.fromAddress + "->" + candidate.toAddress;
      TransferInfo existing = unique.get(key);

      if (existing == null || candidate.amount > existing.amount) {
        unique.put(key, candidate);
      }
    }

    return new ArrayList<>(unique.values());
  }

  /**
   * 改进的找零识别 - 更保守和准确
   */
  private Map<String, Long> removeChangeOutputs(Map<String, Long> inputs, Map<String, Long> outputs) {
    Map<String, Long> filtered = new HashMap<>(outputs);
    filtered.remove(null); // 移除null地址（OP_RETURN等）

    // 移除粉尘输出
    filtered.entrySet().removeIf(entry -> entry.getValue() <= 546);

    // 只在明确的2输出场景下移除找零
    if (outputs.size() == 2 && inputs.size() <= 2) {
      List<Map.Entry<String, Long>> sortedOutputs = outputs.entrySet().stream()
          .filter(entry -> entry.getKey() != null && entry.getValue() > 546)
          .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
          .toList();

      if (sortedOutputs.size() == 2) {
        Map.Entry<String, Long> larger = sortedOutputs.get(0);
        Map.Entry<String, Long> smaller = sortedOutputs.get(1);

        // 更严格的找零判断：小额输出且回到输入地址
        boolean isLikelyChange = inputs.containsKey(smaller.getKey()) &&
            smaller.getValue() < larger.getValue() * 0.3; // 降低阈值避免误判

        if (isLikelyChange) {
          filtered.remove(smaller.getKey());
        }
      }
    }

    return filtered;
  }

  /**
   * 解析输入地址和金额
   */
  private Map<String, Long> parseInputs(Transaction tx, Map<Sha256Hash, Transaction> utxoMap) {
    Map<String, Long> inputs = new HashMap<>();

    for (TransactionInput input : tx.getInputs()) {
      try {
        Transaction prevTx = utxoMap.get(input.getOutpoint().hash());
        if (prevTx != null) {
          int outputIndex = (int) input.getOutpoint().index();
          if (outputIndex < prevTx.getOutputs().size()) {
            TransactionOutput prevOutput = prevTx.getOutputs().get(outputIndex);
            String address = getAddress(prevOutput.getScriptPubKey());
            if (address != null) {
              inputs.merge(address, prevOutput.getValue().value, Long::sum);
            }
            continue;
          }
        }

        Script scriptSig = input.getScriptSig();
        String fallbackAddr = getAddress(scriptSig);
        if (fallbackAddr != null) {
          inputs.merge(fallbackAddr, 0L, Long::sum);
        }
      } catch (Exception e) {
        // 静默处理解析失败的情况
      }
    }
    return inputs;
  }

  /**
   * 解析输出地址和金额
   */
  private Map<String, Long> parseOutputs(Transaction tx) {
    Map<String, Long> outputs = new HashMap<>();

    for (TransactionOutput output : tx.getOutputs()) {
      try {
        String address = getAddress(output.getScriptPubKey());
        if (address != null) {
          outputs.merge(address, output.getValue().value, Long::sum);
        }
      } catch (Exception ignored) {
      }
    }

    return outputs;
  }

  /**
   * 提取地址 - 增强版
   */
  private String getAddress(Script script) {
    if (script == null || script.program().length == 0) {
      return null;
    }
    try {
      Address address = script.getToAddress(BitcoinNetwork.MAINNET, false);
      if (address != null) {
        return address.toString();
      }
    } catch (Exception e) {
      try {
        Address address = script.getToAddress(BitcoinNetwork.MAINNET, true);
        if (address != null) {
          return address.toString();
        }
      } catch (Exception ignored) {
      }
    }
    String manualAddress = parseScriptManually(script);
    if (manualAddress != null) {
      return manualAddress;
    }
    // 生成未知地址标识
    byte[] program = script.program();
    String hash = Sha256Hash.of(program).toString().substring(0, 12);
    return "UNKNOWN_" + hash;
  }

  /**
   * 手动解析脚本中的地址
   */
  private String parseScriptManually(Script script) {
    try {
      ScriptType scriptType = script.getScriptType();
      if (scriptType == null) {
        return null;
      }
      switch (scriptType) {
        case P2PKH:
          if (ScriptPattern.isP2PKH(script)) {
            byte[] hash160 = ScriptPattern.extractHashFromP2PKH(script);
            return LegacyAddress.fromPubKeyHash(BitcoinNetwork.MAINNET, hash160).toString();
          }
          break;
        case P2SH:
          if (ScriptPattern.isP2SH(script)) {
            byte[] hash160 = ScriptPattern.extractHashFromP2SH(script);
            return LegacyAddress.fromScriptHash(BitcoinNetwork.MAINNET, hash160).toString();
          }
          break;
        case P2PK:
          if (ScriptPattern.isP2PK(script)) {
            byte[] pubKey = ScriptPattern.extractKeyFromP2PK(script);
            ECKey key = ECKey.fromPublicOnly(pubKey);
            return key.toAddress(ScriptType.P2PKH, BitcoinNetwork.MAINNET).toString();
          }
          break;
        case P2WPKH:
          if (ScriptPattern.isP2WPKH(script)) {
            byte[] hash160 = ScriptPattern.extractHashFromP2WH(script);
            return SegwitAddress.fromHash(BitcoinNetwork.MAINNET, hash160).toString();
          }
          break;
        case P2WSH:
          if (ScriptPattern.isP2WSH(script)) {
            byte[] hash256 = ScriptPattern.extractHashFromP2WH(script);
            return SegwitAddress.fromHash(BitcoinNetwork.MAINNET, hash256).toString();
          }
          break;
        case P2TR:
          if (ScriptPattern.isP2TR(script)) {
            byte[] outputKey = ScriptPattern.extractOutputKeyFromP2TR(script);
            return SegwitAddress.fromProgram(BitcoinNetwork.MAINNET, 1, outputKey).toString();
          }
          break;
        default:
          return null;
      }

    } catch (Exception ignored) {
    }
    return null;
  }

  /**
   * 判断是否为大额转账
   */
  private boolean isLargeTransfer(long amountSatoshi) {
    return BigDecimal.valueOf(amountSatoshi)
        .compareTo(LARGE_TRANSFER_THRESHOLD.multiply(BigDecimal.valueOf(100_000_000L))) >= 0;
  }

  /**
   * 保存大额转账记录
   */
  private void saveLargeTransfer(Transaction tx, Block block, int blockHeight, String blockHash,
                                 TransferInfo transfer, Map<String, Long> inputs, Map<String, Long> outputs) {

    long totalInput = inputs.values().stream().mapToLong(Long::longValue).sum();
    long totalOutput = outputs.values().stream().mapToLong(Long::longValue).sum();

    BigDecimal transferAmount = satoshiToBtc(transfer.amount);
    BigDecimal totalInputBtc = satoshiToBtc(totalInput);
    BigDecimal totalOutputBtc = satoshiToBtc(totalOutput);

    LargeTransfer largeTransfer = new LargeTransfer();
    largeTransfer.setTxHash(tx.getTxId().toString());
    largeTransfer.setBlockHash(blockHash);
    largeTransfer.setBlockHeight(blockHeight);
    largeTransfer.setTimestamp(LocalDateTime.ofInstant(block.time(), ZoneId.systemDefault()));
    largeTransfer.setFromAddress(transfer.fromAddress);
    largeTransfer.setToAddress(transfer.toAddress);
    largeTransfer.setTransferAmount(transferAmount);
    largeTransfer.setTotalInput(totalInputBtc);
    largeTransfer.setTotalOutput(totalOutputBtc);

    largeTransfers.put(tx.getTxId().toString(), largeTransfer);

    log.info("大额转账 [{}]: {} -> {} {} BTC TX:{}",
        transfer.strategy,
        transfer.fromAddress,
        transfer.toAddress,
        transferAmount,
        tx.getTxId());
  }

  /**
   * Satoshi转BTC
   */
  private BigDecimal satoshiToBtc(long satoshi) {
    return BigDecimal.valueOf(satoshi)
        .divide(BigDecimal.valueOf(100_000_000L), 2, RoundingMode.HALF_UP);
  }

  /**
   * 转账信息内部类
   */
  private record TransferInfo(String fromAddress, String toAddress, long amount, String strategy) {
  }

}
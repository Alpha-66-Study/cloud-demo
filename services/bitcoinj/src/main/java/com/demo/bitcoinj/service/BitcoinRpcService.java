package com.demo.bitcoinj.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.base.Sha256Hash;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class BitcoinRpcService {

  private final BlockChain blockChain;
  private final PeerGroup peerGroup;

  /**
   * 获取本地最新区块高度
   */
  public int getLocalLatestBlockHeight() {
    try {
      int height = blockChain.getBestChainHeight();
      return height > 0 ? height : -1;
    } catch (Exception e) {
      log.error("获取本地最新区块高度失败", e);
      return -1;
    }
  }

  /**
   * 获取网络最新区块高度
   */
  public int getLatestBlockHeight() {
    try {
      return peerGroup.getMostCommonChainHeight();
    } catch (Exception e) {
      log.error("获取网络最新区块高度失败", e);
      return -1;
    }
  }

  /**
   * 获本地指定区块高度
   */
  public int getLocalBlockHeightByHash(String blockHash) {
    try {
      Sha256Hash hash = Sha256Hash.wrap(blockHash);
      StoredBlock storedBlock = blockChain.getBlockStore().get(hash);
      return (storedBlock != null) ? storedBlock.getHeight() : -1;
    } catch (Exception e) {
      log.error("获取本地区块 {} 高度失败", blockHash, e);
      return -1;
    }
  }

  /**
   * 获取本地最新区块hash
   */
  public String getLocalLatestBlockHash() {
    try {
      return Optional.ofNullable(blockChain.getChainHead())
          .map(StoredBlock::getHeader)
          .map(Block::getHashAsString)
          .orElse(null);
    } catch (Exception e) {
      log.error("获取本地最新区块hash失败", e);
      return null;
    }
  }

  /**
   * 获取本地指定区块hash
   */
  public String getLocalBlockHash(long height) {
    try {
      StoredBlock block = findBlockByHeight(height);
      return (block != null) ? block.getHeader().getHashAsString() : null;
    } catch (Exception e) {
      log.error("获取本地指定区块hash {}", height, e);
      return null;
    }
  }

  private StoredBlock findBlockByHeight(long height) throws BlockStoreException {
    StoredBlock current = blockChain.getChainHead();
    while (current != null && current.getHeight() > height) {
      current = blockChain.getBlockStore().get(current.getHeader().getPrevBlockHash());
    }
    return (current != null && current.getHeight() == height) ? current : null;
  }

  /**
   * 获取网络指定区块
   */
  public Block getBlockByHash(String blockHash) {
    try {
      Sha256Hash hash = Sha256Hash.wrap(blockHash);
      peerGroup.waitForPeers(3).get();
      List<Peer> peers = peerGroup.getConnectedPeers();
      Peer peer = peers.get((int) (Math.random() * peers.size()));
      Future<Block> future = peer.getBlock(hash);
      return future.get();
    } catch (Exception e) {
      log.error("获取网络区块哈希 {} 失败", blockHash, e);
      return null;
    }
  }

}
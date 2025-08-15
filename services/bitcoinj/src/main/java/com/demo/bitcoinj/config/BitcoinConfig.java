package com.demo.bitcoinj.config;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.base.BitcoinNetwork;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.store.SPVBlockStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Slf4j
@Configuration
public class BitcoinConfig {

  private final BitcoinNetwork network = BitcoinNetwork.MAINNET;
  private final NetworkParameters params = NetworkParameters.of(network);

  @Bean
  public BlockChain blockChain() throws Exception {
    File dir = new File("doc/bitcoin");
    if (!dir.exists() && !dir.mkdirs()) {
      throw new RuntimeException("无法创建目录 " + dir.getAbsolutePath());
    }
    File blockStoreFile = new File(dir, "mainnet.spvblockstore");
    SPVBlockStore blockStore = new SPVBlockStore(params, blockStoreFile);
    return new BlockChain(network, blockStore);
  }

  @Bean
  public PeerGroup peerGroup(BlockChain blockChain) {
    Context.propagate(new Context());
    PeerGroup peerGroup = new PeerGroup(network, blockChain);
    peerGroup.addPeerDiscovery(new DnsDiscovery(network));
    peerGroup.addBlocksDownloadedEventListener((peer, block, filteredBlock, blocksLeft) ->
        log.info("同步区块: {} 剩余区块: {} 哈希: {}", blockChain.getBestChainHeight(), blocksLeft, block.getHashAsString()));
    peerGroup.start();
    peerGroup.startBlockChainDownload(null);
    return peerGroup;
  }

}
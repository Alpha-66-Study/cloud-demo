package com.demo.storage.service.impl;

import com.demo.storage.mapper.StorageTblMapper;
import com.demo.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

  private final StorageTblMapper storageTblMapper;

  @Transactional
  @Override
  public void deduct(String commodityCode, int count) {
    storageTblMapper.deduct(commodityCode, count);
    if (count == 5) {
      throw new RuntimeException("库存不足");
    }
  }
}

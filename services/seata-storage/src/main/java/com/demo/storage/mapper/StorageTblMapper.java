package com.demo.storage.mapper;

import com.demo.storage.bean.StorageTbl;

public interface StorageTblMapper {

  int deleteByPrimaryKey(Long id);

  int insert(StorageTbl record);

  int insertSelective(StorageTbl record);

  StorageTbl selectByPrimaryKey(Long id);

  int updateByPrimaryKeySelective(StorageTbl record);

  int updateByPrimaryKey(StorageTbl record);

  void deduct(String commodityCode, int count);
}

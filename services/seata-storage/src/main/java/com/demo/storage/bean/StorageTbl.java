package com.demo.storage.bean;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * @TableName storage_tbl
 */
@Data
public class StorageTbl implements Serializable {
  private Integer id;

  private String commodityCode;

  private Integer count;

  @Serial
  private static final long serialVersionUID = 1L;
}
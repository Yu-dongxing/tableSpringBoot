package com.wzz.table.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.wzz.table.pojo.FinancialRecord;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor // 使用Lombok的AllArgsConstructor注解简化构造函数
public class BatchInfo {
    /**
     * 批次ID
     */
    private Long batchId;

    /**
     * 该批次的数据总条数
     */
    private int count;

    /**
     * 该批次的总金额
     */
    private Double totalPrice;

    /**
     * 该批次中 `changes` 最大记录的 `user_id`
     */
    private Long userId;

    /**
     * 该批次中 `changes` 最大记录的 `make` 标记
     */
    private String make;

    /**
     * 该批次中最新的记录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    /**
     * 该批次中最大的 `changes` 值
     */
    private Long maxChanges;

    /**
     * 该批次中最大的 `price` 值
     */
    private Double maxPrice;

    /**
     * 该批次的所有记录数据列表
     */
    private List<FinancialRecord> records;
}

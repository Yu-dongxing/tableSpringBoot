package com.wzz.table.DTO;

import com.wzz.table.pojo.FinancialRecord;
import lombok.Data;

import java.util.List;

@Data
public class BatchData {
    /**
     * 该批次的数据总条数
     */
    private long count;

    /**
     * 新增：该批次的总金额
     */
    private Double totalPrice;

    /**
     * 该批次的所有记录数据列表
     */
    private List<FinancialRecord> records;

    // 更新构造函数以接收总金额
    public BatchData(long count, Double totalPrice, List<FinancialRecord> records) {
        this.count = count;
        this.totalPrice = totalPrice;
        this.records = records;
    }
}

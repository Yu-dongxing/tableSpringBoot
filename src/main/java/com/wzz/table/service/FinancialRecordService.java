package com.wzz.table.service;

import com.wzz.table.pojo.FinancialRecord;

import java.util.List;

public interface FinancialRecordService {
    Boolean add(FinancialRecord f);

    List<FinancialRecord> findByBatch(String batchId);

    Long getMaxBatch();

    Long getNextBatchId();

    List<String> findBatchIdsByMark(String mark);

    Boolean cleanupOldData();
}

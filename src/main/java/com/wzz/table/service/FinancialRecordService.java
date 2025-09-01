package com.wzz.table.service;

import com.wzz.table.pojo.FinancialRecord;

import java.util.List;
import java.util.Map;

public interface FinancialRecordService {
    Boolean add(FinancialRecord f);

    List<FinancialRecord> findByBatch(String batchId);

    Long getMaxBatch();

    Long getNextBatchId();

    List<String> findBatchIdsByMark(String mark);

    Boolean cleanupOldData();

    Boolean cleanupAllData();

    Map<String, Object> findAllBatchesWithDetails();


    List<FinancialRecord> findRecordsByBatchId(Long batchId);

    String generateRecordsImageBase64(List<FinancialRecord> records);

    Long countByUserId(Long userId);
}

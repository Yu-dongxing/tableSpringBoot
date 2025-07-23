package com.wzz.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wzz.table.DTO.BatchInfo;
import com.wzz.table.mapper.FinancialRecordMapper;
import com.wzz.table.pojo.FinancialRecord;
import com.wzz.table.service.FinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
        import java.util.stream.Collectors;

@Service
public class FinancialRecordServiceImpl implements FinancialRecordService {
    @Autowired
    private FinancialRecordMapper financialRecordMapper;
    @Override
    public Boolean add(FinancialRecord f) {
        int i = financialRecordMapper.insert(f);
        return i > 0;
    }

    @Override
    public List<FinancialRecord> findByBatch(String batchId) {
        return financialRecordMapper.selectList( new LambdaQueryWrapper<FinancialRecord>().eq(FinancialRecord::getBatch, batchId));
    }

    @Override
    public Long getMaxBatch() {
        LambdaQueryWrapper<FinancialRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(FinancialRecord::getBatch);
        List<Object> batchList = financialRecordMapper.selectObjs(queryWrapper);
        if (batchList.isEmpty()) {
            return null;
        }
        return batchList.stream().mapToLong(o -> Long.parseLong(o.toString())).max().getAsLong();
    }

    @Override
    public Long getNextBatchId() {
        Long currentMaxBatch = getMaxBatch(); // 获取当前最大批次值
        if (currentMaxBatch == null) {
            currentMaxBatch = 0L; // 如果没有批次值，从 0 开始
        }
        return currentMaxBatch + 1; // 返回下一个批次值
    }

    //根据mark查询batch
    @Override
    public List<String> findBatchIdsByMark(String mark) {
        LambdaQueryWrapper<FinancialRecord> queryWrapper = new LambdaQueryWrapper<>();
        // 查询条件：make 等于传入的 mark
        queryWrapper.eq(FinancialRecord::getMake, mark);
        // 选择查询的字段为 batch
        queryWrapper.select(FinancialRecord::getBatch);
        // 执行查询，获取 batch 列的值列表
        List<Object> batchList = financialRecordMapper.selectObjs(queryWrapper);
        // 将结果转换为 String 类型的列表返回
        return batchList.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean cleanupOldData() {
        // 获取当天 6 点的时间
        LocalDateTime sixAm = LocalDateTime.now().withHour(6).withMinute(0).withSecond(0).withNano(0);
        // 构建删除条件：创建时间早于 6 点
        LambdaUpdateWrapper<FinancialRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.lt(FinancialRecord::getCrTime, sixAm);
        // 执行删除操作
        int n = financialRecordMapper.delete(updateWrapper);
        return n > 0;
    }
    /**
     * 核心方法更新：查询所有批次，并统计每个批次的数据条数、总金额，
     * 并找出 changes 最大的记录所对应的 userId、make，以及批次内最大的 price 和最新的时间。
     */
    @Override
    public Map<String, Object> findAllBatchesWithDetails() {
        // 1. 一次性从数据库查询出所有记录
        List<FinancialRecord> allRecords = financialRecordMapper.selectList(null);
        if (allRecords == null || allRecords.isEmpty()) {
            return Collections.singletonMap("batch", Collections.emptyList());
        }

        // 2. 根据 batch 字段对所有记录进行分组
        Map<Long, List<FinancialRecord>> groupedByBatchId = allRecords.stream()
                .collect(Collectors.groupingBy(FinancialRecord::getBatch));

        // 3. 将分组后的 Map 转换为最终需要的包含批次详情的 List<BatchInfo>
        List<BatchInfo> batchInfoList = groupedByBatchId.entrySet().stream()
                .map(entry -> {
                    Long batchId = entry.getKey();
                    List<FinancialRecord> recordsInBatch = entry.getValue();

                    // 使用 BigDecimal 计算总金额，保证精度
                    BigDecimal totalPrice = recordsInBatch.stream()
                            .map(FinancialRecord::getPrice)
                            .filter(Objects::nonNull)
                            .map(String::valueOf)
                            .map(BigDecimal::new)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // 查找批次内 `changes` 值最大的记录
                    Optional<FinancialRecord> recordWithMaxChanges = recordsInBatch.stream()
                            .filter(r -> r.getChanges() != null)
                            .max(Comparator.comparing(FinancialRecord::getChanges));

                    // 从 `changes` 最大的记录中获取 userId, make 和 maxChanges
                    // 如果找不到，则提供默认值
                    Long userIdWithMaxChanges = recordWithMaxChanges.map(FinancialRecord::getUserId).orElse(0L);
                    String make = recordWithMaxChanges.map(FinancialRecord::getMake).orElse("0");
                    Long maxChanges = recordWithMaxChanges.map(FinancialRecord::getChanges).orElse(0L);

                    // 查找批次内最大的 `price`
                    Double maxPrice = recordsInBatch.stream()
                            .map(FinancialRecord::getPrice)
                            .filter(Objects::nonNull)
                            .mapToDouble(Double::doubleValue)
                            .max()
                            .orElse(0.0);

                    // 查找批次内最新的 `crTime`
                    LocalDateTime latestTime = recordsInBatch.stream()
                            .map(FinancialRecord::getCrTime)
                            .filter(Objects::nonNull)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    // 创建并返回包含所有所需信息的 BatchInfo 对象
                    return new BatchInfo(
                            batchId,
                            recordsInBatch.size(),
                            totalPrice.setScale(2, RoundingMode.HALF_UP).doubleValue(),
                            userIdWithMaxChanges,
                            make,
                            latestTime,
                            maxChanges,
                            maxPrice,
                            recordsInBatch
                    );
                })
                .sorted(Comparator.comparing(BatchInfo::getBatchId)) // 可选：按 batchId 排序
                .collect(Collectors.toList());

        // 4. 构建最终返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("batch", batchInfoList);
        return result;
    }
}

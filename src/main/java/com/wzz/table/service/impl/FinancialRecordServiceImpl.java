package com.wzz.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wzz.table.DTO.BatchData;
import com.wzz.table.mapper.FinancialRecordBatchMapper;
import com.wzz.table.mapper.FinancialRecordMapper;
import com.wzz.table.pojo.FinancialRecord;
import com.wzz.table.service.FinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinancialRecordServiceImpl implements FinancialRecordService {
    @Autowired
    private FinancialRecordMapper financialRecordMapper;
    @Autowired
    private FinancialRecordBatchMapper financialRecordBatchMapper;
    @Override
    public Boolean add(FinancialRecord f) {
        int i = financialRecordMapper.insert(f);
        if (i > 0) {
            return true;
        }else {
            return false;
        }
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
        if (n > 0) {
            return true;
        }else {
            return false;
        }
    }
    /**
     * 核心方法更新：查询所有批次，并统计每个批次的数据条数和总金额
     */
    @Override
    public Map<String, BatchData> findAllBatchesWithDetails() {
        // 1. 一次性从数据库查询出所有记录
        List<FinancialRecord> allRecords = financialRecordMapper.selectList(null);
        if (allRecords == null || allRecords.isEmpty()) {
            return Collections.emptyMap();
        }

        // 2. 根据 batch 字段对所有记录进行分组
        Map<Long, List<FinancialRecord>> groupedByBatchId = allRecords.stream()
                .collect(Collectors.groupingBy(FinancialRecord::getBatch));

        // 3. 将分组后的 Map 转换为最终需要的包含统计数据的结构
        return groupedByBatchId.entrySet().stream()
                .collect(Collectors.toMap(
                        // Map的键：将批次ID (Long) 转换为 String
                        entry -> entry.getKey().toString(),

                        // Map的值：为每个批次创建一个新的BatchData对象
                        entry -> {
                            // 获取当前批次的所有记录列表
                            List<FinancialRecord> recordsInBatch = entry.getValue();

                            // 新增逻辑：计算总金额
                            // 使用stream流处理，先过滤掉price可能为null的记录（增加代码健壮性），
                            // 然后将每个FinancialRecord对象的price映射为Double值，最后求和。
                            double sumOfPrice = recordsInBatch.stream()
                                    .filter(record -> record.getPrice() != null)
                                    .mapToDouble(FinancialRecord::getPrice)
                                    .sum();

                            // 创建并返回包含总条数、总金额和数据列表的BatchData对象
                            return new BatchData(recordsInBatch.size(), sumOfPrice, recordsInBatch);
                        }
                ));
    }
}

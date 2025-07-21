package com.wzz.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wzz.table.mapper.FinancialRecordBatchMapper;
import com.wzz.table.mapper.FinancialRecordMapper;
import com.wzz.table.pojo.FinancialRecord;
import com.wzz.table.service.FinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
}

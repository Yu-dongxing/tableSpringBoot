package com.wzz.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wzz.table.mapper.FinancialRecordBatchMapper;
import com.wzz.table.mapper.FinancialRecordMapper;
import com.wzz.table.pojo.FinancialRecord;
import com.wzz.table.service.FinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

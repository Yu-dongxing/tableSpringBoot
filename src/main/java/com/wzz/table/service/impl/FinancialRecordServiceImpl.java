package com.wzz.table.service.impl;

import com.wzz.table.mapper.FinancialRecordBatchMapper;
import com.wzz.table.mapper.FinancialRecordMapper;
import com.wzz.table.pojo.FinancialRecord;
import com.wzz.table.service.FinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinancialRecordServiceImpl implements FinancialRecordService {
    @Autowired
    private FinancialRecordMapper financialRecordMapper;
    @Autowired
    private FinancialRecordBatchMapper financialRecordBatchMapper;
    @Override
    public Boolean add(FinancialRecord f) {
        int i = financialRecordMapper.insert(f);

        return null;
    }
}

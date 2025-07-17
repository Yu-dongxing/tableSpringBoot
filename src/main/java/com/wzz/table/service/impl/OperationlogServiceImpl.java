package com.wzz.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzz.table.mapper.OperationlogMapper;
import com.wzz.table.pojo.Operationlog;
import com.wzz.table.service.OperationlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperationlogServiceImpl implements OperationlogService {
    @Autowired
    private OperationlogMapper operationlogMapper;

    @Override
    public List<Operationlog> finAll() {
        List<Operationlog> l = operationlogMapper.selectList(null);
        return l;
    }

    @Override
    public Boolean add(Operationlog operationlog) {
        int i = operationlogMapper.insert(operationlog);
        if(i>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public IPage<Operationlog> findPage(int page, int pageSize) {
        return operationlogMapper.selectPage(new Page<>(page,pageSize),null);
    }

    @Override
    public IPage<Operationlog> findPageBytime(int page, int pageSize, LocalDateTime startTime, LocalDateTime endTime) {
        // 创建分页对象
        Page<Operationlog> pageObj = new Page<>(page, pageSize);
        // 创建查询条件
        LambdaQueryWrapper<Operationlog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(startTime != null, Operationlog::getCrTime, startTime);
        queryWrapper.le(endTime != null, Operationlog::getCrTime, endTime);
        // 执行查询
        return operationlogMapper.selectPage(pageObj, queryWrapper);
    }
}

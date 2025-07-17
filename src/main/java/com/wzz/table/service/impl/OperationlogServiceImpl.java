package com.wzz.table.service.impl;

import com.wzz.table.mapper.OperationlogMapper;
import com.wzz.table.pojo.Operationlog;
import com.wzz.table.service.OperationlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

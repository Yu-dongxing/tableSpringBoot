package com.wzz.table.service;

import com.wzz.table.pojo.Operationlog;

import java.util.List;

public interface OperationlogService {
    List<Operationlog> finAll();

    Boolean add(Operationlog operationlog);
}

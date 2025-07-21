package com.wzz.table.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wzz.table.DTO.OperationlogSelectDto;
import com.wzz.table.pojo.Operationlog;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationlogService {
    List<Operationlog> finAll();

    Boolean add(Operationlog operationlog);

    IPage<Operationlog> findPage(int page, int pageSize);

    IPage<Operationlog> findPageBytime(int page, int pageSize, LocalDateTime startTime, LocalDateTime endTime);

    IPage<Operationlog> findByTimeOrUserOrPointUserOrOpenLsPage(OperationlogSelectDto operationlogSelectDto);
}

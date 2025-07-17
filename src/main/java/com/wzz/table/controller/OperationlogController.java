package com.wzz.table.controller;


import com.wzz.table.DTO.Result;
import com.wzz.table.pojo.Operationlog;
import com.wzz.table.service.OperationlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//操作日志接口
@RestController
@RequestMapping("/api/operationlog")
public class OperationlogController {
    @Autowired
    private OperationlogService operationlogService;

    @GetMapping("/all")
    public Result<List<Operationlog>> findAll(){
        List<Operationlog> ls = operationlogService.finAll();
        return Result.success(ls);
    }
}

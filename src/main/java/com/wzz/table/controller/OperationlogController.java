package com.wzz.table.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wzz.table.DTO.Result;
import com.wzz.table.pojo.Operationlog;
import com.wzz.table.service.OperationlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

//操作日志接口
@SaCheckRole("0")
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

    //分页查询所有
    @GetMapping("/page")
    public Result<IPage<Operationlog>> findPage(@RequestParam int page, @RequestParam int pageSize){
        IPage<Operationlog> l = operationlogService.findPage(page,pageSize);
        return Result.success(l);
    }
    //根据日期时间查询
    @GetMapping("/page/time")
    public Result<IPage<Operationlog>> findByTimePage(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime startTime, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime, int page, int size){
        IPage<Operationlog> s = operationlogService.findPageBytime(page,size,startTime,endTime);
        return Result.success(s);
    }
}

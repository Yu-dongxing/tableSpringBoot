package com.wzz.table.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wzz.table.DTO.OperationlogSelectDto;
import com.wzz.table.DTO.Result;
import com.wzz.table.pojo.Operationlog;
import com.wzz.table.service.OperationlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Result<IPage<Operationlog>> findByTimePage(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "10") int size){
        System.out.println(startTime);
        IPage<Operationlog> s = operationlogService.findPageBytime(page,size,startTime,endTime);
        return Result.success(s);
    }

    //根据 操作用户+积分用户+时间查询+操作类型（增加或者减少）（4个条件有任意都能查询 比如操作用户可查询 操作用户+积分用户可查询 以此类推
    @GetMapping("/page/find")
    public Result<IPage<Operationlog>> findByTimeOrUserOrPointUserOrOpenLsPage(@RequestBody OperationlogSelectDto operationlogSelectDto){
        if(operationlogSelectDto.getPage()==0){
            operationlogSelectDto.setPage(1);
        }
        if(operationlogSelectDto.getSize()==0){
            operationlogSelectDto.setSize(10);
        }
        System.out.println(operationlogSelectDto);
        IPage<Operationlog> s = operationlogService.findByTimeOrUserOrPointUserOrOpenLsPage(operationlogSelectDto);
        return Result.success(s);
    }
}

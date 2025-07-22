package com.wzz.table.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wzz.table.DTO.OperationlogSelectDto;
import com.wzz.table.DTO.Result;
import com.wzz.table.pojo.Operationlog;
import com.wzz.table.service.OperationlogService;
import com.wzz.table.utils.DateTimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger log = LogManager.getLogger(OperationlogController.class);
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
        IPage<Operationlog> s = operationlogService.findPageBytime(page,size,startTime,endTime);
        return Result.success(s);
    }

    //根据 操作用户+积分用户+时间查询+操作类型（增加或者减少）（4个条件有任意都能查询 比如操作用户可查询 操作用户+积分用户可查询 以此类推
    @PostMapping("/page/find")
    public Result<IPage<Operationlog>> findByTimeOrUserOrPointUserOrOpenLsPage(@RequestBody OperationlogSelectDto operationlogSelectDto){
        log.info("多项查询："+operationlogSelectDto.toString());
        // 日期解析
        LocalDateTime start = null, end = null;
        // 判断开始时间是否为空或空字符串
        if (operationlogSelectDto.getStartTime() != null && !operationlogSelectDto.getStartTime().isEmpty() && !StrUtil.hasBlank(operationlogSelectDto.getStartTime())
        ) {
            start = DateTimeUtil.parseDateTime(operationlogSelectDto.getStartTime());
            // 验证解析后的开始时间是否为 null
            if (start == null) {
                return Result.error("开始时间格式不正确：" + operationlogSelectDto.getStartTime());
            }
            // 将解析后的开始时间设置回去
            operationlogSelectDto.setStartTime(String.valueOf(start));
        } else {
            // 如果开始时间为空或空字符串，可以设置为默认值或进行其他处理
            operationlogSelectDto.setStartTime(null);
        }
        // 判断结束时间是否为空或空字符串
        if (operationlogSelectDto.getEndTime() != null && !operationlogSelectDto.getEndTime().isEmpty() && !StrUtil.hasBlank(operationlogSelectDto.getEndTime())) {
            end = DateTimeUtil.parseDateTime(operationlogSelectDto.getEndTime());
            // 验证解析后的结束时间是否为 null
            if (end == null) {
                return Result.error("结束时间格式不正确：" + operationlogSelectDto.getEndTime());
            }
            // 将解析后的结束时间设置回去
            operationlogSelectDto.setEndTime(String.valueOf(end));
        } else {
            // 如果结束时间为空或空字符串，可以设置为默认值或进行其他处理
            operationlogSelectDto.setEndTime(null);
        }

        if(operationlogSelectDto.getPage()==0){
            operationlogSelectDto.setPage(1);
        }
        if(operationlogSelectDto.getSize()==0){
            operationlogSelectDto.setSize(10);
        }
        log.info("多项查询,日期时间转换后："+operationlogSelectDto.toString());
        IPage<Operationlog> s = operationlogService.findByTimeOrUserOrPointUserOrOpenLsPage(operationlogSelectDto);
        if(s.getTotal()>0){
            return Result.success("查询成功！",s);
        }
        return Result.error("没有数据，查询条件："+operationlogSelectDto.toString());
    }
}

package com.wzz.table.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.wzz.table.DTO.FinancialRecordDto;
import com.wzz.table.DTO.FinancialRecordListDto;
import com.wzz.table.DTO.Result;
import com.wzz.table.pojo.FinancialRecord;
import com.wzz.table.service.FinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

//财务系统接口(后台)
@RestController
@RequestMapping("/api/root/financialrecord")
public class RootFinancialRecordController {
    @Autowired
    private FinancialRecordService financialRecordService;
    @PostMapping("/up")
    public Result<String> addList(@RequestBody FinancialRecordDto financialRecordDto) {
        for (FinancialRecordListDto item : financialRecordDto.getData()) {
            FinancialRecord f = new FinancialRecord();
            f.setMake(financialRecordDto.getMake());
            f.setIds(financialRecordDto.getIds());
            f.setChanges(item.getChanges());
            f.setName(item.getName());
            f.setPrice(item.getPrice());
            f.setQuantity(item.getQuantity());
            f.setBalance(item.getBalance());
            f.setLastBalance(item.getLastBalance());
            f.setOrders(item.getOrders());
            f.setPrice(item.getPrice());
            f.setCrTime(LocalDateTime.now());
            Boolean is = financialRecordService.add(f);
        }
        return Result.success("ok");
    }

}

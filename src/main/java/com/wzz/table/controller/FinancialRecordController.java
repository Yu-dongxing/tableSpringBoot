package com.wzz.table.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.wzz.table.service.FinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//财务系统接口
@SaCheckRole("0")
@RestController
@RequestMapping("/api/financialrecord")
public class FinancialRecordController {
    @Autowired
    private FinancialRecordService financialRecordService;

}

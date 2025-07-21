package com.wzz.table.tasks;

import com.wzz.table.service.FinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTasks {
    @Autowired
    private FinancialRecordService financialRecordService;

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void executeTask() {
        Boolean is = financialRecordService.cleanupOldData();
        System.out.println("定时任务执行，时间：" + new Date()+"，是否执行成功："+is);
    }
}

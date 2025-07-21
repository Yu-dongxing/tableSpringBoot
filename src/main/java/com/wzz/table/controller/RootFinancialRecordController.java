package com.wzz.table.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.wzz.table.DTO.FinancialRecordDto;
import com.wzz.table.DTO.FinancialRecordListDto;
import com.wzz.table.DTO.Result;
import com.wzz.table.pojo.FinancialRecord;
import com.wzz.table.service.FinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//财务系统接口(后台)
@RestController
@RequestMapping("/api/root/financialrecord")
public class RootFinancialRecordController {
    @Autowired
    private FinancialRecordService financialRecordService;
    private final Object syncLock = new Object(); // 用于线程锁的对象

    @PostMapping("/up")
    public Result<String> addList(@RequestBody FinancialRecordDto financialRecordDto) {
        synchronized (syncLock) { // 添加线程锁
            Long batchSize = financialRecordService.getNextBatchId(); // 获取下一个批次值

            //String baId = generateBatchId();
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
                f.setBatch(batchSize);

                Boolean is = financialRecordService.add(f);
                if(is){
                    System.out.println("插入成功");
                }else {
                    System.out.println("插入错误");
                }
            }
            return Result.success("成功，返回当前批次ID", batchSize.toString());
        }
    }
    //获取随机数
    public static String generateBatchId() {
        long timestamp = Instant.now().toEpochMilli(); // 获取当前时间的毫秒级时间戳
        int random = (int) (Math.random() * 10000); // 生成一个 0-9999 的随机数
        return timestamp + "-" + String.format("%04d", random); // 格式化为固定长度
    }
    //根据批次id查询一组数据
    @GetMapping("/find")
    public Result<List<FinancialRecord>> findByBatch(@RequestParam String batchId) {
        if (batchId == null || batchId.isEmpty()) {
            return Result.error("批次不能为空！");
        }
        List<FinancialRecord> l = financialRecordService.findByBatch(batchId);
        if (l == null || l.isEmpty()) {
            return Result.error("查询错误，没该条件的数据？");
        }
        return Result.success(l);
    }
    //根据标记查询每一组数据
    @GetMapping("/find/mark")
    public Result<Map<String, List<FinancialRecord>>> findByMark(@RequestParam String mark) {
        if (mark == null || mark.isEmpty()) {
            return Result.error("标记不能为空！");
        }

        // 第一步：根据 mark 查询相关的批次
        List<String> batchIds = financialRecordService.findBatchIdsByMark(mark);
        System.out.println("batchIds = " + batchIds);
        if (batchIds == null || batchIds.isEmpty()) {
            return Result.error("未找到与该标记相关的批次！");
        }

        // 第二步：根据每个批次查询对应的财务记录
        Map<String, List<FinancialRecord>> batchRecordMap = new HashMap<>();
        for (String batchId : batchIds) {
            List<FinancialRecord> records = financialRecordService.findByBatch(batchId);
            if (records != null && !records.isEmpty()) {
                batchRecordMap.put(batchId, records);
            }
        }

        if (batchRecordMap.isEmpty()) {
            return Result.error("未找到与该标记相关的数据！");
        }

        return Result.success("查询成功", batchRecordMap);
    }
}

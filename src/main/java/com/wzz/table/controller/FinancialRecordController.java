package com.wzz.table.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.StrUtil;
import com.wzz.table.DTO.FinancialRecordDto;
import com.wzz.table.DTO.FinancialRecordListDto;
import com.wzz.table.DTO.Result;
import com.wzz.table.exception.BusinessException;
import com.wzz.table.pojo.FinancialRecord;
import com.wzz.table.service.FinancialRecordService;
import com.wzz.table.utils.DateTimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

//财务系统接口
@SaCheckRole("0")
@RestController
@RequestMapping("/api/financialrecord")
public class FinancialRecordController {
    private static final Logger log = LogManager.getLogger(RootFinancialRecordController.class);
    @Autowired
    private FinancialRecordService financialRecordService;
    private final Object syncLock = new Object(); // 用于线程锁的对象

    @PostMapping("/up")
    public Result<String> addList(@RequestBody FinancialRecordDto financialRecordDto) {
        synchronized (syncLock) { // 添加线程锁
            Long batchSize = financialRecordService.getNextBatchId(); // 获取下一个批次值

            //String baId = generateBatchId();
            for (FinancialRecordListDto item : financialRecordDto.getData()) {
                LocalDateTime crTime = null;
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
                f.setUserId(item.getUserId());
                f.setBatch(batchSize);
                if(!StrUtil.hasBlank(item.getCrTime())){
                    crTime = DateTimeUtil.parseDateTime(item.getCrTime());
                }else {
                    crTime = LocalDateTime.now();
                }
                f.setCrTime(crTime);
                Boolean is = financialRecordService.add(f);
                if(is){
                    log.info("插入成功数据{}",f.toString());
                }else {
                    log.info("插入错误,数据{}",f.toString());
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
    //根据标记查询每一组数据
    @GetMapping("/find/make")
    public Result<Map<String, Object>> findByMark(@RequestParam String make) {
        if (make == null || make.isEmpty()) {
            return Result.error("标记不能为空！");
        }

        // 第一步：根据 mark 查询相关的批次ID
        List<String> batchIds = financialRecordService.findBatchIdsByMark(make);
        if (batchIds == null || batchIds.isEmpty()) {
            return Result.error("未找到与该标记相关的批次！");
        }

        // 创建一个列表来存储最终的批次数据
        List<Map<String, Object>> batchList = new ArrayList<>();
        // 定义时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 第二步：遍历每个批次ID，查询记录并进行聚合计算
        for (String batchId : batchIds) {
            List<FinancialRecord> records = financialRecordService.findByBatch(batchId);

            if (records != null && !records.isEmpty()) {
                // --- 开始聚合计算 ---

                // 计算总金额
                double totalPrice = records.stream()
                        .mapToDouble(FinancialRecord::getPrice)
                        .sum();

                // 查找最大的变动 (changes)
                Long maxChanges = records.stream()
                        .map(FinancialRecord::getChanges)
                        .max(Comparator.naturalOrder())
                        .orElse(0L);

                // 查找最大的单价 (price)
                Double maxPrice = records.stream()
                        .map(FinancialRecord::getPrice)
                        .max(Comparator.naturalOrder())
                        .orElse(0.0);

                // 查找最晚的时间 (crTime)
                Optional<LocalDateTime> maxTimeOptional = records.stream()
                        .map(FinancialRecord::getCrTime)
                        .max(Comparator.naturalOrder());

                // --- 聚合计算结束 ---

                // 创建父级对象，用于存放聚合数据和子记录列表
                Map<String, Object> batchObject = new HashMap<>();

                // 从第一条记录中获取通用信息
                FinancialRecord firstRecord = records.get(0);
                batchObject.put("userId", firstRecord.getUserId());
                batchObject.put("make", firstRecord.getMake());

                // 填充其他聚合数据
                try {
                    batchObject.put("batchId", Long.parseLong(batchId));
                } catch (NumberFormatException e) {
                    batchObject.put("batchId", batchId); // 如果转换失败，则保留为字符串
                }
                batchObject.put("count", records.size());
                batchObject.put("totalPrice", totalPrice);
                // 格式化时间，如果不存在则为空字符串
                batchObject.put("time", maxTimeOptional.map(formatter::format).orElse(""));
                batchObject.put("maxChanges", maxChanges);
                batchObject.put("maxPrice", maxPrice);

                // 添加原始的子记录列表
                batchObject.put("records", records);

                // 将此批次的父级对象添加到最终列表中
                batchList.add(batchObject);
            }
        }

        if (batchList.isEmpty()) {
            return Result.error("未找到与该标记相关的数据！");
        }

        // 创建最终的返回数据结构
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("batch", batchList);

        return Result.success("查询成功", responseData);
    }
    //返回根据批次分组的数据
    @GetMapping("/find/batch/list")
    public Result<Map<String, Object>> findByBatchList(){
        Map<String, Object> l = financialRecordService.findAllBatchesWithDetails();
        return Result.success(l);
    }
}

package com.wzz.table.controller;

import cn.hutool.core.util.StrUtil;
import com.wzz.table.DTO.FinancialRecordDto;
import com.wzz.table.DTO.FinancialRecordListDto;
import com.wzz.table.DTO.Result;
import com.wzz.table.pojo.FinancialRecord;
import com.wzz.table.service.FinancialRecordService;
import com.wzz.table.utils.DateTimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger log = LogManager.getLogger(RootFinancialRecordController.class);
    @Autowired
    private FinancialRecordService financialRecordService;
    private final Object syncLock = new Object(); // 用于线程锁的对象

    //ids没传导致的Cannot parse null string  现已修改
    @PostMapping("/up")
    public Result<String> addList(@RequestBody FinancialRecordDto financialRecordDto) {
        synchronized (syncLock) { // 添加线程锁
            Long batchSize = financialRecordService.getNextBatchId(); // 获取下一个批次值

            for (FinancialRecordListDto item : financialRecordDto.getData()) {
                LocalDateTime crTime = null;
                FinancialRecord f = new FinancialRecord();

                // 检查 ids 是否为空，如果为空则设置默认值（例如 0）
                String idsStr = financialRecordDto.getIds();
                int ids = 0; // 默认值
                if (idsStr != null && !idsStr.isEmpty()) {
                    try {
                        ids = Integer.parseInt(idsStr);
                    } catch (NumberFormatException e) {
                        log.error("Invalid ids value: {}", idsStr);
                    }
                }
                f.setMake(financialRecordDto.getMake());
                f.setIds(ids);

                // 检查 orders 是否为空，如果为空则设置默认值（例如 0）
                String ordersStr = item.getOrders();
                int orders = 0; // 默认值
                if (ordersStr != null && !ordersStr.isEmpty()) {
                    try {
                        orders = Integer.parseInt(ordersStr);
                    } catch (NumberFormatException e) {
                        log.error("Invalid orders value: {}", ordersStr);
                    }
                }
                f.setOrders(orders);

                // 检查 quantity 是否为空，如果为空则设置默认值（例如 0）
                String quantityStr = item.getQuantity();
                int quantity = 0; // 默认值
                if (quantityStr != null && !quantityStr.isEmpty()) {
                    try {
                        quantity = Integer.parseInt(quantityStr);
                    } catch (NumberFormatException e) {
                        log.error("Invalid quantity value: {}", quantityStr);
                    }
                }
                f.setQuantity(quantity);

                // 检查 balance 是否为空，如果为空则设置默认值（例如 0）
                String balanceStr = item.getBalance();
                long balance = 0; // 默认值
                if (balanceStr != null && !balanceStr.isEmpty()) {
                    try {
                        balance = Long.valueOf(balanceStr);
                    } catch (NumberFormatException e) {
                        log.error("Invalid balance value: {}", balanceStr);
                    }
                }
                f.setBalance(balance);

                // 检查 lastBalance 是否为空，如果为空则设置默认值（例如 0）
                String lastBalanceStr = item.getLastBalance();
                long lastBalance = 0; // 默认值
                if (lastBalanceStr != null && !lastBalanceStr.isEmpty()) {
                    try {
                        lastBalance = Long.valueOf(lastBalanceStr);
                    } catch (NumberFormatException e) {
                        log.error("Invalid lastBalance value: {}", lastBalanceStr);
                    }
                }
                f.setLastBalance(lastBalance);

                // 检查 userId 是否为空，如果为空则设置默认值（例如 0）
                String userIdStr = item.getUserId();
                long userId = 0; // 默认值
                if (userIdStr != null && !userIdStr.isEmpty()) {
                    try {
                        userId = Long.valueOf(userIdStr);
                    } catch (NumberFormatException e) {
                        log.error("Invalid userId value: {}", userIdStr);
                    }
                }
                f.setUserId(userId);

                // 其他字段的处理逻辑...
                f.setChanges(Long.valueOf(item.getChanges()));
                f.setName(item.getName());
                f.setPrice(Double.valueOf(item.getPrice()));
                f.setBatch(batchSize);

                if (!StrUtil.hasBlank(item.getCrTime())) {
                    crTime = DateTimeUtil.parseDateTime(item.getCrTime());
                } else {
                    crTime = LocalDateTime.now();
                }
                f.setCrTime(crTime);

                Boolean is = financialRecordService.add(f);
                if (is) {
                    log.info("插入成功数据{}", f.toString());
                } else {
                    log.info("插入错误,数据{}", f.toString());
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
    //返回根据批次分组的数据
    @GetMapping("/find/batch/list")
    public Result<Map<String, Object>> findByBatchList(){
        Map<String, Object> l = financialRecordService.findAllBatchesWithDetails();
        return Result.success(l);
    }
    /**
     * 根据批次ID查询数据，并返回图片Base64
     * URL示例: /financial-record/find/batch/image/1001
     *
     * @param batchId 批次ID
     * @return 包含图片Base64字符串的Result对象
     */
    @GetMapping("/find/batch/image/{batchId}")
    public Result<?> findBatchAsImage(@PathVariable("batchId") Long batchId) {
        // 1. 根据 batchId 查询数据
        List<FinancialRecord> records = financialRecordService.findRecordsByBatchId(batchId);

        if (records == null || records.isEmpty()) {
            return Result.error("No data found for batchId: " + batchId); // 或者返回成功的空数据
        }

        // 2. 生成图片的Base64编码
        String base64Image = financialRecordService.generateRecordsImageBase64(records);

        // 3. 构建返回结果
        Map<String, String> response = new HashMap<>();
        response.put("imageBase64", base64Image);

        return Result.success(response);
    }
}

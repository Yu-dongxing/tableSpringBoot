package com.wzz.table.controller;

import cn.hutool.core.util.StrUtil;
import com.wzz.table.DTO.FinancialRecordDto;
import com.wzz.table.DTO.FinancialRecordListDto;
import com.wzz.table.DTO.Result;
import com.wzz.table.pojo.FinancialRecord;
import com.wzz.table.pojo.PointsUsers;
import com.wzz.table.service.FinancialRecordService;
import com.wzz.table.service.PointsUsersService;
import com.wzz.table.utils.DateTimeUtil;
import com.wzz.table.utils.OperationlogUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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

    @Autowired
    private PointsUsersService pointsUsersService;
    @Autowired
    private OperationlogUtil operationlogUtil;

    private final Object syncLock = new Object(); // 用于线程锁的对象

    //ids没传导致的Cannot parse null string  现已修改
    @PostMapping("/up")
    public Result<String> addList(@RequestBody FinancialRecordDto financialRecordDto) {
        log.info("<UNK>数据：：{}", financialRecordDto);
        synchronized (syncLock) { // 添加线程锁
            Long batchSize = financialRecordService.getNextBatchId(); // 获取下一个批次值

            for (FinancialRecordListDto item : financialRecordDto.getData()) {
                LocalDateTime crTime = null;
                FinancialRecord f = new FinancialRecord();

                f.setRemark(item.getRemark());

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
                        log.error("balance数值为: {}", balanceStr);
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

    /**
     * //增加积分----
     * @param user
     * @param point
     * @return
     */
    @PostMapping("/addpoint")
    public Result<?> addPoint (String user,long point) {
        PointsUsers p = pointsUsersService.findByUser(user);
        if (p == null) {
            PointsUsers pointsUsers =new PointsUsers();
            pointsUsers.setUser(user);
            pointsUsers.setPoints(point);
            Boolean is_add = pointsUsersService.add(pointsUsers);
            if (is_add) {
                return Result.success("增加用户 并且增加积分成功");
            }else {
                return Result.success("失败 未知原因");
            }

        }else {
            p.setPoints(p.getPoints() + point);
            Boolean is_update = pointsUsersService.update(p);
            if (is_update) {
                operationlogUtil.adminAdd(p.getUser(), point,"增加");
                return Result.success("积分增加成功！");
            }else {
                return Result.success("积分增加失败！");
            }

        }
    }

    /**
     * //减少积分------
     * @param user
     * @param point
     * @return
     */
    @PostMapping("/reducepoint")
    public Result<?> reducePoint (String user,long point) {
        PointsUsers p = pointsUsersService.findByUser(user);
        if (p == null) {
            return Result.success("用户不存在，无法减少积分");
        }else {
            if(p.getPoints()-point<0){
                return Result.error("积分减少失败，值为负数？");
            }
            p.setPoints(p.getPoints() - point);
            Boolean is_update = pointsUsersService.update(p);
            if (is_update) {
                operationlogUtil.adminAdd(p.getUser(), point,"减少");
                return Result.success("积分减少成功！");
            }else {
                return Result.success("积分减少失败！");
            }

        }
    }

    /**
     * //根据用户名查询积分 ----
     * @param username
     * @return
     */

    @GetMapping("/find/user")
    public Result<?> findByUser(String username){
        PointsUsers a = pointsUsersService.findByUser(username);
        if (a != null) {
            return Result.success(a);
        }else {
            return Result.error("查询错误！");
        }

    }
    /**
     * 根据用户标识（user）修改用户昵称（nickname）
     */
    @PostMapping("/point/user/update")
    public Result<?> updateByUserId(@RequestBody Map<String, String> map) {
        String user = map.get("user");
        String nickname = map.get("nickname");
        if (!StringUtils.hasText(user)) {
            return Result.error("更新失败，用户标识'user'不能为空！");
        }
        if (!StringUtils.hasText(nickname)) {
            return Result.error("更新失败，昵称'nickname'不能为空！");
        }
        PointsUsers p = pointsUsersService.findByUser(user);
        if (p == null) {
            return Result.error("该用户不存在！");
        }
        if (nickname.equals(p.getNickname())) {
            return Result.success("更新成功！昵称未发生变化。");
        }
        p.setNickname(nickname);
        try {
            boolean isUpdated = pointsUsersService.update(p);
            if (isUpdated) {
                return Result.success("更新成功！");
            } else {
                return Result.error("更新失败，请稍后重试。");
            }
        } catch (Exception e) {
            return Result.error("更新失败，服务器内部错误。");
        }
    }
    /**
     * 根据userId查询当前财务系统数据的数量
     */
    @GetMapping("/unm/{id}")
    public Result<?> findByUserIdNum(@PathVariable String id){
        Long unm = financialRecordService.countByUserId(Long.valueOf(id));
        if (unm == null) {
            return Result.error("查询错误！");
        }
        return Result.success("查询成功！",unm);
    }

    /**
     * 获取最大批次
     */
    @GetMapping("/max/batch")
    public Result<?> findByMaxBatch(){
        Long maxBatch = financialRecordService.getMaxBatch();
        if (maxBatch == null) {
            return Result.error("没有批次id");
        }
        return Result.success(maxBatch);
    }
}

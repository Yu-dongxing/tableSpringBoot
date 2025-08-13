package com.wzz.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wzz.table.DTO.BatchInfo;
import com.wzz.table.mapper.FinancialRecordMapper;
import com.wzz.table.pojo.FinancialRecord;
import com.wzz.table.service.FinancialRecordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinancialRecordServiceImpl implements FinancialRecordService {
    private static final Logger log = LogManager.getLogger(FinancialRecordServiceImpl.class);
    @Autowired
    private FinancialRecordMapper financialRecordMapper;
    @Override
    public Boolean add(FinancialRecord f) {
        int i = financialRecordMapper.insert(f);
        return i > 0;
    }

    @Override
    public List<FinancialRecord> findByBatch(String batchId) {
        return financialRecordMapper.selectList( new LambdaQueryWrapper<FinancialRecord>().eq(FinancialRecord::getBatch, batchId));
    }

    @Override
    public Long getMaxBatch() {
        LambdaQueryWrapper<FinancialRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(FinancialRecord::getBatch);
        List<Object> batchList = financialRecordMapper.selectObjs(queryWrapper);
        if (batchList.isEmpty()) {
            return null;
        }
        return batchList.stream().mapToLong(o -> Long.parseLong(o.toString())).max().getAsLong();
    }

    @Override
    public Long getNextBatchId() {
        Long currentMaxBatch = getMaxBatch(); // 获取当前最大批次值
        if (currentMaxBatch == null) {
            currentMaxBatch = 0L; // 如果没有批次值，从 0 开始
        }
        return currentMaxBatch + 1; // 返回下一个批次值
    }

    //根据mark查询batch
    @Override
    public List<String> findBatchIdsByMark(String mark) {
        LambdaQueryWrapper<FinancialRecord> queryWrapper = new LambdaQueryWrapper<>();
        // 查询条件：make 等于传入的 mark
        queryWrapper.eq(FinancialRecord::getMake, mark);
        // 选择查询的字段为 batch
        queryWrapper.select(FinancialRecord::getBatch);
        // 执行查询，获取 batch 列的值列表
        List<Object> batchList = financialRecordMapper.selectObjs(queryWrapper);
        // 将结果转换为 String 类型的列表返回
        return batchList.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean cleanupOldData() {
        // 获取当天 6 点的时间
        LocalDateTime sixAm = LocalDateTime.now().withHour(6).withMinute(0).withSecond(0).withNano(0);
        // 构建删除条件：创建时间早于 6 点
        LambdaUpdateWrapper<FinancialRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.lt(FinancialRecord::getCrTime, sixAm);
        // 执行删除操作
        int n = financialRecordMapper.delete(updateWrapper);
        return n > 0;
    }
    /**
     * 核心方法更新：查询所有批次，并统计每个批次的数据条数、总金额，
     * 并找出 changes 最大的记录所对应的 userId、make，以及批次内最大的 price 和最新的时间。
     */
    @Override
    public Map<String, Object> findAllBatchesWithDetails() {
        // 1. 一次性从数据库查询出所有记录
        List<FinancialRecord> allRecords = financialRecordMapper.selectList(null);
        if (allRecords == null || allRecords.isEmpty()) {
            return Collections.singletonMap("batch", Collections.emptyList());
        }

        // 2. 根据 batch 字段对所有记录进行分组
        Map<Long, List<FinancialRecord>> groupedByBatchId = allRecords.stream()
                .collect(Collectors.groupingBy(FinancialRecord::getBatch));

        // 3. 将分组后的 Map 转换为最终需要的包含批次详情的 List<BatchInfo>
        List<BatchInfo> batchInfoList = groupedByBatchId.entrySet().stream()
                .map(entry -> {
                    Long batchId = entry.getKey();
                    List<FinancialRecord> recordsInBatch = entry.getValue();

                    // 使用 BigDecimal 计算总金额，保证精度
                    BigDecimal totalPrice = recordsInBatch.stream()
                            .map(FinancialRecord::getPrice)
                            .filter(Objects::nonNull)
                            .map(String::valueOf)
                            .map(BigDecimal::new)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // 查找批次内 `changes` 值最大的记录
                    Optional<FinancialRecord> recordWithMaxChanges = recordsInBatch.stream()
                            .filter(r -> r.getChanges() != null)
                            .max(Comparator.comparing(FinancialRecord::getChanges));

                    // 从 `changes` 最大的记录中获取 userId, make 和 maxChanges
                    // 如果找不到，则提供默认值
                    Long userIdWithMaxChanges = recordWithMaxChanges.map(FinancialRecord::getUserId).orElse(0L);
                    String make = recordWithMaxChanges.map(FinancialRecord::getMake).orElse("0");
                    Long maxChanges = recordWithMaxChanges.map(FinancialRecord::getChanges).orElse(0L);

                    // 查找批次内最大的 `price`
                    Double maxPrice = recordsInBatch.stream()
                            .map(FinancialRecord::getPrice)
                            .filter(Objects::nonNull)
                            .mapToDouble(Double::doubleValue)
                            .max()
                            .orElse(0.0);

                    // 查找批次内最新的 `crTime`
                    LocalDateTime latestTime = recordsInBatch.stream()
                            .map(FinancialRecord::getCrTime)
                            .filter(Objects::nonNull)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    // 创建并返回包含所有所需信息的 BatchInfo 对象
                    return new BatchInfo(
                            batchId,
                            recordsInBatch.size(),
                            totalPrice.setScale(2, RoundingMode.HALF_UP).doubleValue(),
                            userIdWithMaxChanges,
                            make,
                            latestTime,
                            maxChanges,
                            maxPrice,
                            recordsInBatch
                    );
                })
                .sorted(Comparator.comparing(BatchInfo::getBatchId)) // 可选：按 batchId 排序
                .collect(Collectors.toList());

        // 4. 构建最终返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("batch", batchInfoList);
        return result;
    }

    /**
     * 新增方法：根据批次ID查询数据
     */
    @Override
    public List<FinancialRecord> findRecordsByBatchId(Long batchId) {
        // 使用 MyBatis Plus 的 QueryWrapper 根据 batchId 进行查询
        return financialRecordMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<FinancialRecord>()
                        .eq("batch", batchId)
        );
    }


    /**
     * 新增方法：生成图片Base64
     * (版本 4: 将a列和b列的数据文本颜色改为蓝色)
     */
    @Override
    public String generateRecordsImageBase64(List<FinancialRecord> records) {
        if (records == null || records.isEmpty()) {
            return ""; // 或者返回一个表示“无数据”的图片Base64
        }

        // --- 1. 定义图片和表格的尺寸、样式 ---
        int rowHeight = 30;
        int padding = 20;

        // 根据您代码中的注释，更新了表头，使其更具可读性
        String[] headers = {"a", "b", "c", "d", "e", "f", "g","h"};
        int[] columnWidths = {50, 60, 80, 80, 80, 150, 200,200};

        int tableWidth = 0;
        for (int width : columnWidths) {
            tableWidth += width;
        }

        int tableHeight = rowHeight * (records.size() + 1);
        int imageWidth = tableWidth + 2 * padding;
        int imageHeight = tableHeight + 2 * padding;

        Font headerFont = new Font("SimSun", Font.BOLD, 14);
        Font bodyFont = new Font("Arial", Font.PLAIN, 12);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // --- 2. 创建画布 ---
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imageWidth, imageHeight);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.translate(padding, padding);

        // --- 3. 绘制表头和数据行文字 ---
        g2d.setColor(Color.BLACK); // 设置默认颜色为黑色

        // 绘制表头
        g2d.setFont(headerFont);
        int currentX = 0;
        for (int i = 0; i < headers.length; i++) {
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(headers[i]);
            g2d.drawString(headers[i], currentX + (columnWidths[i] - textWidth) / 2, rowHeight - 10);
            currentX += columnWidths[i];
        }

        // 绘制数据行
        g2d.setFont(bodyFont);
        for (int i = 0; i < records.size(); i++) {
            FinancialRecord record = records.get(i);
            int currentY = rowHeight * (i + 1);
            currentX = 0;

            String[] rowData = {
                    //序号，操作员，件数，金额，单z,变动，变动前金额，当前金额
                    String.valueOf(i+1),//序号
                    String.valueOf(record.getUserId()),//操作员
                    String.valueOf(record.getQuantity()),//件数
                    record.getPrice() != null ? String.format("%.2f", record.getPrice()) : "N/A",//金额
                    String.valueOf(record.getOrders()),//订单数量
                    record.getChanges() != null ? String.valueOf(record.getChanges()) : "N/A",//变动
                    String.valueOf(record.getLastBalance()),//变动前金额
                    String.valueOf(record.getBalance())//当前金额
            };

            for (int j = 0; j < rowData.length; j++) {
                // ******************** 修改的核心部分 ********************
                // 如果是前两列（'a'列和'b'列），则将颜色设置为蓝色
                if (j == 0 || j == 1) {
                    g2d.setColor(Color.BLUE);
                } else {
                    // 其他列使用默认的黑色
                    g2d.setColor(Color.BLACK);
                }


                // ******************** 修改的核心部分 ********************
                // 计算文本宽度以实现居中
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(rowData[j]);
                int x = currentX + (columnWidths[j] - textWidth) / 2; // 计算居中后的x坐标

                // 使用计算好的x坐标绘制文本
                g2d.drawString(rowData[j], x, currentY + rowHeight - 10);
                // ******************************************************

                currentX += columnWidths[j];
            }
        }

        // --- 4. 绘制完整的表格网格线 ---
        g2d.setColor(Color.BLACK); // 确保网格线是黑色的
        int numRows = records.size() + 1;
        for (int i = 0; i <= numRows; i++) {
            int y = i * rowHeight;
            g2d.drawLine(0, y, tableWidth, y);
        }

        currentX = 0;
        g2d.drawLine(0, 0, 0, tableHeight);
        for (int width : columnWidths) {
            currentX += width;
            g2d.drawLine(currentX, 0, currentX, tableHeight);
        }

        g2d.dispose();

        // --- 5. 将图片转换为Base64字符串 ---
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            // 建议使用日志框架记录错误, 例如:
             log.error("生成图片Base64时出错", e);
//            e.printStackTrace();
            return ""; // 或者抛出自定义异常
        }
    }

}

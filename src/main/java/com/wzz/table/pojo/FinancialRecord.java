package com.wzz.table.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("financial_record")
public class FinancialRecord {
    // id
    @TableId(type = IdType.AUTO)
    private int id;
    // 序号 get
    @TableField("ids")
    private int ids;
    // 批次
    @TableField("batch")
    private Long batch;
    //标记
    @TableField("make")
    private String make;
    // 操作员昵称
    @TableField("name")
    private String name;
    // 件数
    @TableField("quantity")
    private int quantity;
    // 金额
    @TableField("price")
    private Double price;
    // 订单数量
    @TableField("orders")
    private int orders;
    // 金额变动
    @TableField("changes")
    private Long changes;
    // 变动前金额
    @TableField("lastBalance")
    private Long lastBalance;
    // 当前金额
    @TableField("balance")
    private Long balance;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("cr_time")
    private LocalDateTime crTime;
}


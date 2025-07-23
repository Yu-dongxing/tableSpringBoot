package com.wzz.table.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FinancialRecordListDto {
    // 操作员昵称
    private String name;
    //操作用户id
    private Long userId;
    // 件数
    private int quantity;
    // 金额
    private Double price;
    // 订单数量
    private int orders;
    // 金额变动
    private Long changes;
    // 变动前金额
    private Long lastBalance;
    // 当前金额
    private Long balance;
    // 创建时间
    private String crTime;
}

package com.wzz.table.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FinancialRecordListDto {
    // 操作员昵称
    private String name;
    //操作用户id
    private String userId;
    // 件数
    private String quantity;
    // 金额
    private String price;
    // 订单数量
    private String orders;
    // 金额变动
    private String changes;
    // 变动前金额
    private String lastBalance;
    // 当前金额
    private String balance;
    // 创建时间
    private String crTime;
}

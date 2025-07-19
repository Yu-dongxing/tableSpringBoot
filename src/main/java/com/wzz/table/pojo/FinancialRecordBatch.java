package com.wzz.table.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("financial_record_batch")
public class FinancialRecordBatch {
    // id
    @TableId(type = IdType.AUTO)
    private int id;

    //
    @TableField("financial_record_id")
    private int FinancialRecordId ;
}

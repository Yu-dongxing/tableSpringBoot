package com.wzz.table.DTO;

import lombok.Data;

import java.util.List;

@Data
public class FinancialRecordDto {
    // 字段 make 用于存储 JSON 中的 make 值
    private String make;
    private int ids;

    // 字段 data 是一个二维数组，使用 List<List<Detail>> 表示
    private List<FinancialRecordListDto> data;
}

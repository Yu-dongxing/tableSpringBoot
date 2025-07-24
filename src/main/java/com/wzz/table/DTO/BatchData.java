package com.wzz.table.DTO;

import com.wzz.table.pojo.FinancialRecord;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor // Lombok annotation for a constructor with all fields
public class BatchData {
    private Long batchId;
    private List<FinancialRecord> records;
}

package com.wzz.table.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class OperationlogSelectDto {
    //管理员
    private String adminUser;
    //积分用户
    private String pointsUser;
    //操作类型
    private String openLs;
    //开始时间
    private String startTime;
    //结束时间
    private String endTime;
    //页码
    @JsonProperty(defaultValue = "1") // 默认值为1
    private int page;
    //内容大小
    @JsonProperty(defaultValue = "10") // 默认值为10
    private int size;
}

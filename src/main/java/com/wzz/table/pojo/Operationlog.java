package com.wzz.table.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class Operationlog {
    //id
    @TableId(type = IdType.AUTO)
    private Long id;

    //管理员
    @TableField("admin_user")
    private String adminUser;

    //用户
    @TableField("points_user")
    private String pointsUser;
    //操作类型
    @TableField("open_ls")
    private String openLs;
    //操作数值
    @TableField("change_num")
    private Long changeNum;

    //操作后数值
    @TableField("points")
    private Long points;

    //操作时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("cr_time")
    private LocalDateTime crTime;

}

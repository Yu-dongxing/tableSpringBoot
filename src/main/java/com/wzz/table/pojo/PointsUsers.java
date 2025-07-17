package com.wzz.table.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("points_users")
public class PointsUsers {
    //id
    @TableId(type = IdType.AUTO)
    private Long id;
    //用户名
    @TableField("user")
    private String user;
    //昵称
    @TableField("nickname")
    private String nickname;
    //积分
    @TableField("points")
    private long points;

}

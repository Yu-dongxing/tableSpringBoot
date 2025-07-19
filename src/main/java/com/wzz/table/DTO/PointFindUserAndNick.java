package com.wzz.table.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class PointFindUserAndNick {
    //用户名
    private String user;
    //昵称
    private String nickname;
}

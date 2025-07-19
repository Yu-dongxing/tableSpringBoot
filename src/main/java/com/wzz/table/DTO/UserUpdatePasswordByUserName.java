package com.wzz.table.DTO;

import lombok.Data;

@Data
public class UserUpdatePasswordByUserName {
    private String userName;
    private String oldPassword;
    private String newPassword;
}

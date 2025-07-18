package com.wzz.table.DTO;

import lombok.Data;

@Data
public class UserUpdatePasswordDto {
    private String oldPassword;

    private String newPassword;
}

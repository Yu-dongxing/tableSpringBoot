package com.wzz.table.auth;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 实际项目中根据业务逻辑查询权限，此处为示例
        List<String> permissionList = new ArrayList<>();
        permissionList.add("user.add");
        permissionList.add("user.update");
        return permissionList;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 实际项目中根据业务逻辑查询角色，此处为示例
        List<String> roleList = new ArrayList<>();
        roleList.add("admin");
        roleList.add("super-admin");
        return roleList;
    }
}

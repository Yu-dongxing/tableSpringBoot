package com.wzz.table.auth;

import cn.dev33.satoken.stp.StpInterface;
import com.wzz.table.pojo.User;
import com.wzz.table.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {
    private static final Logger log = LogManager.getLogger(StpInterfaceImpl.class);
    @Autowired
    private UserService userService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        List<String> list = new ArrayList<String>();
        list.add("101");
        list.add("user.add");
        list.add("user.update");
        list.add("user.get");
        // list.add("user.delete");
        list.add("art.*");
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        Long longId = null;
        if (loginId == null) {
            // 处理 loginId 为 null 的情况
            System.err.println("loginId 是 null，无法转换为 Long");
        } else {
            try {
                // 尝试将 loginId 转换为 Long 类型
                longId = Long.parseLong(String.valueOf(loginId));
            } catch (NumberFormatException e) {
                // 如果转换失败，处理异常
                System.err.println("loginId 转换为 Long 失败: " + loginId);
                // 可以选择抛出异常或返回默认值，根据业务需求决定
                return new ArrayList<String>();
            }
        }
        User u = userService.findById(longId);
        if (u == null) {
            log.info("没有用户");
            return null;
        }else {
            log.info("需要角色："+u.toString());
            List<String> list = new ArrayList<String>();
            list.add(String.valueOf(u.getRole()));
            return list;
        }

    }
}

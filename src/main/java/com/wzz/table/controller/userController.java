package com.wzz.table.controller;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.wzz.table.DTO.Result;
import com.wzz.table.pojo.User;
import com.wzz.table.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class userController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<String> login(String username, String password) {
        User loginUser = userService.findByUsername(username);
        if (loginUser == null) {
            return Result.error("用户名或者密码错误！");
        }else {
            if (loginUser.getPassword().equals(password)) {
                //jwt模式有效
                StpUtil.login(loginUser.getId(), SaLoginConfig
                        .setExtra("roleId",loginUser.getRole())
                        .setExtra("userId",loginUser.getId())
                        .setExtra("username",loginUser.getUsername())
                        .setIsWriteHeader(true)
                );
                SaTokenInfo a = StpUtil.getTokenInfo();
                return Result.success("登陆成功，返回token",a.tokenValue);
            }else {
                return Result.error("密码错误，请重新输入！");
            }
        }
    }
    @PostMapping("/sign")
    public Result<String> sign(@RequestBody User user) {
        user.setRole(1);//默认为管理员角色
        User loginUser = userService.findByUsername(user.getUsername());
        if (loginUser == null) {

            Boolean isSign = userService.sign(user);
            if (isSign) {
                User loginU = userService.findByUsername(user.getUsername());
                StpUtil.login(loginU.getId(), SaLoginConfig
                        .setExtra("roleId",loginU.getRole())
                        .setExtra("userId",loginU.getId())
                        .setExtra("username",loginU.getUsername())
                );
                SaTokenInfo a = StpUtil.getTokenInfo();
                return Result.success("注册并且登陆成功！返回token",a.tokenValue);
            }else {
                return Result.error("注册失败！");
            }
        }else {
            return Result.error("用户已存在！");
        }
    }
    //更新密码
    @PostMapping("/repas")
    public Result<String> rePassword(String oldPassword, String newPassword) {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.findById(userId);
        if (user != null) {
            if (oldPassword.equals(user.getPassword())) {
                user.setPassword(newPassword);
                Boolean is = userService.rePassword(user);
                if (is) {
                    return Result.success("更新密码成功！");
                }else {
                    return Result.error("更新密码失败！");
                }
            }else {
                return Result.error("密码不一致，请重新输入！");
            }
        }else {
            return Result.error("查询用户错误，token传递错误！");
        }

    }
    //删除管理用户
    @PostMapping("/delete")
    public Result<String> deleteByUser(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            Boolean is_d = userService.deleteByid(user);
            if (is_d) {
                return Result.success("删除成功！");
            }else {
                return Result.error("删除失败！");
            }
        }else {
            return Result.error("查询不到用户，删除失败！");
        }
    }
}

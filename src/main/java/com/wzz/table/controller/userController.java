package com.wzz.table.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.Hutool;
import com.wzz.table.DTO.Result;
import com.wzz.table.DTO.UserLoginDto;
import com.wzz.table.DTO.UserUpdatePasswordByUserName;
import com.wzz.table.DTO.UserUpdatePasswordDto;
import com.wzz.table.pojo.User;
import com.wzz.table.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//管理员登陆
@RestController
@RequestMapping("/api/user")
public class userController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody UserLoginDto userLoginDto) {
        User loginUser = userService.findByUsername(userLoginDto.getUsername());
        if (loginUser == null) {
            return Result.error("用户名或者密码错误！");
        }else {
            if (loginUser.getPassword().equals(userLoginDto.getPassword())) {
                //jwt模式有效
                StpUtil.login(loginUser.getId(), SaLoginConfig
                        .setExtra("roleId",loginUser.getRole())
                        .setExtra("userId",loginUser.getId())
                        .setExtra("username",loginUser.getUsername())
                        .setIsWriteHeader(true)
                );
                SaTokenInfo a = StpUtil.getTokenInfo();
                // 构造 Map
                Map<String, Object> userInfoMap = new HashMap<>();
                String roleName = "";
                if(loginUser.getRole() == 0){
                    roleName = "Boss";
                }else {
                    roleName = "Admin";
                }
                userInfoMap.put("userName",loginUser.getUsername());
                userInfoMap.put("roleName", roleName);
                userInfoMap.put("roleId", loginUser.getRole());
                userInfoMap.put("token", a.getTokenValue());
                return Result.success("登陆成功，返回角色和token",userInfoMap);
            }else {
                return Result.error("密码错误，请重新输入！");
            }
        }
    }

    @SaCheckRole("0")
    @PostMapping("/sign")
    public Result<String> sign(@RequestBody UserLoginDto userLoginDto) {
        User u = new User();
        u.setRole(1);//默认为管理员角色
        if (userLoginDto.getPassword() != null) {
            u.setPassword(userLoginDto.getPassword());
        }else {
            u.setPassword("123456789");
        }
        u.setUsername(userLoginDto.getUsername());
        User loginUser = userService.findByUsername(userLoginDto.getUsername());
        if (loginUser == null) {

            Boolean isSign = userService.sign(u);
            if (isSign) {
                return Result.success("注册成功！");
            }else {
                return Result.error("注册失败！");
            }
        }else {
            return Result.error("用户已存在！");
        }
    }
    //更新密码
    @PostMapping("/repas")
    public Result<String> rePassword(@RequestBody UserUpdatePasswordDto userUpdatePasswordDto) {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.findById(userId);
        if (user != null) {
            if (userUpdatePasswordDto.getOldPassword().equals(user.getPassword())) {
                user.setPassword(userUpdatePasswordDto.getNewPassword());
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
    @SaCheckRole("0")
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
    //退出登陆
    @GetMapping("/logout")
    public Result<String> logOut(){
        StpUtil.logout();
        return Result.success("退出登陆成功，请删除token和cookie！");
    }
    @SaCheckRole("0")
    //根据用户id查询用户信息
    @GetMapping("/find/userid")
    public Result<User> findByUserId(@RequestBody Long userId){
        User u = userService.findById(userId);
        return Result.success("查询成功",u);
    }
    @SaCheckRole("0")
    //查询所有管理用户
    @GetMapping("/find/all")
    public Result<List<User>> findAll(){
        List<User> ls = userService.findAll();
        if(ls!=null){
            return Result.success("查询所有管理用户成功",ls);
        }else {
            return Result.error("查询所有管理用户错误，没有找到数据");
        }

    }
    //根据用户名查询管理用户
    @SaCheckRole("0")
    @GetMapping("/find/username")
    public Result<User> findByUserName(String name){
        User u = userService.findByUsername(name);
        if(u!=null){
            return Result.success("查询成功！",u);
        }else {
            return Result.error("查询失败！");
        }

    }
    //根据用户名更新密码
    @PostMapping("/repas/username")
    @SaCheckRole("0")
    public Result<String> updatePasswordByuserName(@RequestBody UserUpdatePasswordByUserName userUpdatePasswordByUserName){
        User user = userService.findByUsername(userUpdatePasswordByUserName.getUserName());
        if (user != null) {
            if (userUpdatePasswordByUserName.getOldPassword().equals(user.getPassword())) {
                user.setPassword(userUpdatePasswordByUserName.getNewPassword());
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
            return Result.error("查询用户错误或者该用户不存在！");
        }
    }
}

package com.wzz.table.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.wzz.table.pojo.Operationlog;
import com.wzz.table.pojo.PointsUsers;
import com.wzz.table.service.OperationlogService;
import com.wzz.table.service.PointsUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OperationlogUtil {
    @Autowired
    private OperationlogService operationlogService;
    @Autowired
    private PointsUsersService pointsUsersService;
    //user points_user  change       points           time
    //管理员  用户          操作数值      操作后数值   操作时间
    //操作类型
    public void add(
            String points_user,
            Long change,
            String czlx
    ) {
        Operationlog operationlog = new Operationlog();
        operationlog.setCrTime(LocalDateTime.now());
        String userNa = StpUtil.getExtra("username").toString();
        operationlog.setAdminUser(userNa);
        operationlog.setChangeNum(change);
        operationlog.setOpenLs(czlx);
        PointsUsers us = pointsUsersService.findByUser(points_user);
        if (us == null) {
            System.out.println("没有该用户");
            operationlog.setPointsUser("访客");
        }else {
            operationlog.setPoints(us.getPoints());
            operationlog.setPointsUser(us.getUser());
        }
        Boolean is_add = operationlogService.add(operationlog);
        if (is_add) {
            System.out.println("操作日志添加成功！");
        }else {
            System.out.println("操作日志添加失败？");
        }
    }
    public void del(
            String points_user,
            String ope
    ){
        Operationlog operationlog = new Operationlog();
        operationlog.setCrTime(LocalDateTime.now());
        String userNa = StpUtil.getExtra("username").toString();
        operationlog.setAdminUser(userNa);
        operationlog.setChangeNum(0L);
        operationlog.setOpenLs("删除积分用户");
        PointsUsers us = pointsUsersService.findByUser(points_user);
        if (us == null) {
            System.out.println("没有该用户");
            operationlog.setPointsUser(points_user);
            operationlog.setPoints(0L);
        }else {
            operationlog.setPoints(us.getPoints());
            operationlog.setPointsUser(us.getUser());
        }
        Boolean is_add = operationlogService.add(operationlog);
        if (is_add) {
            System.out.println("操作日志添加成功！");
        }else {
            System.out.println("操作日志添加失败？");
        }
    }
}

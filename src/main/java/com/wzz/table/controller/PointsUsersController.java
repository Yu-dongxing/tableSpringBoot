package com.wzz.table.controller;


import com.wzz.table.DTO.Result;
import com.wzz.table.pojo.PointsUsers;
import com.wzz.table.service.PointsUsersService;
import com.wzz.table.utils.OperationlogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//积分管理接口
@RestController
@RequestMapping("/api/points")
public class PointsUsersController {
    @Autowired
    private PointsUsersService pointsUsersService;
    @Autowired
    private OperationlogUtil operationlogUtil;
    //积分添加，更新
    @PostMapping("/add")
    public Result<String> add(@RequestBody PointsUsers pointsUsers) {
        PointsUsers p = pointsUsersService.findByUser(pointsUsers.getUser());
        if (p == null) {
            Boolean is_add = pointsUsersService.add(pointsUsers);
            if (is_add) {
                operationlogUtil.add(pointsUsers.getUser(), pointsUsers.getPoints());
                return Result.success("添加积分用户成功！");
            }else {
                return Result.success("添加积分用户失败！");
            }
        }else {
            p.setPoints(pointsUsers.getPoints());
            p.setNickname(pointsUsers.getNickname());
            Boolean is_update = pointsUsersService.update(p);
            if (is_update) {
                operationlogUtil.add(p.getUser(), pointsUsers.getPoints());
                return Result.success("积分用户更新成功");
            }else {
                return Result.success("积分用户更新失败");
            }
        }
    }
    //增加积分
    @PostMapping("/addpoint")
    public Result<String> addPoint (String user,long point) {
        PointsUsers p = pointsUsersService.findByUser(user);
        if (p == null) {
            return Result.success("用户不存在，无法增加积分");
        }else {

            p.setPoints(p.getPoints() + point);
            Boolean is_update = pointsUsersService.update(p);
            if (is_update) {
                operationlogUtil.add(p.getUser(), point);
                return Result.success("积分增加成功！");
            }else {
                return Result.success("积分增加失败！");
            }

        }
    }
    //减少积分
    @PostMapping("/reducepoint")
    public Result<String> reducePoint (String user,long point) {
        PointsUsers p = pointsUsersService.findByUser(user);
        if (p == null) {
            return Result.success("用户不存在，无法减少积分");
        }else {
            if(p.getPoints()-point<0){
                return Result.error("积分减少失败，值为负数？");
            }
            p.setPoints(p.getPoints() - point);
            Boolean is_update = pointsUsersService.update(p);
            if (is_update) {
                operationlogUtil.add(p.getUser(), point);
                return Result.success("积分减少成功！");
            }else {
                return Result.success("积分减少失败！");
            }

        }
    }
    //查询所有积分用户信息
    @GetMapping("/all")
    public Result<List<PointsUsers>> getAll() {
        List<PointsUsers> ls = pointsUsersService.findAll();
        return Result.success(ls);
    }
    //根据用户删除积分
    @PostMapping("/delete")
    public Result<String> deleteByUser(String username){
        Boolean is_de = pointsUsersService.deleteByUser(username);
        if (is_de) {
            return Result.success("该用户删除成功");
        }else {
            return Result.success("该用户删除失败");
        }
    }
}

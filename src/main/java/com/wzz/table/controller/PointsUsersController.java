package com.wzz.table.controller;


import com.wzz.table.DTO.PointFindUserAndNick;
import com.wzz.table.DTO.Result;
import com.wzz.table.pojo.Operationlog;
import com.wzz.table.pojo.PointsUsers;
import com.wzz.table.service.FinancialRecordService;
import com.wzz.table.service.PointsUsersService;
import com.wzz.table.utils.OperationlogUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//积分管理接口
@RestController
@RequestMapping("/api/points")
public class PointsUsersController {
    private static final Logger log = LogManager.getLogger(PointsUsersController.class);
    @Autowired
    private PointsUsersService pointsUsersService;
    @Autowired
    private OperationlogUtil operationlogUtil;
    @Autowired
    private FinancialRecordService financialRecordService;
    //积分添加，更新
    @PostMapping("/add")
    public Result<String> add(@RequestBody PointsUsers pointsUsers) {
        PointsUsers p = pointsUsersService.findByUser(pointsUsers.getUser());
        if (p == null) {
            Boolean is_add = pointsUsersService.add(pointsUsers);
            if (is_add) {
                operationlogUtil.add(pointsUsers.getUser(), pointsUsers.getPoints(),"添加");
                return Result.success("添加积分用户成功！");
            }else {
                return Result.success("添加积分用户失败！");
            }
        }else {
            p.setPoints(pointsUsers.getPoints());
            if (pointsUsers.getNickname() != null && !"".equals(pointsUsers.getNickname())) {
                p.setNickname(pointsUsers.getNickname());
            }
            Boolean is_update = pointsUsersService.update(p);

            if (is_update) {
                operationlogUtil.add(p.getUser(), pointsUsers.getPoints(),"重置");
                return Result.success("积分用户的积分重置成功");
            }else {
                return Result.success("积分用户的积分重置失败");
            }
        }
    }
    //添加积分用户
    @PostMapping("/add/user")
    public Result<String> addPointUser(@RequestBody PointsUsers pointsUsers){
        PointsUsers p = pointsUsersService.findByUser(pointsUsers.getUser());
        if (p == null) {
            Boolean is_add = pointsUsersService.add(pointsUsers);
            if (is_add) {
                operationlogUtil.add(pointsUsers.getUser(), pointsUsers.getPoints(),"添加");
                return Result.success("添加积分用户成功！");
            }else {
                return Result.success("添加积分用户失败！");
            }
        }else {
            return Result.error("该用户已存在！");
        }
    }
    //增加积分----
    @PostMapping("/addpoint")
    public Result<String> addPoint (String user,long point) {
        PointsUsers p = pointsUsersService.findByUser(user);
        if (p == null) {
            PointsUsers pointsUsers =new PointsUsers();
            pointsUsers.setUser(user);
            pointsUsers.setPoints(point);
            Boolean is_add = pointsUsersService.add(pointsUsers);
            if (is_add) {
                return Result.success("增加用户 并且增加积分成功");
            }else {
                return Result.success("失败 未知原因");
            }

        }else {
            p.setPoints(p.getPoints() + point);
            Boolean is_update = pointsUsersService.update(p);
            if (is_update) {
                operationlogUtil.add(p.getUser(), point,"增加");
                return Result.success("积分增加成功！");
            }else {
                return Result.success("积分增加失败！");
            }

        }
    }
    //减少积分------
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
                operationlogUtil.add(p.getUser(), point,"减少");
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
        if (ls == null) {
            return Result.error("查询失败！");
        }
        return Result.success(ls);
    }
    //根据用户删除积分
    @PostMapping("/delete")
    public Result<String> deleteByUser(String username){
        Boolean is_de = pointsUsersService.deleteByUser(username);
        if (is_de) {
            operationlogUtil.del(username, "删除");
            return Result.success("该用户删除成功");
        }else {
            return Result.success("该用户删除失败");
        }
    }
    //根据用户名查询积分 ----
    @GetMapping("/find/user")
    public Result<PointsUsers> findByUser(String username){
        PointsUsers a = pointsUsersService.findByUser(username);
        if (a != null) {
            return Result.success(a);
        }else {
            return Result.error("查询错误！");
        }

    }
    //根据昵称查询用户信息
    @GetMapping("/find/nickname")
    public Result<PointsUsers> findByNickName(String nickname){
        PointsUsers  u = pointsUsersService.findByNickName(nickname);
        if (u == null) {
            return Result.error("查询错误！");
        }
        return Result.success("根据昵称查询用户信息",u);
    }
    //根据用户名或者昵称查询用户信息
    @PostMapping("/find/userandnick")
    public Result<PointsUsers> findByUserAndNick(@RequestBody PointFindUserAndNick pointFindUserAndNick){
        if(pointFindUserAndNick.getNickname()!=null && pointFindUserAndNick.getUser()!=null){
            PointsUsers u = pointsUsersService.findByUserAndBick(pointFindUserAndNick);
            return Result.success("据用户名或者昵称查询用户信息查询成功！",u);
        }else {
            return Result.error("查询条件不能为空！");
        }


    }
    /**
     *删除所有积分用户
     */
    @GetMapping("/delete/all")
    public Result<?> deleteAll(){
        log.info("开始删除积分用户和财务系统的数据》》》");
        Boolean is =  pointsUsersService.deleteAll();
        Boolean is_f = financialRecordService.cleanupAllData();
        if (is) {
            if (is_f) {
                return Result.success("删除所有积分用户和财务数据成功！");
            }
            return Result.success("删除所有积分用户成功！");
        }
        log.info("\"<删除所有积分用户失败>\"");
        return Result.error("<删除所有积分用户失败>");
    }
}

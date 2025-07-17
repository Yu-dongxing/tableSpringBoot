package com.wzz.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.wzz.table.mapper.PointsUsersMapper;
import com.wzz.table.pojo.PointsUsers;
import com.wzz.table.service.PointsUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PointsUsersServiceImpl implements PointsUsersService {
    @Autowired
    private PointsUsersMapper pointsUsersMapper;
    @Override
    public PointsUsers findByUser(String user) {
        return pointsUsersMapper.selectOne(
                new LambdaQueryWrapper<PointsUsers>()
                        .eq(PointsUsers::getUser,user));
    }

    @Override
    public Boolean add(PointsUsers p) {
        int i = pointsUsersMapper.insert(p);
        if(i>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Boolean update(PointsUsers p) {
        int i = pointsUsersMapper.updateById(p);
        if(i>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public List<PointsUsers> findAll() {
        List<PointsUsers> li = pointsUsersMapper.selectList(null);
        return li;
    }

    @Override
    public Boolean deleteByUser(String username) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", username);
        int result = pointsUsersMapper.deleteByMap(map);
        if(result>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public PointsUsers findByUserName(String userNa) {
        return null;
    }
}


//new LambdaQueryWrapper<User>()
//                        .eq(User::getUsername, username)
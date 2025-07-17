package com.wzz.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wzz.table.mapper.UserMapper;
import com.wzz.table.pojo.User;
import com.wzz.table.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean sign(User user) {
        int is = userMapper.insert(user);
        if(is>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void login(String username, String password) {

    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username) // 根据用户名查询
        );
    }

    @Override
    public User findById(long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public Boolean rePassword(User user) {
        int is = userMapper.updateById(user);
        if(is>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Boolean deleteByid(User user) {
        int is = userMapper.deleteById(user.getId());
        if(is>0){
            return true;
        }else {
            return false;
        }
    }

}

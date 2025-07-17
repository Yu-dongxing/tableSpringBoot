package com.wzz.table.service;

import com.wzz.table.pojo.User;
import org.springframework.stereotype.Service;

public interface UserService {
    Boolean sign(User user);

    void login(String username, String password);

    User findByUsername(String username);

    User findById(long userId);

    Boolean rePassword(User user);

    Boolean deleteByid(User user);
}

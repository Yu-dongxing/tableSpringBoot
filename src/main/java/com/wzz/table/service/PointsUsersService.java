package com.wzz.table.service;

import com.wzz.table.pojo.PointsUsers;

import java.util.List;

public interface PointsUsersService {
    PointsUsers findByUser(String user);

    Boolean add(PointsUsers p);

    Boolean update(PointsUsers p);

    List<PointsUsers> findAll();

    Boolean deleteByUser(String username);

    PointsUsers findByUserName(String userNa);
}

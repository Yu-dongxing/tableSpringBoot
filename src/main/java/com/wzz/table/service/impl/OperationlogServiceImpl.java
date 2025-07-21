package com.wzz.table.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzz.table.DTO.OperationlogSelectDto;
import com.wzz.table.mapper.OperationlogMapper;
import com.wzz.table.pojo.Operationlog;
import com.wzz.table.service.OperationlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperationlogServiceImpl implements OperationlogService {
    @Autowired
    private OperationlogMapper operationlogMapper;

    @Override
    public List<Operationlog> finAll() {
        List<Operationlog> l = operationlogMapper.selectList(null);
        return l;
    }

    @Override
    public Boolean add(Operationlog operationlog) {
        int i = operationlogMapper.insert(operationlog);
        if(i>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public IPage<Operationlog> findPage(int page, int pageSize) {
        return operationlogMapper.selectPage(new Page<>(page,pageSize),null);
    }

    @Override
    public IPage<Operationlog> findPageBytime(int page, int pageSize, LocalDateTime startTime, LocalDateTime endTime) {
        // 创建分页对象
        Page<Operationlog> pageObj = new Page<>(page, pageSize);
        // 创建查询条件
        LambdaQueryWrapper<Operationlog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(startTime != null, Operationlog::getCrTime, startTime);
        queryWrapper.le(endTime != null, Operationlog::getCrTime, endTime);
        // 执行查询
        return operationlogMapper.selectPage(pageObj, queryWrapper);
    }

    ////根据 操作用户+积分用户+时间查询+操作类型（增加或者减少）（4个条件有任意都能查询 比如操作用户可查询 操作用户+积分用户可查询 以此类推
    @Override
    public IPage<Operationlog> findByTimeOrUserOrPointUserOrOpenLsPage(OperationlogSelectDto operationlogSelectDto) {
        // 创建分页对象
        Page<Operationlog> pageObj = new Page<>(operationlogSelectDto.getPage(), operationlogSelectDto.getSize());
        // 创建查询条件
        LambdaQueryWrapper<Operationlog> queryWrapper = new LambdaQueryWrapper<>();

        // 判断条件是否存在，存在则添加到查询条件中
        if (StrUtil.isNotBlank(operationlogSelectDto.getAdminUser())) {
            queryWrapper.eq(Operationlog::getAdminUser, operationlogSelectDto.getAdminUser());
        }
        if (StrUtil.isNotBlank(operationlogSelectDto.getPointsUser())) {
            queryWrapper.eq(Operationlog::getPointsUser, operationlogSelectDto.getPointsUser());
        }
        if (operationlogSelectDto.getOpenLs() != null) {
            queryWrapper.eq(Operationlog::getOpenLs, operationlogSelectDto.getOpenLs());
        }
        if (operationlogSelectDto.getStartTime() != null && operationlogSelectDto.getEndTime() != null) {
            queryWrapper.between(Operationlog::getCrTime, operationlogSelectDto.getStartTime(), operationlogSelectDto.getEndTime());
        }


        // 执行查询
        return operationlogMapper.selectPage(pageObj, queryWrapper);
    }
}

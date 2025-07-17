package com.wzz.table.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzz.table.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

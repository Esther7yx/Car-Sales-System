package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carsale.entity.SystemUser;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户表Mapper接口
 * 继承MyBatis Plus的BaseMapper，提供基础的CRUD操作
 */
public interface SystemUserMapper extends BaseMapper<SystemUser> {
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    SystemUser selectByUsername(@Param("username") String username);
}
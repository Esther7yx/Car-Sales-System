package com.carsale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carsale.entity.SystemUser;

/**
 * 系统用户表Service接口
 * 继承MyBatis Plus的IService，提供基础的业务逻辑操作
 */
public interface SystemUserService extends IService<SystemUser> {
    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 验证通过的用户信息，验证失败返回null
     */
    SystemUser login(String username, String password);
}
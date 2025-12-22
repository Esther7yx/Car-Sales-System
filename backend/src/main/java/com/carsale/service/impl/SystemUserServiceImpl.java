package com.carsale.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carsale.entity.SystemUser;
import com.carsale.mapper.SystemUserMapper;
import com.carsale.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统用户表Service实现类
 * 继承MyBatis Plus的ServiceImpl，实现具体的业务逻辑
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    @Autowired
    private SystemUserMapper systemUserMapper;

    @Override
    public SystemUser login(String username, String password) {
        // 根据用户名查询用户信息
        SystemUser user = systemUserMapper.selectByUsername(username);
        
        // 验证用户是否存在且密码正确
        if (user != null && user.getPassword().equals(password)) {
            // 这里可以添加密码加密验证逻辑
            // 检查用户状态
            if ("active".equals(user.getStatus())) {
                return user;
            }
        }
        
        return null;
    }
}
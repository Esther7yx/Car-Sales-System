package com.carsale.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carsale.entity.SystemUser;
import com.carsale.mapper.SystemUserMapper;
import com.carsale.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 系统用户表Service实现类
 * 继承MyBatis Plus的ServiceImpl，实现具体的业务逻辑
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    @Autowired
    private SystemUserMapper systemUserMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SystemUser login(String username, String password) {
        // 根据用户名查询用户信息
        SystemUser user = systemUserMapper.selectByUsername(username);
        
        if (user != null && user.getPassword() != null) {
            // 直接比对明文密码
            if (password.equals(user.getPassword())) {
                if ("active".equals(user.getStatus())) {
                    return user;
                }
            }
        }
        
        return null;
    }
}
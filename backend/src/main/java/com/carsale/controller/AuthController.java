package com.carsale.controller;

import com.carsale.entity.SystemUser;
import com.carsale.service.SystemUserService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * 认证控制器
 * 修复后：类路径添加 /api，与前端请求的 /api/login 保持一致
 */
@RestController
@RequestMapping("/api") // <--- 关键修改：必须加上这个，否则报 404
public class AuthController {
    @Autowired
    private SystemUserService systemUserService;

    /**
     * 用户登录
     * 最终接口地址：POST /api/login
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 验证参数
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return Result.error(400, "用户名和密码不能为空");
        }

        // 调用登录服务
        SystemUser user = systemUserService.login(username, password);

        if (user != null) {
            // 登录成功，生成临时token
            String token = "temp_token_" + System.currentTimeMillis();

            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            systemUserService.updateById(user);

            // 构造返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);

            // 构造用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("role", user.getRole());
            userInfo.put("status", user.getStatus());

            data.put("userInfo", userInfo);

            return Result.success(data);
        } else {
            return Result.error(401, "用户名或密码错误");
        }
    }
}
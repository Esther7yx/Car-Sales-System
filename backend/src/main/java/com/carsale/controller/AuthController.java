package com.carsale.controller;

import com.carsale.entity.SystemUser;
import com.carsale.service.SystemUserService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * 认证控制器
 * 处理用户登录相关请求
 */
@RestController
public class AuthController {
    @Autowired
    private SystemUserService systemUserService;

    /**
     * 用户登录
     * @param loginRequest 登录请求参数，包含username和password
     * @return 登录结果
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
            // 登录成功，生成token（这里使用简单的临时token，实际项目中应该使用JWT）
            String token = "temp_token_" + System.currentTimeMillis();

            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            systemUserService.updateById(user);

            // 构造返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);

            // 构造用户信息，不包含敏感字段
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("role", user.getRole());
            userInfo.put("status", user.getStatus());

            data.put("userInfo", userInfo);

            return Result.success(data);
        } else {
            // 登录失败
            return Result.error(401, "用户名或密码错误");
        }
    }
}
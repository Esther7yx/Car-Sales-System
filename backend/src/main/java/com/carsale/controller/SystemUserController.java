package com.carsale.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.annotation.RequiresRole;
import com.carsale.entity.SystemUser;
import com.carsale.service.SystemUserService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户管理控制器
 * 提供用户的增删改查功能
 * 只有管理员可以访问此控制器
 */
@RestController
@RequestMapping("/api/system-users")
@RequiresRole("admin")
public class SystemUserController {

    @Autowired
    private SystemUserService systemUserService;

    /**
     * 获取用户列表（分页）
     */
    @GetMapping("/list")
    public Result<Page<SystemUser>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SystemUser> userPage = systemUserService.page(
                new Page<>(page, size),
                new QueryWrapper<SystemUser>().orderByDesc("created_at")
        );
        return Result.success(userPage);
    }

    /**
     * 获取所有用户列表（不分页）
     */
    @GetMapping("/all")
    public Result<List<SystemUser>> getAllUsers() {
        List<SystemUser> users = systemUserService.list(new QueryWrapper<SystemUser>().orderByDesc("created_at"));
        return Result.success(users);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<SystemUser> getUserById(@PathVariable Integer id) {
        SystemUser user = systemUserService.getById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error(404, "用户不存在");
    }

    /**
     * 创建用户
     */
    @PostMapping("/create")
    public Result<String> createUser(@RequestBody SystemUser user) {
        if (systemUserService.save(user)) {
            return Result.success("用户创建成功");
        }
        return Result.error(500, "用户创建失败");
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable Integer id, @RequestBody SystemUser user) {
        user.setUserId(id);
        if (systemUserService.updateById(user)) {
            return Result.success("用户更新成功");
        }
        return Result.error(500, "用户更新失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Integer id) {
        if (systemUserService.removeById(id)) {
            return Result.success("用户删除成功");
        }
        return Result.error(500, "用户删除失败");
    }
}

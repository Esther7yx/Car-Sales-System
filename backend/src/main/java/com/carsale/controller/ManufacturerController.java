package com.carsale.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.annotation.RequiresRole;
import com.carsale.entity.Manufacturer;
import com.carsale.service.ManufacturerService;
import com.carsale.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // 【新增】导入时间类
import java.util.List;

/**
 * 厂商信息Controller
 * 处理厂商信息相关的HTTP请求
 */
@RestController
@RequestMapping("/api/manufacturer")
@RequiresRole({"admin", "manager"})
public class ManufacturerController {

    @Autowired
    private ManufacturerService manufacturerService;

    // ... pageQuery, getById, getByStatus 方法保持不变 ...

    // 为了节省篇幅，省略了未修改的方法，请保留你原有的 pageQuery 等方法代码
    // 下面重点修改 add 和 update 方法

    /**
     * 分页查询厂商信息
     */
    @GetMapping("/page")
    public Result<IPage<Manufacturer>> pageQuery(@RequestParam(defaultValue = "1") long current,
                                                 @RequestParam(defaultValue = "10") long size,
                                                 @RequestParam(required = false) String manufacturerName,
                                                 @RequestParam(required = false) String cooperationStatus) {
        Page<Manufacturer> page = new Page<>(current, size);
        IPage<Manufacturer> pageResult = manufacturerService.selectPage(page, manufacturerName, cooperationStatus);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<Manufacturer> getById(@PathVariable Integer id) {
        Manufacturer manufacturer = manufacturerService.getById(id);
        if (manufacturer == null) {
            return Result.error("厂商不存在");
        }
        return Result.success(manufacturer);
    }

    @GetMapping("/by-status/{status}")
    public Result<List<Manufacturer>> getByStatus(@PathVariable String status) {
        List<Manufacturer> list = manufacturerService.selectByStatus(status);
        return Result.success(list);
    }

    /**
     * 新增厂商信息
     * @param manufacturer 厂商信息
     * @return 操作结果
     */
    @PostMapping
    public Result<String> add(@RequestBody Manufacturer manufacturer) {
        // 验证参数
        if (StringUtils.isBlank(manufacturer.getManufacturerName())) {
            return Result.error("厂商名称不能为空");
        }
        if (StringUtils.isBlank(manufacturer.getContactPerson())) {
            return Result.error("联系人不能为空");
        }
        if (StringUtils.isBlank(manufacturer.getContactPhone())) {
            return Result.error("联系电话不能为空");
        }
        if (StringUtils.isBlank(manufacturer.getAddress())) {
            return Result.error("地址不能为空");
        }

        // 检查厂商名称是否已存在
        if (manufacturerService.checkManufacturerNameExists(manufacturer.getManufacturerName(), null)) {
            return Result.error("厂商名称已存在");
        }

        // 设置默认合作状态
        if (StringUtils.isBlank(manufacturer.getCooperationStatus())) {
            manufacturer.setCooperationStatus("active");
        }

        // 【新增】手动设置创建时间和更新时间，确保数据库能存入当前时间
        manufacturer.setCreatedAt(LocalDateTime.now());
        manufacturer.setUpdatedAt(LocalDateTime.now());

        // 保存厂商信息
        boolean success = manufacturerService.save(manufacturer);
        if (success) {
            return Result.success("新增成功");
        } else {
            return Result.error("新增失败");
        }
    }

    /**
     * 更新厂商信息
     * @param id 厂商ID
     * @param manufacturer 厂商信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Integer id, @RequestBody Manufacturer manufacturer) {
        // 验证厂商是否存在
        Manufacturer existing = manufacturerService.getById(id);
        if (existing == null) {
            return Result.error("厂商不存在");
        }

        // 验证参数
        if (StringUtils.isBlank(manufacturer.getManufacturerName())) {
            return Result.error("厂商名称不能为空");
        }
        if (StringUtils.isBlank(manufacturer.getContactPerson())) {
            return Result.error("联系人不能为空");
        }
        if (StringUtils.isBlank(manufacturer.getContactPhone())) {
            return Result.error("联系电话不能为空");
        }
        if (StringUtils.isBlank(manufacturer.getAddress())) {
            return Result.error("地址不能为空");
        }

        // 检查厂商名称是否已存在（排除当前记录）
        if (!existing.getManufacturerName().equals(manufacturer.getManufacturerName()) &&
                manufacturerService.checkManufacturerNameExists(manufacturer.getManufacturerName(), id)) {
            return Result.error("厂商名称已存在");
        }

        // 设置厂商ID
        manufacturer.setManufacturerId(id);

        // 【新增】更新时，手动设置更新时间
        manufacturer.setUpdatedAt(LocalDateTime.now());

        // 更新厂商信息
        boolean success = manufacturerService.updateById(manufacturer);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    // ... delete 和 batchDelete 方法保持不变 ...
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        Manufacturer existing = manufacturerService.getById(id);
        if (existing == null) {
            return Result.error("厂商不存在");
        }
        boolean success = manufacturerService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的厂商");
        }
        boolean success = manufacturerService.removeByIds(ids);
        return success ? Result.success("批量删除成功") : Result.error("批量删除失败");
    }
}
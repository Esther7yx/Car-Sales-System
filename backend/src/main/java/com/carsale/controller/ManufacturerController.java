package com.carsale.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.Manufacturer;
import com.carsale.service.ManufacturerService;
import com.carsale.utils.Result;
import com.carsale.utils.Result.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 厂商信息Controller
 * 处理厂商信息相关的HTTP请求
 */
@RestController
@RequestMapping("/manufacturer")
public class ManufacturerController {

    @Autowired
    private ManufacturerService manufacturerService;

    /**
     * 分页查询厂商信息
     * @param current 当前页码
     * @param size 每页记录数
     * @param manufacturerName 厂商名称模糊查询
     * @param cooperationStatus 合作状态查询
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<List<Manufacturer>> pageQuery(@RequestParam(defaultValue = "1") long current,
                                                @RequestParam(defaultValue = "10") long size,
                                                @RequestParam(required = false) String manufacturerName,
                                                @RequestParam(required = false) String cooperationStatus) {
        // 创建分页对象
        Page<Manufacturer> page = new Page<>(current, size);
        
        // 执行分页查询
        com.baomidou.mybatisplus.core.metadata.IPage<Manufacturer> pageResult = 
                manufacturerService.selectPage(page, manufacturerName, cooperationStatus);
        
        // 构建分页信息
        PageInfo pageInfo = new PageInfo();
        pageInfo.setCurrent(pageResult.getCurrent());
        pageInfo.setSize(pageResult.getSize());
        pageInfo.setTotal(pageResult.getTotal());
        pageInfo.setPages(pageResult.getPages());
        pageInfo.setHasPrevious(pageResult.getCurrent() > 1);
        pageInfo.setHasNext(pageResult.getCurrent() < pageResult.getPages());
        
        // 返回结果
        return Result.success(pageResult.getRecords(), pageInfo);
    }

    /**
     * 根据ID查询厂商信息
     * @param id 厂商ID
     * @return 厂商信息
     */
    @GetMapping("/{id}")
    public Result<Manufacturer> getById(@PathVariable Integer id) {
        Manufacturer manufacturer = manufacturerService.getById(id);
        if (manufacturer == null) {
            return Result.error("厂商不存在");
        }
        return Result.success(manufacturer);
    }

    /**
     * 根据合作状态查询厂商列表
     * @param status 合作状态
     * @return 厂商列表
     */
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
        
        // 更新厂商信息
        boolean success = manufacturerService.updateById(manufacturer);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    /**
     * 删除厂商信息
     * @param id 厂商ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        // 验证厂商是否存在
        Manufacturer existing = manufacturerService.getById(id);
        if (existing == null) {
            return Result.error("厂商不存在");
        }
        
        // 删除厂商信息
        boolean success = manufacturerService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 批量删除厂商信息
     * @param ids 厂商ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的厂商");
        }
        
        // 批量删除厂商信息
        boolean success = manufacturerService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        } else {
            return Result.error("批量删除失败");
        }
    }
}

package com.carsale.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.Vehicle;
import com.carsale.service.VehicleService;
import com.carsale.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map; // 导入Map用于统计接口

/**
 * 车辆信息表Controller
 * 提供车辆信息的RESTful API接口
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * 【新增】获取仪表盘统计数据接口
     * 返回总车辆数、库存数、总销售数、总利润
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = vehicleService.getDashboardStats();
        return Result.success(stats);
    }

    /**
     * 分页查询车辆信息，包含车型和厂商关联
     * 【保留修复】返回 IPage<Vehicle> 以匹配前端结构
     */
    @GetMapping("/page")
    public Result<IPage<Vehicle>> pageQuery(@RequestParam(defaultValue = "1") long current,
                                            @RequestParam(defaultValue = "10") long size,
                                            @RequestParam(required = false) String vin,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(required = false) Integer modelId) {
        Page<Vehicle> page = new Page<>(current, size);
        IPage<Vehicle> pageResult = vehicleService.selectPageWithDetails(page, vin, status, modelId);

        // 直接返回 pageResult，包含 records 和 total
        return Result.success(pageResult);
    }

    /**
     * 根据VIN码查询车辆信息
     */
    @GetMapping("/{vin}")
    public Result<Vehicle> getByVin(@PathVariable String vin) {
        Vehicle vehicle = vehicleService.getById(vin);
        if (vehicle == null) {
            return Result.error("车辆不存在");
        }
        return Result.success(vehicle);
    }

    /**
     * 根据车型ID查询车辆列表
     */
    @GetMapping("/by-model/{modelId}")
    public Result<List<Vehicle>> getByModelId(@PathVariable Integer modelId) {
        List<Vehicle> vehicles = vehicleService.selectByModelId(modelId);
        return Result.success(vehicles);
    }

    /**
     * 根据状态查询车辆列表
     */
    @GetMapping("/by-status/{status}")
    public Result<List<Vehicle>> getByStatus(@PathVariable String status) {
        List<Vehicle> vehicles = vehicleService.selectByStatus(status);
        return Result.success(vehicles);
    }

    /**
     * 查询当前库存车辆（在库状态）
     */
    @GetMapping("/inventory")
    public Result<List<Vehicle>> getCurrentInventory() {
        List<Vehicle> vehicles = vehicleService.selectCurrentInventory();
        return Result.success(vehicles);
    }

    /**
     * 获取可售车辆列表（在库状态且未被预订）
     */
    @GetMapping("/available")
    public Result<List<Vehicle>> getAvailableVehicles() {
        List<Vehicle> vehicles = vehicleService.selectAvailableVehicles();
        return Result.success(vehicles);
    }

    /**
     * 新增车辆信息
     * 【保留修复】手动设置创建/更新时间
     */
    @PostMapping
    public Result<String> add(@RequestBody Vehicle vehicle) {
        // 验证VIN码是否为空
        if (StringUtils.isBlank(vehicle.getVin())) {
            return Result.error("VIN码不能为空");
        }

        // 验证VIN码格式（17位字符）
        if (vehicle.getVin().length() != 17) {
            return Result.error("VIN码必须为17位字符");
        }

        // 验证车型ID是否存在
        if (vehicle.getModelId() == null) {
            return Result.error("车型ID不能为空");
        }

        // 验证采购价和销售价
        if (vehicle.getPurchasePrice() == null || vehicle.getSalePrice() == null) {
            return Result.error("采购价和销售价不能为空");
        }

        // 验证销售价是否大于等于采购价
        if (vehicle.getSalePrice().compareTo(vehicle.getPurchasePrice()) < 0) {
            return Result.error("销售价必须大于等于采购价");
        }

        // 验证VIN码是否已存在
        if (vehicleService.checkVinExists(vehicle.getVin(), null)) {
            return Result.error("该VIN码已存在");
        }

        // 手动设置创建时间和更新时间
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());

        boolean saved = vehicleService.save(vehicle);
        if (saved) {
            return Result.success("新增车辆成功");
        } else {
            return Result.error("新增车辆失败");
        }
    }

    /**
     * 更新车辆信息
     * 【保留修复】手动更新时间
     */
    @PutMapping("/{vin}")
    public Result<String> update(@PathVariable String vin, @RequestBody Vehicle vehicle) {
        // 验证车辆是否存在
        if (vehicleService.getById(vin) == null) {
            return Result.error("车辆不存在");
        }

        // 设置VIN码
        vehicle.setVin(vin);

        // 验证销售价是否大于等于采购价
        if (vehicle.getSalePrice().compareTo(vehicle.getPurchasePrice()) < 0) {
            return Result.error("销售价必须大于等于采购价");
        }

        // 手动设置更新时间
        vehicle.setUpdatedAt(LocalDateTime.now());

        boolean updated = vehicleService.updateById(vehicle);
        if (updated) {
            return Result.success("更新车辆成功");
        } else {
            return Result.error("更新车辆失败");
        }
    }

    /**
     * 更新车辆状态
     * 【保留修复】
     * 1. 使用 @PutMapping
     * 2. 状态变为 sold 时自动记录销售日期
     */
    @PutMapping("/{vin}/status")
    public Result<String> updateStatus(@PathVariable String vin, @RequestParam String status) {
        // 1. 获取当前车辆信息
        Vehicle vehicle = vehicleService.getById(vin);
        if (vehicle == null) {
            return Result.error("车辆不存在");
        }

        // 2. 验证状态值是否合法
        if (!StringUtils.equalsAny(status, "in_stock", "sold", "reserved", "in_transit")) {
            return Result.error("无效的车辆状态");
        }

        // 3. 设置新状态和更新时间
        vehicle.setStatus(status);
        vehicle.setUpdatedAt(LocalDateTime.now());

        // 【关键逻辑】如果状态变为已售(sold)，自动设置销售日期为今天
        if ("sold".equals(status)) {
            vehicle.setSaleDate(LocalDate.now());
        }

        // 4. 更新数据库
        boolean success = vehicleService.updateById(vehicle);

        if (success) {
            return Result.success("更新车辆状态成功");
        } else {
            return Result.error("更新车辆状态失败");
        }
    }

    /**
     * 删除车辆信息
     */
    @DeleteMapping("/{vin}")
    public Result<String> delete(@PathVariable String vin) {
        // 验证车辆是否存在
        if (vehicleService.getById(vin) == null) {
            return Result.error("车辆不存在");
        }

        boolean deleted = vehicleService.removeById(vin);
        if (deleted) {
            return Result.success("删除车辆成功");
        } else {
            return Result.error("删除车辆失败");
        }
    }

    /**
     * 批量删除车辆信息
     */
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<String> vins) {
        if (vins == null || vins.isEmpty()) {
            return Result.error("请选择要删除的车辆");
        }

        boolean deleted = vehicleService.removeByIds(vins);
        if (deleted) {
            return Result.success("批量删除车辆成功");
        } else {
            return Result.error("批量删除车辆失败");
        }
    }
}
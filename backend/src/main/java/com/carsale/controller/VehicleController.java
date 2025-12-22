package com.carsale.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.Vehicle;
import com.carsale.service.VehicleService;
import com.carsale.utils.Result;
import com.carsale.utils.Result.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;

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
     * 分页查询车辆信息，包含车型和厂商关联
     * @param current 当前页码
     * @param size 每页大小
     * @param vin VIN码模糊查询
     * @param status 车辆状态查询
     * @param modelId 车型ID查询
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public Result<List<Vehicle>> pageQuery(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size,
                                          @RequestParam(required = false) String vin,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) Integer modelId) {
        Page<Vehicle> page = new Page<>(current, size);
        IPage<Vehicle> pageResult = vehicleService.selectPageWithDetails(page, vin, status, modelId);
        
        PageInfo pageInfo = new PageInfo();
        pageInfo.setCurrent(pageResult.getCurrent());
        pageInfo.setSize(pageResult.getSize());
        pageInfo.setTotal(pageResult.getTotal());
        pageInfo.setPages(pageResult.getPages());
        pageInfo.setHasPrevious(pageResult.getCurrent() > 1);
        pageInfo.setHasNext(pageResult.getCurrent() < pageResult.getPages());
        
        return Result.success(pageResult.getRecords(), pageInfo);
    }

    /**
     * 根据VIN码查询车辆信息
     * @param vin VIN码
     * @return 车辆信息
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
     * @param modelId 车型ID
     * @return 车辆列表
     */
    @GetMapping("/by-model/{modelId}")
    public Result<List<Vehicle>> getByModelId(@PathVariable Integer modelId) {
        List<Vehicle> vehicles = vehicleService.selectByModelId(modelId);
        return Result.success(vehicles);
    }

    /**
     * 根据状态查询车辆列表
     * @param status 车辆状态
     * @return 车辆列表
     */
    @GetMapping("/by-status/{status}")
    public Result<List<Vehicle>> getByStatus(@PathVariable String status) {
        List<Vehicle> vehicles = vehicleService.selectByStatus(status);
        return Result.success(vehicles);
    }

    /**
     * 查询当前库存车辆（在库状态）
     * @return 库存车辆列表
     */
    @GetMapping("/inventory")
    public Result<List<Vehicle>> getCurrentInventory() {
        List<Vehicle> vehicles = vehicleService.selectCurrentInventory();
        return Result.success(vehicles);
    }

    /**
     * 新增车辆信息
     * @param vehicle 车辆信息
     * @return 新增结果
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
        
        boolean saved = vehicleService.save(vehicle);
        if (saved) {
            return Result.success("新增车辆成功");
        } else {
            return Result.error("新增车辆失败");
        }
    }

    /**
     * 更新车辆信息
     * @param vin VIN码
     * @param vehicle 车辆信息
     * @return 更新结果
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
        
        boolean updated = vehicleService.updateById(vehicle);
        if (updated) {
            return Result.success("更新车辆成功");
        } else {
            return Result.error("更新车辆失败");
        }
    }

    /**
     * 更新车辆状态
     * @param vin VIN码
     * @param status 新状态
     * @return 更新结果
     */
    @PatchMapping("/{vin}/status")
    public Result<String> updateStatus(@PathVariable String vin, @RequestParam String status) {
        // 验证车辆是否存在
        if (vehicleService.getById(vin) == null) {
            return Result.error("车辆不存在");
        }
        
        // 验证状态值是否合法
        if (!StringUtils.equalsAny(status, "in_stock", "sold", "reserved", "in_transit")) {
            return Result.error("无效的车辆状态");
        }
        
        int updated = vehicleService.updateStatusByVin(vin, status);
        if (updated > 0) {
            return Result.success("更新车辆状态成功");
        } else {
            return Result.error("更新车辆状态失败");
        }
    }

    /**
     * 删除车辆信息
     * @param vin VIN码
     * @return 删除结果
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
     * @param vins VIN码列表
     * @return 删除结果
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
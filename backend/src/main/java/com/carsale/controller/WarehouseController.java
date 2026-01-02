package com.carsale.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.Warehouse;
import com.carsale.entity.WarehouseDetail;
import com.carsale.service.WarehouseService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 仓库管理控制器
 */
@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    /**
     * 分页查询仓库信息
     */
    @GetMapping("/page")
    public Result<Page<Warehouse>> pageQuery(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String status) {
        
        Page<Warehouse> page = new Page<>(current, size);
        Page<Warehouse> pageResult = warehouseService.selectWarehousePage(page, warehouseName, status);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取仓库信息
     */
    @GetMapping("/{id}")
    public Result<Warehouse> getById(@PathVariable Integer id) {
        Warehouse warehouse = warehouseService.getById(id);
        return warehouse != null ? Result.success(warehouse) : Result.error("仓库不存在");
    }

    /**
     * 新增仓库
     */
    @PostMapping
    public Result<Boolean> create(@RequestBody Warehouse warehouse) {
        boolean saved = warehouseService.save(warehouse);
        return saved ? Result.success(true) : Result.error("新增仓库失败");
    }

    /**
     * 更新仓库信息
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody Warehouse warehouse) {
        boolean updated = warehouseService.updateById(warehouse);
        return updated ? Result.success(true) : Result.error("更新仓库失败");
    }

    /**
     * 删除仓库
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        boolean removed = warehouseService.removeById(id);
        return removed ? Result.success(true) : Result.error("删除仓库失败");
    }

    /**
     * 分页查询仓库明细
     */
    @GetMapping("/detail/page")
    public Result<Page<WarehouseDetail>> detailPageQuery(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Integer warehouseId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        Page<WarehouseDetail> page = new Page<>(current, size);
        Page<WarehouseDetail> pageResult = warehouseService.selectWarehouseDetailPage(page, warehouseId, operationType, startDate, endDate);
        return Result.success(pageResult);
    }

    /**
     * 车辆入库操作
     */
    @PostMapping("/in")
    public Result<Boolean> vehicleInStock(
            @RequestParam String vin,
            @RequestParam Integer warehouseId,
            @RequestParam(required = false) String relatedOrder,
            @RequestParam(required = false) String remark,
            @RequestParam Integer operatorId) {
        
        try {
            boolean success = warehouseService.vehicleInStock(vin, warehouseId, relatedOrder, remark, operatorId);
            return success ? Result.success(true) : Result.error("车辆入库失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 车辆出库操作
     */
    @PostMapping("/out")
    public Result<Boolean> vehicleOutStock(
            @RequestParam String vin,
            @RequestParam Integer warehouseId,
            @RequestParam(required = false) String relatedOrder,
            @RequestParam(required = false) String remark,
            @RequestParam Integer operatorId) {
        
        try {
            boolean success = warehouseService.vehicleOutStock(vin, warehouseId, relatedOrder, remark, operatorId);
            return success ? Result.success(true) : Result.error("车辆出库失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 车辆调拨操作
     */
    @PostMapping("/transfer")
    public Result<Boolean> vehicleTransfer(
            @RequestParam String vin,
            @RequestParam Integer fromWarehouseId,
            @RequestParam Integer toWarehouseId,
            @RequestParam(required = false) String remark,
            @RequestParam Integer operatorId) {
        
        try {
            boolean success = warehouseService.vehicleTransfer(vin, fromWarehouseId, toWarehouseId, remark, operatorId);
            return success ? Result.success(true) : Result.error("车辆调拨失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取仓库统计信息
     */
    @GetMapping("/statistics/{warehouseId}")
    public Result<Map<String, Object>> getWarehouseStatistics(@PathVariable Integer warehouseId) {
        Map<String, Object> stats = warehouseService.getWarehouseStatistics(warehouseId);
        return Result.success(stats);
    }

    /**
     * 获取进销存统计信息
     */
    @GetMapping("/inventory/statistics")
    public Result<Map<String, Object>> getInventoryStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        Map<String, Object> stats = warehouseService.getInventoryStatistics(period, startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 获取仓库中的车辆列表
     */
    @GetMapping("/vehicles/{warehouseId}")
    public Result<Object> getVehiclesInWarehouse(@PathVariable Integer warehouseId) {
        // 这里返回Object类型，因为需要返回车辆列表及其关联信息
        Object vehicles = warehouseService.getVehiclesInWarehouse(warehouseId);
        return Result.success(vehicles);
    }

    /**
     * 检查仓库容量是否足够
     */
    @GetMapping("/capacity/check")
    public Result<Boolean> checkWarehouseCapacity(
            @RequestParam Integer warehouseId,
            @RequestParam Integer additionalQuantity) {
        
        boolean sufficient = warehouseService.isWarehouseCapacitySufficient(warehouseId, additionalQuantity);
        return Result.success(sufficient);
    }

    /**
     * 更新仓库库存数量
     */
    @PutMapping("/stock")
    public Result<Boolean> updateWarehouseStock(
            @RequestParam Integer warehouseId,
            @RequestParam Integer newStock) {
        
        boolean success = warehouseService.updateWarehouseStock(warehouseId, newStock);
        return success ? Result.success(true) : Result.error("更新仓库库存失败");
    }
}
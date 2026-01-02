package com.carsale.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.PurchaseOrder;
import com.carsale.entity.Vehicle;
import com.carsale.service.PurchaseOrderService;
import com.carsale.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 采购订单控制器
 */
@RestController
@RequestMapping("/api/purchase")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * 分页查询采购订单
     */
    @GetMapping("/page")
    public Result<Page<PurchaseOrder>> pageQuery(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String manufacturerName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        Page<PurchaseOrder> page = new Page<>(current, size);
        Page<PurchaseOrder> pageResult = purchaseOrderService.selectPurchaseOrderPage(page, orderNumber, manufacturerName, status, startDate, endDate);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询采购订单详情
     */
    @GetMapping("/{id}")
    public Result<PurchaseOrder> getById(@PathVariable Integer id) {
        PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrderDetail(id);
        if (purchaseOrder == null) {
            return Result.error("采购订单不存在");
        }
        return Result.success(purchaseOrder);
    }

    /**
     * 创建采购订单
     */
    @PostMapping
    public Result<String> create(@RequestBody PurchaseOrder purchaseOrder) {
        try {
            // 验证必填字段
            if (StringUtils.isBlank(purchaseOrder.getOrderNumber())) {
                return Result.error("订单编号不能为空");
            }
            if (purchaseOrder.getManufacturerId() == null) {
                return Result.error("厂商ID不能为空");
            }
            if (purchaseOrder.getOperatorId() == null) {
                return Result.error("操作员ID不能为空");
            }

            // 这里需要传入车辆列表，简化处理
            boolean created = purchaseOrderService.createPurchaseOrder(purchaseOrder, List.of());
            if (created) {
                return Result.success("创建采购订单成功");
            } else {
                return Result.error("创建采购订单失败");
            }
        } catch (Exception e) {
            return Result.error("创建采购订单失败：" + e.getMessage());
        }
    }

    /**
     * 更新采购订单状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        try {
            if (StringUtils.isBlank(status)) {
                return Result.error("状态不能为空");
            }
            
            boolean updated = purchaseOrderService.updatePurchaseOrderStatus(id, status);
            if (updated) {
                return Result.success("更新状态成功");
            } else {
                return Result.error("更新状态失败");
            }
        } catch (Exception e) {
            return Result.error("更新状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取采购统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        Map<String, Object> statistics = purchaseOrderService.getPurchaseStatistics(period, startDate, endDate);
        return Result.success(statistics);
    }

    /**
     * 获取采购订单关联的车辆列表
     */
    @GetMapping("/{id}/vehicles")
    public Result<List<Vehicle>> getVehicles(@PathVariable Integer id) {
        List<Vehicle> vehicles = purchaseOrderService.getVehiclesByOrderId(id);
        return Result.success(vehicles);
    }

    /**
     * 检查订单编号是否已存在
     */
    @GetMapping("/check-order-number")
    public Result<Boolean> checkOrderNumber(@RequestParam String orderNumber) {
        boolean exists = purchaseOrderService.isOrderNumberExists(orderNumber);
        return Result.success(exists);
    }
}
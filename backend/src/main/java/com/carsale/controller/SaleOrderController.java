package com.carsale.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.SaleOrder;
import com.carsale.entity.SaleDetail;
import com.carsale.service.SaleOrderService;
import com.carsale.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 销售订单控制器
 */
@RestController
@RequestMapping("/api/sale")
public class SaleOrderController {

    @Autowired
    private SaleOrderService saleOrderService;

    /**
     * 分页查询销售订单
     */
    @GetMapping("/page")
    public Result<Page<SaleOrder>> pageQuery(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        Page<SaleOrder> page = new Page<>(current, size);
        Page<SaleOrder> pageResult = saleOrderService.selectSaleOrderPage(page, orderNumber, customerName, status, paymentMethod, startDate, endDate);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询销售订单详情
     */
    @GetMapping("/{id}")
    public Result<SaleOrder> getById(@PathVariable Integer id) {
        SaleOrder saleOrder = saleOrderService.getSaleOrderDetail(id);
        if (saleOrder == null) {
            return Result.error("销售订单不存在");
        }
        return Result.success(saleOrder);
    }

    /**
     * 创建销售订单
     */
    @PostMapping
    public Result<String> create(@RequestBody SaleOrder saleOrder) {
        try {
            // 验证必填字段
            if (StringUtils.isBlank(saleOrder.getOrderNumber())) {
                return Result.error("订单编号不能为空");
            }
            if (saleOrder.getCustomerId() == null) {
                return Result.error("客户ID不能为空");
            }
            if (saleOrder.getOperatorId() == null) {
                return Result.error("操作员ID不能为空");
            }
            if (StringUtils.isBlank(saleOrder.getPaymentMethod())) {
                return Result.error("支付方式不能为空");
            }

            // 这里需要传入销售明细，简化处理
            boolean created = saleOrderService.createSaleOrder(saleOrder, List.of());
            if (created) {
                return Result.success("创建销售订单成功");
            } else {
                return Result.error("创建销售订单失败");
            }
        } catch (Exception e) {
            return Result.error("创建销售订单失败：" + e.getMessage());
        }
    }

    /**
     * 更新销售订单状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        try {
            if (StringUtils.isBlank(status)) {
                return Result.error("状态不能为空");
            }
            
            boolean updated = saleOrderService.updateSaleOrderStatus(id, status);
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
     * 获取销售统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        Map<String, Object> statistics = saleOrderService.getSalesStatistics(period, startDate, endDate);
        return Result.success(statistics);
    }

    /**
     * 获取销售明细列表
     */
    @GetMapping("/{id}/details")
    public Result<List<SaleDetail>> getSaleDetails(@PathVariable Integer id) {
        List<SaleDetail> saleDetails = saleOrderService.getSaleDetailsBySaleId(id);
        return Result.success(saleDetails);
    }

    /**
     * 检查订单编号是否已存在
     */
    @GetMapping("/check-order-number")
    public Result<Boolean> checkOrderNumber(@RequestParam String orderNumber) {
        boolean exists = saleOrderService.isOrderNumberExists(orderNumber);
        return Result.success(exists);
    }
}
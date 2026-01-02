package com.carsale.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.carsale.entity.PurchaseOrder;
import com.carsale.entity.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 采购订单服务接口
 */
public interface PurchaseOrderService extends IService<PurchaseOrder> {

    /**
     * 分页查询采购订单
     */
    Page<PurchaseOrder> selectPurchaseOrderPage(Page<PurchaseOrder> page, String orderNumber, String manufacturerName, String status, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 创建采购订单
     */
    boolean createPurchaseOrder(PurchaseOrder purchaseOrder, List<Vehicle> vehicles);

    /**
     * 更新采购订单状态
     */
    boolean updatePurchaseOrderStatus(Integer orderId, String status);

    /**
     * 获取采购订单详情
     */
    PurchaseOrder getPurchaseOrderDetail(Integer orderId);

    /**
     * 获取采购统计信息
     */
    Map<String, Object> getPurchaseStatistics(String period, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 计算采购订单总金额
     */
    BigDecimal calculateTotalAmount(List<Vehicle> vehicles);

    /**
     * 检查订单编号是否已存在
     */
    boolean isOrderNumberExists(String orderNumber);

    /**
     * 获取采购订单关联的车辆列表
     */
    List<Vehicle> getVehiclesByOrderId(Integer orderId);
}
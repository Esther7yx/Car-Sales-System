package com.carsale.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.carsale.entity.SaleOrder;
import com.carsale.entity.SaleDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 销售订单服务接口
 */
public interface SaleOrderService extends IService<SaleOrder> {

    /**
     * 分页查询销售订单
     */
    Page<SaleOrder> selectSaleOrderPage(Page<SaleOrder> page, String orderNumber, String customerName, String status, String paymentMethod, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 创建销售订单
     */
    boolean createSaleOrder(SaleOrder saleOrder, List<SaleDetail> saleDetails);

    /**
     * 更新销售订单状态
     */
    boolean updateSaleOrderStatus(Integer saleId, String status);

    /**
     * 获取销售订单详情
     */
    SaleOrder getSaleOrderDetail(Integer saleId);

    /**
     * 获取销售统计信息
     */
    Map<String, Object> getSalesStatistics(String period, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取销售明细列表
     */
    List<SaleDetail> getSaleDetailsBySaleId(Integer saleId);

    /**
     * 计算销售订单总金额
     */
    BigDecimal calculateTotalAmount(List<SaleDetail> saleDetails);

    /**
     * 检查订单编号是否已存在
     */
    boolean isOrderNumberExists(String orderNumber);
}
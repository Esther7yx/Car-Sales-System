package com.carsale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carsale.entity.*;
import com.carsale.mapper.SaleDetailMapper;
import com.carsale.mapper.SaleOrderMapper;
import com.carsale.service.SaleOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 销售订单服务实现类
 */
@Service
public class SaleOrderServiceImpl extends ServiceImpl<SaleOrderMapper, SaleOrder> implements SaleOrderService {

    @Autowired
    private SaleDetailMapper saleDetailMapper;

    @Override
    public Page<SaleOrder> selectSaleOrderPage(Page<SaleOrder> page, String orderNumber, String customerName, String status, String paymentMethod, LocalDateTime startDate, LocalDateTime endDate) {
        LambdaQueryWrapper<SaleOrder> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(orderNumber)) {
            queryWrapper.like(SaleOrder::getOrderNumber, orderNumber);
        }
        
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq(SaleOrder::getStatus, status);
        }
        
        if (StringUtils.isNotBlank(paymentMethod)) {
            queryWrapper.eq(SaleOrder::getPaymentMethod, paymentMethod);
        }
        
        if (startDate != null && endDate != null) {
            queryWrapper.between(SaleOrder::getCreateTime, startDate, endDate);
        } else if (startDate != null) {
            queryWrapper.ge(SaleOrder::getCreateTime, startDate);
        } else if (endDate != null) {
            queryWrapper.le(SaleOrder::getCreateTime, endDate);
        }
        
        queryWrapper.orderByDesc(SaleOrder::getCreateTime);
        
        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean createSaleOrder(SaleOrder saleOrder, List<SaleDetail> saleDetails) {
        // 验证订单编号是否已存在
        if (isOrderNumberExists(saleOrder.getOrderNumber())) {
            throw new RuntimeException("订单编号已存在");
        }
        
        // 计算总金额
        BigDecimal totalAmount = calculateTotalAmount(saleDetails);
        saleOrder.setTotalAmount(totalAmount);
        
        // 保存销售订单
        boolean saved = this.save(saleOrder);
        if (!saved) {
            throw new RuntimeException("保存销售订单失败");
        }
        
        // 保存销售明细
        for (SaleDetail detail : saleDetails) {
            detail.setSaleId(saleOrder.getSaleId());
            int inserted = saleDetailMapper.insert(detail);
            if (inserted <= 0) {
                throw new RuntimeException("保存销售明细失败");
            }
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean updateSaleOrderStatus(Integer saleId, String status) {
        SaleOrder saleOrder = this.getById(saleId);
        if (saleOrder == null) {
            throw new RuntimeException("销售订单不存在");
        }
        
        saleOrder.setStatus(status);
        
        // 根据状态更新对应时间
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case "paid":
                saleOrder.setPaymentTime(now);
                break;
            case "delivered":
                saleOrder.setDeliveryTime(now);
                break;
            case "cancelled":
                saleOrder.setCancelTime(now);
                break;
        }
        
        return this.updateById(saleOrder);
    }

    @Override
    public SaleOrder getSaleOrderDetail(Integer saleId) {
        SaleOrder saleOrder = this.getById(saleId);
        if (saleOrder != null) {
            // 获取销售明细
            List<SaleDetail> saleDetails = getSaleDetailsBySaleId(saleId);
            // 这里可以设置关联的客户信息等
        }
        return saleOrder;
    }

    @Override
    public Map<String, Object> getSalesStatistics(String period, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        // 总销售额
        LambdaQueryWrapper<SaleOrder> totalQuery = new LambdaQueryWrapper<>();
        totalQuery.eq(SaleOrder::getStatus, "delivered");
        if (startDate != null && endDate != null) {
            totalQuery.between(SaleOrder::getCreateTime, startDate, endDate);
        }
        List<SaleOrder> deliveredOrders = this.list(totalQuery);
        BigDecimal totalSales = deliveredOrders.stream()
                .map(SaleOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 销售订单数量
        long orderCount = this.count(totalQuery);
        
        // 本月销售额
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LambdaQueryWrapper<SaleOrder> monthQuery = new LambdaQueryWrapper<>();
        monthQuery.eq(SaleOrder::getStatus, "delivered")
                 .ge(SaleOrder::getCreateTime, monthStart);
        List<SaleOrder> monthOrders = this.list(monthQuery);
        BigDecimal monthSales = monthOrders.stream()
                .map(SaleOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("totalSales", totalSales);
        stats.put("orderCount", orderCount);
        stats.put("monthSales", monthSales);
        stats.put("monthOrderCount", monthOrders.size());
        
        return stats;
    }

    @Override
    public List<SaleDetail> getSaleDetailsBySaleId(Integer saleId) {
        LambdaQueryWrapper<SaleDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SaleDetail::getSaleId, saleId);
        return saleDetailMapper.selectList(queryWrapper);
    }

    @Override
    public BigDecimal calculateTotalAmount(List<SaleDetail> saleDetails) {
        return saleDetails.stream()
                .map(detail -> detail.getUnitPrice().subtract(detail.getDiscount()).multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean isOrderNumberExists(String orderNumber) {
        LambdaQueryWrapper<SaleOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SaleOrder::getOrderNumber, orderNumber);
        return this.count(queryWrapper) > 0;
    }
}
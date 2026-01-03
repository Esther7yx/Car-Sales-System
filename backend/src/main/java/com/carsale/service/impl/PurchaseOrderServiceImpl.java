package com.carsale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carsale.entity.PurchaseOrder;
import com.carsale.entity.Vehicle;
import com.carsale.mapper.PurchaseOrderMapper;
import com.carsale.mapper.VehicleMapper;
import com.carsale.service.PurchaseOrderService;
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
 * 采购订单服务实现类
 */
@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public Page<PurchaseOrder> selectPurchaseOrderPage(Page<PurchaseOrder> page, String orderNumber, String manufacturerName, String status, LocalDateTime startDate, LocalDateTime endDate) {
        LambdaQueryWrapper<PurchaseOrder> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(orderNumber)) {
            queryWrapper.like(PurchaseOrder::getOrderNumber, orderNumber);
        }
        
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq(PurchaseOrder::getStatus, status);
        }
        
        if (startDate != null && endDate != null) {
            queryWrapper.between(PurchaseOrder::getCreateTime, startDate, endDate);
        } else if (startDate != null) {
            queryWrapper.ge(PurchaseOrder::getCreateTime, startDate);
        } else if (endDate != null) {
            queryWrapper.le(PurchaseOrder::getCreateTime, endDate);
        }
        
        queryWrapper.orderByDesc(PurchaseOrder::getCreateTime);
        
        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean createPurchaseOrder(PurchaseOrder purchaseOrder, List<Vehicle> vehicles) {
        // 验证订单编号是否已存在
        if (isOrderNumberExists(purchaseOrder.getOrderNumber())) {
            throw new RuntimeException("订单编号已存在");
        }
        
        // 计算总金额
        BigDecimal totalAmount = calculateTotalAmount(vehicles);
        purchaseOrder.setTotalAmount(totalAmount);
        
        // 保存采购订单
        boolean saved = this.save(purchaseOrder);
        if (!saved) {
            throw new RuntimeException("保存采购订单失败");
        }
        
        // 保存车辆信息
        for (Vehicle vehicle : vehicles) {
            vehicle.setStatus("in_transit");
            vehicle.setPurchaseDate(LocalDateTime.now().toLocalDate());
            vehicle.setPurchaseOrderId(purchaseOrder.getOrderId());
            
            int inserted = vehicleMapper.insert(vehicle);
            if (inserted <= 0) {
                throw new RuntimeException("保存车辆信息失败");
            }
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean updatePurchaseOrderStatus(Integer orderId, String status) {
        PurchaseOrder purchaseOrder = this.getById(orderId);
        if (purchaseOrder == null) {
            throw new RuntimeException("采购订单不存在");
        }
        
        purchaseOrder.setStatus(status);
        
        // 根据状态更新对应时间
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case "approved":
                purchaseOrder.setApproveTime(now);
                break;
            case "received":
                purchaseOrder.setReceiveTime(now);
                // 更新车辆状态为在库
                updateVehicleStatusByOrder(orderId, "in_stock");
                break;
            case "cancelled":
                purchaseOrder.setCancelTime(now);
                // 更新车辆状态为取消（暂时注释，因为车辆表缺少purchase_order_id字段）
                // updateVehicleStatusByOrder(orderId, "cancelled");
                break;
        }
        
        return this.updateById(purchaseOrder);
    }

    @Override
    public PurchaseOrder getPurchaseOrderDetail(Integer orderId) {
        PurchaseOrder purchaseOrder = this.getById(orderId);
        if (purchaseOrder != null) {
            // 获取关联的车辆列表
            List<Vehicle> vehicles = getVehiclesByOrderId(orderId);
            // 这里可以设置关联的厂商信息等
        }
        return purchaseOrder;
    }

    @Override
    public Map<String, Object> getPurchaseStatistics(String period, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        // 总采购金额
        LambdaQueryWrapper<PurchaseOrder> totalQuery = new LambdaQueryWrapper<>();
        totalQuery.eq(PurchaseOrder::getStatus, "received");
        if (startDate != null && endDate != null) {
            totalQuery.between(PurchaseOrder::getCreateTime, startDate, endDate);
        }
        List<PurchaseOrder> receivedOrders = this.list(totalQuery);
        BigDecimal totalPurchase = receivedOrders.stream()
                .map(PurchaseOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 采购订单数量
        long orderCount = this.count(totalQuery);
        
        // 本月采购金额
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LambdaQueryWrapper<PurchaseOrder> monthQuery = new LambdaQueryWrapper<>();
        monthQuery.eq(PurchaseOrder::getStatus, "received")
                 .ge(PurchaseOrder::getCreateTime, monthStart);
        List<PurchaseOrder> monthOrders = this.list(monthQuery);
        BigDecimal monthPurchase = monthOrders.stream()
                .map(PurchaseOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 待审批订单数量
        LambdaQueryWrapper<PurchaseOrder> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(PurchaseOrder::getStatus, "pending");
        long pendingCount = this.count(pendingQuery);
        
        stats.put("totalPurchase", totalPurchase);
        stats.put("orderCount", orderCount);
        stats.put("monthPurchase", monthPurchase);
        stats.put("monthOrderCount", monthOrders.size());
        stats.put("pendingCount", pendingCount);
        
        return stats;
    }

    @Override
    public BigDecimal calculateTotalAmount(List<Vehicle> vehicles) {
        return vehicles.stream()
                .map(Vehicle::getPurchasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean isOrderNumberExists(String orderNumber) {
        LambdaQueryWrapper<PurchaseOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseOrder::getOrderNumber, orderNumber);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public List<Vehicle> getVehiclesByOrderId(Integer orderId) {
        // 根据采购订单ID查询关联的车辆
        // 这里假设车辆表中有purchase_order_id字段来关联采购订单
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Vehicle> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(Vehicle::getPurchaseOrderId, orderId);
        return vehicleMapper.selectList(queryWrapper);
    }

    /**
     * 更新订单关联车辆的状态
     */
    private void updateVehicleStatusByOrder(Integer orderId, String status) {
        // 获取该订单关联的所有车辆
        List<Vehicle> vehicles = getVehiclesByOrderId(orderId);
        
        // 批量更新车辆状态
        for (Vehicle vehicle : vehicles) {
            vehicle.setStatus(status);
            vehicleMapper.updateById(vehicle);
        }
    }
}
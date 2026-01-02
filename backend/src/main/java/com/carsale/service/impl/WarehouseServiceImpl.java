package com.carsale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carsale.entity.Warehouse;
import com.carsale.entity.WarehouseDetail;
import com.carsale.entity.Vehicle;
import com.carsale.mapper.WarehouseMapper;
import com.carsale.mapper.WarehouseDetailMapper;
import com.carsale.mapper.VehicleMapper;
import com.carsale.service.WarehouseService;
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
 * 仓库管理服务实现类
 */
@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    @Autowired
    private WarehouseDetailMapper warehouseDetailMapper;

    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public Page<Warehouse> selectWarehousePage(Page<Warehouse> page, String warehouseName, String status) {
        LambdaQueryWrapper<Warehouse> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(warehouseName)) {
            queryWrapper.like(Warehouse::getWarehouseName, warehouseName);
        }
        
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq(Warehouse::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Warehouse::getCreatedAt);
        
        return this.page(page, queryWrapper);
    }

    @Override
    public Page<WarehouseDetail> selectWarehouseDetailPage(Page<WarehouseDetail> page, Integer warehouseId, 
                                                          String operationType, LocalDateTime startDate, LocalDateTime endDate) {
        LambdaQueryWrapper<WarehouseDetail> queryWrapper = new LambdaQueryWrapper<>();
        
        if (warehouseId != null) {
            queryWrapper.eq(WarehouseDetail::getWarehouseId, warehouseId);
        }
        
        if (StringUtils.isNotBlank(operationType)) {
            queryWrapper.eq(WarehouseDetail::getOperationType, operationType);
        }
        
        if (startDate != null && endDate != null) {
            queryWrapper.between(WarehouseDetail::getOperationTime, startDate, endDate);
        } else if (startDate != null) {
            queryWrapper.ge(WarehouseDetail::getOperationTime, startDate);
        } else if (endDate != null) {
            queryWrapper.le(WarehouseDetail::getOperationTime, endDate);
        }
        
        queryWrapper.orderByDesc(WarehouseDetail::getOperationTime);
        
        return warehouseDetailMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean vehicleInStock(String vin, Integer warehouseId, String relatedOrder, String remark, Integer operatorId) {
        // 获取仓库信息
        Warehouse warehouse = this.getById(warehouseId);
        if (warehouse == null) {
            throw new RuntimeException("仓库不存在");
        }
        
        // 检查仓库容量
        if (!isWarehouseCapacitySufficient(warehouseId, 1)) {
            throw new RuntimeException("仓库容量不足");
        }
        
        // 获取车辆信息
        Vehicle vehicle = vehicleMapper.selectById(vin);
        if (vehicle == null) {
            throw new RuntimeException("车辆不存在");
        }
        
        // 记录入库明细
        WarehouseDetail detail = new WarehouseDetail();
        detail.setWarehouseId(warehouseId);
        detail.setVin(vin);
        detail.setOperationType("in");
        detail.setQuantity(1);
        detail.setBeforeStock(warehouse.getCurrentStock());
        detail.setAfterStock(warehouse.getCurrentStock() + 1);
        detail.setAmount(vehicle.getPurchasePrice());
        detail.setOperatorId(operatorId);
        detail.setRelatedOrder(relatedOrder);
        detail.setRemark(remark);
        detail.setOperationTime(LocalDateTime.now());
        
        int inserted = warehouseDetailMapper.insert(detail);
        if (inserted <= 0) {
            throw new RuntimeException("记录入库明细失败");
        }
        
        // 更新仓库库存
        warehouse.setCurrentStock(warehouse.getCurrentStock() + 1);
        boolean updated = this.updateById(warehouse);
        if (!updated) {
            throw new RuntimeException("更新仓库库存失败");
        }
        
        // 更新车辆状态和仓库位置
        vehicle.setStatus("in_stock");
        vehicle.setWarehouseLocation(warehouse.getWarehouseName());
        vehicleMapper.updateById(vehicle);
        
        return true;
    }

    @Override
    @Transactional
    public boolean vehicleOutStock(String vin, Integer warehouseId, String relatedOrder, String remark, Integer operatorId) {
        // 获取仓库信息
        Warehouse warehouse = this.getById(warehouseId);
        if (warehouse == null) {
            throw new RuntimeException("仓库不存在");
        }
        
        // 检查库存是否足够
        if (warehouse.getCurrentStock() <= 0) {
            throw new RuntimeException("仓库库存不足");
        }
        
        // 获取车辆信息
        Vehicle vehicle = vehicleMapper.selectById(vin);
        if (vehicle == null) {
            throw new RuntimeException("车辆不存在");
        }
        
        // 记录出库明细
        WarehouseDetail detail = new WarehouseDetail();
        detail.setWarehouseId(warehouseId);
        detail.setVin(vin);
        detail.setOperationType("out");
        detail.setQuantity(1);
        detail.setBeforeStock(warehouse.getCurrentStock());
        detail.setAfterStock(warehouse.getCurrentStock() - 1);
        detail.setAmount(vehicle.getSalePrice());
        detail.setOperatorId(operatorId);
        detail.setRelatedOrder(relatedOrder);
        detail.setRemark(remark);
        detail.setOperationTime(LocalDateTime.now());
        
        int inserted = warehouseDetailMapper.insert(detail);
        if (inserted <= 0) {
            throw new RuntimeException("记录出库明细失败");
        }
        
        // 更新仓库库存
        warehouse.setCurrentStock(warehouse.getCurrentStock() - 1);
        boolean updated = this.updateById(warehouse);
        if (!updated) {
            throw new RuntimeException("更新仓库库存失败");
        }
        
        // 更新车辆状态
        vehicle.setStatus("sold");
        vehicle.setSaleDate(LocalDateTime.now().toLocalDate());
        vehicleMapper.updateById(vehicle);
        
        return true;
    }

    @Override
    @Transactional
    public boolean vehicleTransfer(String vin, Integer fromWarehouseId, Integer toWarehouseId, String remark, Integer operatorId) {
        // 先出库
        boolean outSuccess = vehicleOutStock(vin, fromWarehouseId, "transfer", remark, operatorId);
        if (!outSuccess) {
            return false;
        }
        
        // 再入库
        boolean inSuccess = vehicleInStock(vin, toWarehouseId, "transfer", remark, operatorId);
        if (!inSuccess) {
            // 如果入库失败，需要回滚出库操作
            throw new RuntimeException("调拨入库失败，已回滚出库操作");
        }
        
        return true;
    }

    @Override
    public Map<String, Object> getWarehouseStatistics(Integer warehouseId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取仓库基本信息
        Warehouse warehouse = this.getById(warehouseId);
        if (warehouse != null) {
            stats.put("warehouseInfo", warehouse);
            
            // 计算库存利用率
            double utilizationRate = warehouse.getCapacity() > 0 ? 
                (double) warehouse.getCurrentStock() / warehouse.getCapacity() * 100 : 0;
            stats.put("utilizationRate", String.format("%.2f%%", utilizationRate));
            
            // 今日入库数量
            LambdaQueryWrapper<WarehouseDetail> todayInQuery = new LambdaQueryWrapper<>();
            todayInQuery.eq(WarehouseDetail::getWarehouseId, warehouseId)
                       .eq(WarehouseDetail::getOperationType, "in")
                       .ge(WarehouseDetail::getOperationTime, LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
            long todayInCount = warehouseDetailMapper.selectCount(todayInQuery);
            stats.put("todayInCount", todayInCount);
            
            // 今日出库数量
            LambdaQueryWrapper<WarehouseDetail> todayOutQuery = new LambdaQueryWrapper<>();
            todayOutQuery.eq(WarehouseDetail::getWarehouseId, warehouseId)
                        .eq(WarehouseDetail::getOperationType, "out")
                        .ge(WarehouseDetail::getOperationTime, LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
            long todayOutCount = warehouseDetailMapper.selectCount(todayOutQuery);
            stats.put("todayOutCount", todayOutCount);
        }
        
        return stats;
    }

    @Override
    public Map<String, Object> getInventoryStatistics(String period, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        // 总入库数量
        LambdaQueryWrapper<WarehouseDetail> inQuery = new LambdaQueryWrapper<>();
        inQuery.eq(WarehouseDetail::getOperationType, "in");
        if (startDate != null && endDate != null) {
            inQuery.between(WarehouseDetail::getOperationTime, startDate, endDate);
        }
        long totalIn = warehouseDetailMapper.selectCount(inQuery);
        stats.put("totalIn", totalIn);
        
        // 总出库数量
        LambdaQueryWrapper<WarehouseDetail> outQuery = new LambdaQueryWrapper<>();
        outQuery.eq(WarehouseDetail::getOperationType, "out");
        if (startDate != null && endDate != null) {
            outQuery.between(WarehouseDetail::getOperationTime, startDate, endDate);
        }
        long totalOut = warehouseDetailMapper.selectCount(outQuery);
        stats.put("totalOut", totalOut);
        
        // 当前总库存
        LambdaQueryWrapper<Warehouse> warehouseQuery = new LambdaQueryWrapper<>();
        warehouseQuery.eq(Warehouse::getStatus, "active");
        List<Warehouse> warehouses = this.list(warehouseQuery);
        int totalStock = warehouses.stream().mapToInt(Warehouse::getCurrentStock).sum();
        stats.put("totalStock", totalStock);
        
        // 库存周转率
        double turnoverRate = totalStock > 0 ? (double) totalOut / totalStock : 0;
        stats.put("turnoverRate", String.format("%.2f", turnoverRate));
        
        return stats;
    }

    @Override
    public List<Vehicle> getVehiclesInWarehouse(Integer warehouseId) {
        LambdaQueryWrapper<Vehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Vehicle::getStatus, "in_stock");
        // 这里假设车辆表中有warehouse_id字段来关联仓库
        // 简化处理，返回所有在库车辆
        return vehicleMapper.selectList(queryWrapper);
    }

    @Override
    public boolean isWarehouseCapacitySufficient(Integer warehouseId, Integer additionalQuantity) {
        Warehouse warehouse = this.getById(warehouseId);
        if (warehouse == null) {
            return false;
        }
        return warehouse.getCurrentStock() + additionalQuantity <= warehouse.getCapacity();
    }

    @Override
    public boolean updateWarehouseStock(Integer warehouseId, Integer newStock) {
        Warehouse warehouse = this.getById(warehouseId);
        if (warehouse == null) {
            return false;
        }
        
        warehouse.setCurrentStock(newStock);
        return this.updateById(warehouse);
    }
}
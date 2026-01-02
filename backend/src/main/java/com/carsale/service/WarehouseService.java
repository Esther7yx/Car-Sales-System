package com.carsale.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.carsale.entity.Warehouse;
import com.carsale.entity.WarehouseDetail;
import com.carsale.entity.Vehicle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 仓库管理服务接口
 */
public interface WarehouseService extends IService<Warehouse> {

    /**
     * 分页查询仓库信息
     */
    Page<Warehouse> selectWarehousePage(Page<Warehouse> page, String warehouseName, String status);

    /**
     * 分页查询仓库明细
     */
    Page<WarehouseDetail> selectWarehouseDetailPage(Page<WarehouseDetail> page, Integer warehouseId, 
                                                   String operationType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 车辆入库操作
     */
    boolean vehicleInStock(String vin, Integer warehouseId, String relatedOrder, String remark, Integer operatorId);

    /**
     * 车辆出库操作
     */
    boolean vehicleOutStock(String vin, Integer warehouseId, String relatedOrder, String remark, Integer operatorId);

    /**
     * 车辆调拨操作
     */
    boolean vehicleTransfer(String vin, Integer fromWarehouseId, Integer toWarehouseId, String remark, Integer operatorId);

    /**
     * 获取仓库库存统计
     */
    Map<String, Object> getWarehouseStatistics(Integer warehouseId);

    /**
     * 获取进销存统计
     */
    Map<String, Object> getInventoryStatistics(String period, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取仓库中的车辆列表
     */
    List<Vehicle> getVehiclesInWarehouse(Integer warehouseId);

    /**
     * 检查仓库容量是否足够
     */
    boolean isWarehouseCapacitySufficient(Integer warehouseId, Integer additionalQuantity);

    /**
     * 更新仓库当前库存数量
     */
    boolean updateWarehouseStock(Integer warehouseId, Integer newStock);
}
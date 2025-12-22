package com.carsale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carsale.entity.Vehicle;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 车辆信息表Service接口
 * 继承MyBatis Plus的IService，提供基础的业务逻辑操作
 */
public interface VehicleService extends IService<Vehicle> {
    /**
     * 分页查询车辆信息，包含车型和厂商关联
     * @param page 分页参数
     * @param vin VIN码模糊查询
     * @param status 车辆状态查询
     * @param modelId 车型ID查询
     * @return 分页结果
     */
    IPage<Vehicle> selectPageWithDetails(Page<Vehicle> page, String vin, String status, Integer modelId);

    /**
     * 根据车型ID查询车辆列表
     * @param modelId 车型ID
     * @return 车辆列表
     */
    List<Vehicle> selectByModelId(Integer modelId);

    /**
     * 查询当前库存车辆（在库状态）
     * @return 库存车辆列表
     */
    List<Vehicle> selectCurrentInventory();

    /**
     * 根据VIN码更新车辆状态
     * @param vin VIN码
     * @param status 新状态
     * @return 更新数量
     */
    int updateStatusByVin(String vin, String status);

    /**
     * 根据状态查询车辆列表
     * @param status 车辆状态
     * @return 车辆列表
     */
    List<Vehicle> selectByStatus(String status);

    /**
     * 检查VIN码是否已存在
     * @param vin VIN码
     * @param excludeVin 排除的VIN码（用于更新时检查）
     * @return 是否已存在
     */
    boolean checkVinExists(String vin, String excludeVin);

    /**
     * 检查车辆是否可售
     * @param vin VIN码
     * @return 是否可售
     */
    boolean checkVehicleAvailable(String vin);
}
package com.carsale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carsale.entity.Vehicle;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map; // 【新增】

public interface VehicleService extends IService<Vehicle> {
    IPage<Vehicle> selectPageWithDetails(Page<Vehicle> page, String vin, String status, Integer modelId);
    List<Vehicle> selectByModelId(Integer modelId);
    List<Vehicle> selectCurrentInventory();
    int updateStatusByVin(String vin, String status);
    List<Vehicle> selectByStatus(String status);
    boolean checkVinExists(String vin, String excludeVin);

    // 【新增】获取仪表盘统计数据
    Map<String, Object> getDashboardStats();
    
    // 【新增】获取可售车辆列表（在库状态）
    List<Vehicle> selectAvailableVehicles();
}
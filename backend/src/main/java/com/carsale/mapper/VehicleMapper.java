package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.Vehicle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map; // 【新增】导入Map

@Mapper
public interface VehicleMapper extends BaseMapper<Vehicle> {

    // 原有的方法
    IPage<Vehicle> selectPageWithDetails(Page<Vehicle> page, @Param("vin") String vin, @Param("status") String status, @Param("modelId") Integer modelId);

    List<Vehicle> selectByModelId(Integer modelId);

    List<Vehicle> selectCurrentInventory();

    int updateStatusByVin(@Param("vin") String vin, @Param("status") String status);

    /**
     * 【新增】查询仪表盘统计数据
     * @return 包含各项统计指标的Map
     */
    Map<String, Object> selectDashboardStats();

    /**
     * 【新增】获取可售车辆列表（在库状态）
     * @return 可售车辆列表
     */
    List<Vehicle> selectAvailableVehicles();
}
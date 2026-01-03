package com.carsale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carsale.entity.Vehicle;
import com.carsale.mapper.VehicleMapper;
import com.carsale.service.VehicleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map; // 【新增】

@Service
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {

    @Override
    public IPage<Vehicle> selectPageWithDetails(Page<Vehicle> page, String vin, String status, Integer modelId) {
        return this.baseMapper.selectPageWithDetails(page, vin, status, modelId);
    }

    @Override
    public List<Vehicle> selectByModelId(Integer modelId) {
        return this.baseMapper.selectByModelId(modelId);
    }

    @Override
    public List<Vehicle> selectCurrentInventory() {
        return this.baseMapper.selectCurrentInventory();
    }

    @Override
    public int updateStatusByVin(String vin, String status) {
        return this.baseMapper.updateStatusByVin(vin, status);
    }

    @Override
    public List<Vehicle> selectByStatus(String status) {
        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vehicle::getStatus, status);
        wrapper.orderByDesc(Vehicle::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public boolean checkVinExists(String vin, String excludeVin) {
        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vehicle::getVin, vin);
        if (StringUtils.isNotBlank(excludeVin)) {
            wrapper.ne(Vehicle::getVin, excludeVin);
        }
        return this.count(wrapper) > 0;
    }

    // 【新增】实现统计方法
    @Override
    public Map<String, Object> getDashboardStats() {
        return this.baseMapper.selectDashboardStats();
    }

    // 【新增】获取可售车辆列表（在库状态且未被预订）
    @Override
    public List<Vehicle> selectAvailableVehicles() {
        return this.baseMapper.selectAvailableVehicles();
    }
}
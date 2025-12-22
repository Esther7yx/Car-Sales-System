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

/**
 * 车辆信息表Service实现类
 * 实现VehicleService接口，提供具体的业务逻辑实现
 */
@Service
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {

    /**
     * 分页查询车辆信息，包含车型和厂商关联
     * @param page 分页参数
     * @param vin VIN码模糊查询
     * @param status 车辆状态查询
     * @param modelId 车型ID查询
     * @return 分页结果
     */
    @Override
    public IPage<Vehicle> selectPageWithDetails(Page<Vehicle> page, String vin, String status, Integer modelId) {
        // 调用Mapper层的自定义分页查询方法
        return this.baseMapper.selectPageWithDetails(page, vin, status, modelId);
    }

    /**
     * 根据车型ID查询车辆列表
     * @param modelId 车型ID
     * @return 车辆列表
     */
    @Override
    public List<Vehicle> selectByModelId(Integer modelId) {
        // 调用Mapper层的自定义查询方法
        return this.baseMapper.selectByModelId(modelId);
    }

    /**
     * 查询当前库存车辆（在库状态）
     * @return 库存车辆列表
     */
    @Override
    public List<Vehicle> selectCurrentInventory() {
        // 调用Mapper层的自定义查询方法
        return this.baseMapper.selectCurrentInventory();
    }

    /**
     * 根据VIN码更新车辆状态
     * @param vin VIN码
     * @param status 新状态
     * @return 更新数量
     */
    @Override
    public int updateStatusByVin(String vin, String status) {
        // 调用Mapper层的自定义更新方法
        return this.baseMapper.updateStatusByVin(vin, status);
    }

    /**
     * 根据状态查询车辆列表
     * @param status 车辆状态
     * @return 车辆列表
     */
    @Override
    public List<Vehicle> selectByStatus(String status) {
        // 创建查询条件包装器
        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<>();
        
        // 添加状态查询条件
        wrapper.eq(Vehicle::getStatus, status);
        
        // 添加排序条件
        wrapper.orderByDesc(Vehicle::getCreatedAt);
        
        // 执行查询
        return this.list(wrapper);
    }

    /**
     * 检查VIN码是否已存在
     * @param vin VIN码
     * @param excludeVin 排除的VIN码（用于更新时检查）
     * @return 是否已存在
     */
    @Override
    public boolean checkVinExists(String vin, String excludeVin) {
        // 创建查询条件包装器
        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<>();
        
        // 添加VIN码查询条件
        wrapper.eq(Vehicle::getVin, vin);
        
        // 如果是更新操作，排除当前记录
        if (StringUtils.isNotBlank(excludeVin)) {
            wrapper.ne(Vehicle::getVin, excludeVin);
        }
        
        // 执行查询
        return this.count(wrapper) > 0;
    }

    /**
     * 检查车辆是否可售
     * @param vin VIN码
     * @return 是否可售
     */
    @Override
    public boolean checkVehicleAvailable(String vin) {
        // 创建查询条件包装器
        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<>();
        
        // 查询VIN码且状态为in_stock的车辆
        wrapper.eq(Vehicle::getVin, vin)
               .eq(Vehicle::getStatus, "in_stock");
        
        // 执行查询
        return this.count(wrapper) > 0;
    }
}
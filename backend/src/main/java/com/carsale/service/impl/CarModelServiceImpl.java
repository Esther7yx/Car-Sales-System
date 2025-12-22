package com.carsale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carsale.entity.CarModel;
import com.carsale.mapper.CarModelMapper;
import com.carsale.service.CarModelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 车型信息表Service实现类
 * 实现CarModelService接口，提供具体的业务逻辑实现
 */
@Service
public class CarModelServiceImpl extends ServiceImpl<CarModelMapper, CarModel> implements CarModelService {

    /**
     * 分页查询车型信息，包含厂商关联
     * @param page 分页参数
     * @param modelName 车型名称模糊查询
     * @param year 年份查询
     * @return 分页结果
     */
    @Override
    public IPage<CarModel> selectPageWithManufacturer(Page<CarModel> page, String modelName, Integer year) {
        // 调用Mapper层的自定义分页查询方法
        return this.baseMapper.selectPageWithManufacturer(page, modelName, year);
    }

    /**
     * 根据厂商ID查询车型列表
     * @param manufacturerId 厂商ID
     * @return 车型列表
     */
    @Override
    public List<CarModel> selectByManufacturerId(Integer manufacturerId) {
        // 创建查询条件包装器
        LambdaQueryWrapper<CarModel> wrapper = new LambdaQueryWrapper<>();
        
        // 添加厂商ID查询条件
        wrapper.eq(CarModel::getManufacturerId, manufacturerId);
        
        // 添加排序条件
        wrapper.orderByAsc(CarModel::getModelName);
        
        // 执行查询
        return this.list(wrapper);
    }

    /**
     * 检查车型名称在同一厂商下是否已存在
     * @param manufacturerId 厂商ID
     * @param modelName 车型名称
     * @param excludeId 排除的车型ID（用于更新时检查）
     * @return 是否已存在
     */
    @Override
    public boolean checkModelNameExists(Integer manufacturerId, String modelName, Integer excludeId) {
        // 创建查询条件包装器
        LambdaQueryWrapper<CarModel> wrapper = new LambdaQueryWrapper<>();
        
        // 添加厂商ID和车型名称查询条件
        wrapper.eq(CarModel::getManufacturerId, manufacturerId)
               .eq(CarModel::getModelName, modelName);
        
        // 如果是更新操作，排除当前记录
        if (excludeId != null) {
            wrapper.ne(CarModel::getModelId, excludeId);
        }
        
        // 执行查询
        return this.count(wrapper) > 0;
    }
}
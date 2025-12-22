package com.carsale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carsale.entity.Manufacturer;
import com.carsale.mapper.ManufacturerMapper;
import com.carsale.service.ManufacturerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 厂商信息表Service实现类
 * 实现ManufacturerService接口，提供具体的业务逻辑实现
 */
@Service
public class ManufacturerServiceImpl extends ServiceImpl<ManufacturerMapper, Manufacturer> implements ManufacturerService {

    /**
     * 分页查询厂商信息
     * @param page 分页参数
     * @param manufacturerName 厂商名称模糊查询
     * @param cooperationStatus 合作状态查询
     * @return 分页结果
     */
    @Override
    public IPage<Manufacturer> selectPage(Page<Manufacturer> page, String manufacturerName, String cooperationStatus) {
        // 创建查询条件包装器
        LambdaQueryWrapper<Manufacturer> wrapper = new LambdaQueryWrapper<>();
        
        // 添加厂商名称模糊查询条件
        if (StringUtils.isNotBlank(manufacturerName)) {
            wrapper.like(Manufacturer::getManufacturerName, manufacturerName);
        }
        
        // 添加合作状态查询条件
        if (StringUtils.isNotBlank(cooperationStatus)) {
            wrapper.eq(Manufacturer::getCooperationStatus, cooperationStatus);
        }
        
        // 添加排序条件
        wrapper.orderByDesc(Manufacturer::getCreatedAt);
        
        // 执行分页查询
        return this.page(page, wrapper);
    }

    /**
     * 根据合作状态查询厂商列表
     * @param cooperationStatus 合作状态
     * @return 厂商列表
     */
    @Override
    public List<Manufacturer> selectByStatus(String cooperationStatus) {
        // 创建查询条件包装器
        LambdaQueryWrapper<Manufacturer> wrapper = new LambdaQueryWrapper<>();
        
        // 添加合作状态查询条件
        wrapper.eq(Manufacturer::getCooperationStatus, cooperationStatus);
        
        // 添加排序条件
        wrapper.orderByAsc(Manufacturer::getManufacturerName);
        
        // 执行查询
        return this.list(wrapper);
    }

    /**
     * 检查厂商名称是否已存在
     * @param manufacturerName 厂商名称
     * @param excludeId 排除的厂商ID（用于更新时检查）
     * @return 是否已存在
     */
    @Override
    public boolean checkManufacturerNameExists(String manufacturerName, Integer excludeId) {
        // 创建查询条件包装器
        LambdaQueryWrapper<Manufacturer> wrapper = new LambdaQueryWrapper<>();
        
        // 添加厂商名称查询条件
        wrapper.eq(Manufacturer::getManufacturerName, manufacturerName);
        
        // 如果是更新操作，排除当前记录
        if (excludeId != null) {
            wrapper.ne(Manufacturer::getManufacturerId, excludeId);
        }
        
        // 执行查询
        return this.count(wrapper) > 0;
    }
}

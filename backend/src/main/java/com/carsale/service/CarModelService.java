package com.carsale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carsale.entity.CarModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 车型信息表Service接口
 * 继承MyBatis Plus的IService，提供基础的业务逻辑操作
 */
public interface CarModelService extends IService<CarModel> {
    /**
     * 分页查询车型信息，包含厂商关联
     * @param page 分页参数
     * @param modelName 车型名称模糊查询
     * @param year 年份查询
     * @return 分页结果
     */
    IPage<CarModel> selectPageWithManufacturer(Page<CarModel> page, String modelName, Integer year);

    /**
     * 根据厂商ID查询车型列表
     * @param manufacturerId 厂商ID
     * @return 车型列表
     */
    List<CarModel> selectByManufacturerId(Integer manufacturerId);

    /**
     * 检查车型名称在同一厂商下是否已存在
     * @param manufacturerId 厂商ID
     * @param modelName 车型名称
     * @param excludeId 排除的车型ID（用于更新时检查）
     * @return 是否已存在
     */
    boolean checkModelNameExists(Integer manufacturerId, String modelName, Integer excludeId);
}
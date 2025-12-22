package com.carsale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carsale.entity.Manufacturer;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 厂商信息表Service接口
 * 继承MyBatis Plus的IService，提供基础的业务逻辑操作
 */
public interface ManufacturerService extends IService<Manufacturer> {
    /**
     * 分页查询厂商信息
     * @param page 分页参数
     * @param manufacturerName 厂商名称模糊查询
     * @param cooperationStatus 合作状态查询
     * @return 分页结果
     */
    IPage<Manufacturer> selectPage(Page<Manufacturer> page, String manufacturerName, String cooperationStatus);

    /**
     * 根据合作状态查询厂商列表
     * @param cooperationStatus 合作状态
     * @return 厂商列表
     */
    List<Manufacturer> selectByStatus(String cooperationStatus);

    /**
     * 检查厂商名称是否已存在
     * @param manufacturerName 厂商名称
     * @param excludeId 排除的厂商ID（用于更新时检查）
     * @return 是否已存在
     */
    boolean checkManufacturerNameExists(String manufacturerName, Integer excludeId);
}

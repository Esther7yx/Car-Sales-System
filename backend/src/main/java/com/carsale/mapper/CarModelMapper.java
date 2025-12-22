package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.CarModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车型信息表Mapper接口
 * 继承MyBatis Plus的BaseMapper，提供基础的CRUD操作
 */
public interface CarModelMapper extends BaseMapper<CarModel> {
    /**
     * 根据厂商ID查询车型列表
     * @param manufacturerId 厂商ID
     * @return 车型列表
     */
    List<CarModel> selectByManufacturerId(@Param("manufacturerId") Integer manufacturerId);

    /**
     * 分页查询车型信息，包含厂商关联
     * @param page 分页参数
     * @param modelName 车型名称模糊查询
     * @param year 年份查询
     * @return 分页结果
     */
    IPage<CarModel> selectPageWithManufacturer(Page<CarModel> page, 
                                               @Param("modelName") String modelName, 
                                               @Param("year") Integer year);
}

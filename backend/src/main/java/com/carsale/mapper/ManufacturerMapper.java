package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carsale.entity.Manufacturer;

/**
 * 厂商信息表Mapper接口
 * 继承MyBatis Plus的BaseMapper，提供基础的CRUD操作
 */
public interface ManufacturerMapper extends BaseMapper<Manufacturer> {
    // 可以在此添加自定义的查询方法
}

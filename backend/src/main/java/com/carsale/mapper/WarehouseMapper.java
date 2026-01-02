package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carsale.entity.Warehouse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息表Mapper接口
 */
@Mapper
public interface WarehouseMapper extends BaseMapper<Warehouse> {
}
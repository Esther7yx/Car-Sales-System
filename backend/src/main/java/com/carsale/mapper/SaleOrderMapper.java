package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carsale.entity.SaleOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售订单Mapper接口
 */
@Mapper
public interface SaleOrderMapper extends BaseMapper<SaleOrder> {
}
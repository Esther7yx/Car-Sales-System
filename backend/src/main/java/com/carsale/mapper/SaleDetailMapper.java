package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carsale.entity.SaleDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售明细Mapper接口
 */
@Mapper
public interface SaleDetailMapper extends BaseMapper<SaleDetail> {
}
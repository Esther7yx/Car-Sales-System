package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carsale.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户信息Mapper接口
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
package com.carsale.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface SaleOrderMapper {
    // 调用存储过程 process_sale_order
    void createSaleOrder(Map<String, Object> params);
}
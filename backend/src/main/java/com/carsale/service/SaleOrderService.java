package com.carsale.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.carsale.entity.SaleOrder;
import com.carsale.utils.Result;
import java.util.List;
import java.util.Map;

public interface SaleOrderService {
    // 获取销售订单列表
    Result<IPage<SaleOrder>> getList(Integer current, Integer size, String orderNumber);

    // 获取订单明细
    Result<List<Map<String, Object>>> getDetails(Integer saleId);

    // 创建销售订单 (核心事务方法)
    Result<Map<String, Object>> create(Map<String, Object> params);
}
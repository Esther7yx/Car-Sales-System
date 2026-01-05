package com.carsale.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.carsale.entity.PurchaseOrder;
import com.carsale.utils.Result;
import java.util.List;
import java.util.Map;

public interface PurchaseOrderService {
    Result<IPage<PurchaseOrder>> getList(Integer current, Integer size, String status);
    Result<List<Map<String, Object>>> getDetails(Integer orderId);
    Result<Map<String, Object>> create(Map<String, Object> params);
    Result<String> receiveBatch(Map<String, Object> params);

    // 【新增】撤销采购单
    Result<String> cancel(Integer id);
}
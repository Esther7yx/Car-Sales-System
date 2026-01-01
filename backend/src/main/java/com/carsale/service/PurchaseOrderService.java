package com.carsale.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.carsale.entity.PurchaseOrder;
import com.carsale.utils.Result;
import java.util.List;
import java.util.Map;

public interface PurchaseOrderService {
    // 分页获取采购单列表
    Result<IPage<PurchaseOrder>> getList(Integer current, Integer size, String status);

    // 获取某个采购单的明细（买了哪些车）
    Result<List<Map<String, Object>>> getDetails(Integer orderId);

    // 创建采购单
    Result<Map<String, Object>> create(Map<String, Object> params);

    // 批量入库（收货）
    Result<String> receiveBatch(Map<String, Object> params);
}
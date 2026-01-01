package com.carsale.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.PurchaseOrder;
import com.carsale.mapper.PurchaseOrderMapper;
import com.carsale.service.PurchaseOrderService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public Result<IPage<PurchaseOrder>> getList(Integer current, Integer size, String status) {
        Page<PurchaseOrder> page = new Page<>(current, size);
        IPage<PurchaseOrder> resultPage = purchaseOrderMapper.selectOrderPage(page, status);
        return Result.success(resultPage);
    }

    @Override
    public Result<List<Map<String, Object>>> getDetails(Integer orderId) {
        List<Map<String, Object>> details = purchaseOrderMapper.selectOrderDetails(orderId);
        return Result.success(details);
    }

    @Override
    @Transactional
    public Result<Map<String, Object>> create(Map<String, Object> params) {
        try {
            // 1. 接收存储过程返回的结果（包含 order_id 和 order_number）
            Map<String, Object> result = purchaseOrderMapper.createPurchaseOrder(params);

            // 2. 将结果返回给前端
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            return Result.error("创建采购单失败: " + msg);
        }
    }

    @Override
    @Transactional
    public Result<String> receiveBatch(Map<String, Object> params) {
        try {
            purchaseOrderMapper.receivePurchaseBatch(params);
            return Result.success("入库成功");
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            return Result.error("入库失败: " + msg);
        }
    }
}
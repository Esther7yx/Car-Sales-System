package com.carsale.service;

import com.carsale.utils.Result;
import java.util.Map;

public interface SaleOrderService {
    // 接收参数：customerId, paymentMethod, saleItems (JSON列表)
    Result<Map<String, Object>> createOrder(Map<String, Object> params);
}
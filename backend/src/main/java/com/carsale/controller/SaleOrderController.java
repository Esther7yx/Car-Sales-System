package com.carsale.controller;

import com.carsale.service.SaleOrderService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SaleOrderController {

    @Autowired
    private SaleOrderService saleOrderService;

    @PostMapping("/create")
    public Result<Map<String, Object>> createSaleOrder(@RequestBody Map<String, Object> params) {
        // 为了确保 operatorId 存在，如果前端没传，默认设为 1 (Admin)
        if (!params.containsKey("operatorId")) {
            params.put("operatorId", 1);
        }

        // 必须确保 saleItemsJson 是 JSON 字符串格式（如果你前端传的是对象，这里可能需要转换）
        // 在 MyBatis 调用存储过程时，#{saleItemsJson} 需要对应 JSON 类型
        // 如果前端传的是 List 对象，建议在这里用 Jackson 转成 JSON String
        // 但为了简单，我们假设前端直接传好了格式，或者 MyBatis TypeHandler 能处理

        return saleOrderService.createOrder(params);
    }
}
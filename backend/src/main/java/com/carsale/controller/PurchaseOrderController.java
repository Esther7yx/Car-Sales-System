package com.carsale.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.carsale.entity.PurchaseOrder;
import com.carsale.service.PurchaseOrderService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping
    public Result<IPage<PurchaseOrder>> getList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        return purchaseOrderService.getList(current, size, status);
    }

    @GetMapping("/{id}/details")
    public Result<List<Map<String, Object>>> getDetails(@PathVariable Integer id) {
        return purchaseOrderService.getDetails(id);
    }

    @PostMapping("/create")
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        if (!params.containsKey("operatorId")) {
            params.put("operatorId", 1);
        }
        return purchaseOrderService.create(params);
    }

    @PostMapping("/{id}/receive")
    public Result<String> receive(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        params.put("orderId", id);

        // 【修改】确保操作员ID正确，防止外键报错
        if (!params.containsKey("operatorId")) {
            params.put("operatorId", 1);
        }

        // 【修改】直接移除 JSON 转换逻辑，Service 层直接接收 Map/List
        return purchaseOrderService.receiveBatch(params);
    }

    @PostMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Integer id) {
        return purchaseOrderService.cancel(id);
    }
}
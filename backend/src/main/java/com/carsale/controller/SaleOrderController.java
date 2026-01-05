package com.carsale.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.carsale.entity.SaleOrder;
import com.carsale.service.SaleOrderService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sale-orders")
public class SaleOrderController {

    @Autowired
    private SaleOrderService saleOrderService;

    // 查询销售列表
    @GetMapping
    public Result<IPage<SaleOrder>> getList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNumber) {
        return saleOrderService.getList(current, size, orderNumber);
    }

    // 查询详情
    @GetMapping("/{id}/details")
    public Result<List<Map<String, Object>>> getDetails(@PathVariable Integer id) {
        return saleOrderService.getDetails(id);
    }

    // 创建销售单
    @PostMapping("/create")
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {

        if (!params.containsKey("operatorId")) {
            params.put("operatorId", 1);
        }
        return saleOrderService.create(params);
    }
}
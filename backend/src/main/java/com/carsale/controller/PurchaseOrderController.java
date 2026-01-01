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

    // 获取列表
    @GetMapping
    public Result<IPage<PurchaseOrder>> getList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        return purchaseOrderService.getList(current, size, status);
    }

    // 获取详情
    @GetMapping("/{id}/details")
    public Result<List<Map<String, Object>>> getDetails(@PathVariable Integer id) {
        return purchaseOrderService.getDetails(id);
    }

    // 创建采购单
    @PostMapping("/create")
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        // 简单模拟当前操作员ID (实际应从Token获取)
        if (!params.containsKey("operatorId")) {
            params.put("operatorId", 1);
        }

        // 前端传来的 items 数组，MyBatis调用存储过程时需要转为 JSON 字符串
        // 但如果使用 MyBatis 的 TypeHandler，Map 中的 List 可以自动转。
        // 为了稳妥，我们建议前端传参时直接传 JSON 对象，或者由后端 Jackson 转换。
        // 这里假设前端传的是标准 JSON 对象结构，Spring Boot 会自动解析为 List/Map。
        // 而 MyBatis 调用存储过程时，#{itemsJson} 需要是 JSON 字符串格式。

        // 转换 items 为 JSON String (粗略处理，依赖前端传参格式)
        // 注意：如果你在 Mapper XML 里配置了 jdbcType=OTHER 或 JSON TypeHandler，这步可能由框架处理。
        // 为了兼容最基础的配置，我们在 Service/Mapper 接口参数定义的是 Map<String, Object>
        // MyBatis 在调用存储过程时，通常需要手动将 List 转为 String 传给 JSON 参数。

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            if (params.get("items") != null) {
                String json = mapper.writeValueAsString(params.get("items"));
                params.put("itemsJson", json); // 对应 Mapper XML 中的 #{itemsJson}
            }
        } catch (Exception e) {
            return Result.error("参数解析错误");
        }

        return purchaseOrderService.create(params);
    }

    // 确认收货（批量入库）
    @PostMapping("/{id}/receive")
    public Result<String> receive(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        params.put("orderId", id);
        if (!params.containsKey("operatorId")) {
            params.put("operatorId", 1);
        }

        // 同样处理 JSON 参数转换
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            if (params.get("vehicleList") != null) {
                String json = mapper.writeValueAsString(params.get("vehicleList"));
                params.put("vehicleListJson", json); // 对应 Mapper XML 中的 #{vehicleListJson}
            }
        } catch (Exception e) {
            return Result.error("参数解析错误");
        }

        return purchaseOrderService.receiveBatch(params);
    }
}
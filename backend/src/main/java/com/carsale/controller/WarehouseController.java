package com.carsale.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.carsale.annotation.RequiresRole;
import com.carsale.entity.InventorySummary;
import com.carsale.entity.InvoicingStats;
import com.carsale.entity.WarehouseDetail;
import com.carsale.service.WarehouseService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@RequiresRole({"admin", "manager", "warehouse"})
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    // 1. 获取库存车辆统计 (每种车型的剩余数量)
    @GetMapping("/inventory-summary")
    public Result<IPage<InventorySummary>> getInventorySummary(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<InventorySummary> result = warehouseService.getInventorySummary(page, size);
        return Result.success(result);
    }

    // 2. 获取仓库明细 (具体到每一辆车)
    @GetMapping("/details")
    public Result<IPage<WarehouseDetail>> getWarehouseDetails(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<WarehouseDetail> result = warehouseService.getWarehouseDetails(page, size, keyword);
        return Result.success(result);
    }

    // 3. 获取进销存统计 (图表数据)
    @GetMapping("/stats")
    public Result<List<InvoicingStats>> getInvoicingStats() {
        List<InvoicingStats> stats = warehouseService.getInvoicingStats();
        return Result.success(stats);
    }
}
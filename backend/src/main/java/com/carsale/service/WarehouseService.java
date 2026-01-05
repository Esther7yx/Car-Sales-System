package com.carsale.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.carsale.entity.InventorySummary;
import com.carsale.entity.InvoicingStats;
import com.carsale.entity.WarehouseDetail;

import java.util.List;

public interface WarehouseService {
    IPage<InventorySummary> getInventorySummary(int page, int size);
    IPage<WarehouseDetail> getWarehouseDetails(int page, int size, String keyword);
    List<InvoicingStats> getInvoicingStats();
}
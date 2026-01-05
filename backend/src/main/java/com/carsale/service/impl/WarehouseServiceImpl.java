package com.carsale.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.mapper.WarehouseMapper;
import com.carsale.service.WarehouseService;
import com.carsale.entity.InventorySummary;
import com.carsale.entity.InvoicingStats;
import com.carsale.entity.WarehouseDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Override
    public IPage<InventorySummary> getInventorySummary(int page, int size) {
        Page<InventorySummary> pageParam = new Page<>(page, size);
        return warehouseMapper.selectInventorySummaryPage(pageParam);
    }

    @Override
    public IPage<WarehouseDetail> getWarehouseDetails(int page, int size, String keyword) {
        Page<WarehouseDetail> pageParam = new Page<>(page, size);
        return warehouseMapper.selectWarehouseDetailsPage(pageParam, keyword);
    }

    @Override
    public List<InvoicingStats> getInvoicingStats() {
        return warehouseMapper.selectInvoicingStats();
    }
}
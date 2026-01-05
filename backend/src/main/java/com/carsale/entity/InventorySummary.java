package com.carsale.entity;

import java.math.BigDecimal;

/**
 * 库存车辆统计实体类
 * 对应视图: view_inventory_summary
 */
public class InventorySummary {
    private Integer modelId;
    private String modelName;
    private Integer year;
    private String manufacturerName;
    private BigDecimal guidePrice;
    private Integer stockQuantity;
    private BigDecimal totalStockValue;

    // Getters and Setters
    public Integer getModelId() { return modelId; }
    public void setModelId(Integer modelId) { this.modelId = modelId; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }

    public BigDecimal getGuidePrice() { return guidePrice; }
    public void setGuidePrice(BigDecimal guidePrice) { this.guidePrice = guidePrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public BigDecimal getTotalStockValue() { return totalStockValue; }
    public void setTotalStockValue(BigDecimal totalStockValue) { this.totalStockValue = totalStockValue; }
}
package com.carsale.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 仓库明细实体类
 * 对应视图: view_warehouse_details
 */
public class WarehouseDetail {
    private String vin;
    private String modelName;
    private String manufacturerName;
    private String status;
    private BigDecimal purchasePrice;
    private String warehouseLocation; // 库位备注
    private LocalDateTime entryDate;
    private Integer daysInStock;

    // Getters and Setters
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }

    public LocalDateTime getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDateTime entryDate) { this.entryDate = entryDate; }

    public Integer getDaysInStock() { return daysInStock; }
    public void setDaysInStock(Integer daysInStock) { this.daysInStock = daysInStock; }
}
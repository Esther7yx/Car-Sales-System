package com.carsale.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * 车辆信息表实体类
 * 对应数据库中的vehicle表，是系统的核心表
 */
@TableName("vehicle")
public class Vehicle {
    /**
     * VIN码（车架号），17位全球唯一，主键
     */
    @TableId(value = "vin", type = IdType.INPUT)
    private String vin;

    /**
     * 车型ID，外键关联car_model表
     */
    @TableField("model_id")
    private Integer modelId;

    /**
     * 采购价格
     */
    @TableField("purchase_price")
    private BigDecimal purchasePrice;

    /**
     * 销售价格，CHECK约束保证销售价≥采购价
     */
    @TableField("sale_price")
    private BigDecimal salePrice;

    /**
     * 车辆状态：in_stock(在库)、sold(已售)、reserved(预订)、in_transit(在途)
     */
    @TableField("status")
    private String status;

    /**
     * 仓库位置
     */
    @TableField("warehouse_location")
    private String warehouseLocation;

    /**
     * 采购日期
     */
    @TableField("purchase_date")
    private LocalDate purchaseDate;

    /**
     * 销售日期
     */
    @TableField("sale_date")
    private LocalDate saleDate;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 关联的车型信息（非数据库字段，用于查询时关联）
     */
    @TableField(exist = false)
    private CarModel carModel;

    /**
     * 关联的厂商信息（非数据库字段，用于查询时关联）
     */
    @TableField(exist = false)
    private Manufacturer manufacturer;

    // getter and setter methods
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }
}

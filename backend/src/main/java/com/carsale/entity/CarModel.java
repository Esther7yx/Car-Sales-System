package com.carsale.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 车型信息表实体类
 * 对应数据库中的car_model表
 */
@TableName("car_model")
public class CarModel {
    /**
     * 车型ID，主键，自增
     */
    @TableId(value = "model_id", type = IdType.AUTO)
    private Integer modelId;

    /**
     * 厂商ID，外键关联manufacturer表
     */
    @TableField("manufacturer_id")
    private Integer manufacturerId;

    /**
     * 车型名称
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 年份
     */
    @TableField("year")
    private Integer year;

    /**
     * 发动机类型
     */
    @TableField("engine_type")
    private String engineType;

    /**
     * 变速箱
     */
    @TableField("transmission")
    private String transmission;

    /**
     * 燃油类型
     */
    @TableField("fuel_type")
    private String fuelType;

    /**
     * 指导价格
     */
    @TableField("guide_price")
    private BigDecimal guidePrice;

    /**
     * 配置特性，JSON格式存储
     */
    @TableField("features")
    private String features;

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
     * 关联的厂商信息（非数据库字段，用于查询时关联）
     */
    @TableField(exist = false)
    private Manufacturer manufacturer;

    // getter and setter methods
    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public Integer getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Integer manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public BigDecimal getGuidePrice() {
        return guidePrice;
    }

    public void setGuidePrice(BigDecimal guidePrice) {
        this.guidePrice = guidePrice;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
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

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }
}

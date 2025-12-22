package com.carsale.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 厂商信息表实体类
 * 对应数据库中的manufacturer表
 */
@TableName("manufacturer")
public class Manufacturer {
    /**
     * 厂商ID，主键，自增
     */
    @TableId(value = "manufacturer_id", type = IdType.AUTO)
    private Integer manufacturerId;

    /**
     * 厂商名称，唯一约束
     */
    @TableField("manufacturer_name")
    private String manufacturerName;

    /**
     * 联系人
     */
    @TableField("contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 合作状态：active(活跃)、inactive(不活跃)、suspended(暂停)
     */
    @TableField("cooperation_status")
    private String cooperationStatus;

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

    // getter and setter methods
    public Integer getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Integer manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCooperationStatus() {
        return cooperationStatus;
    }

    public void setCooperationStatus(String cooperationStatus) {
        this.cooperationStatus = cooperationStatus;
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
}

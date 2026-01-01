package com.carsale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("purchase_order")
public class PurchaseOrder {
    @TableId(type = IdType.AUTO)
    private Integer orderId;
    private String orderNumber;
    private Integer manufacturerId;
    private Integer operatorId;
    private BigDecimal totalAmount;
    private String status; // pending, approved, received, cancelled
    private LocalDateTime createTime;
    private LocalDateTime receiveTime;

    // --- 扩展字段 (用于前端展示) ---
    @TableField(exist = false)
    private String manufacturerName;

    @TableField(exist = false)
    private String operatorName;

    // --- Getters and Setters ---
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public Integer getManufacturerId() { return manufacturerId; }
    public void setManufacturerId(Integer manufacturerId) { this.manufacturerId = manufacturerId; }
    public Integer getOperatorId() { return operatorId; }
    public void setOperatorId(Integer operatorId) { this.operatorId = operatorId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getReceiveTime() { return receiveTime; }
    public void setReceiveTime(LocalDateTime receiveTime) { this.receiveTime = receiveTime; }
    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
}
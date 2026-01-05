package com.carsale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售订单实体类
 */
@TableName("sale_order")
public class SaleOrder {
    /**
     * 销售单ID
     */
    @TableId(type = IdType.AUTO)
    private Integer saleId;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 客户ID
     */
    private Integer customerId;

    /**
     * 操作员ID
     */
    private Integer operatorId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 支付方式 (cash, loan, installment)
     */
    private String paymentMethod;

    /**
     * 状态 (pending, paid, delivered, cancelled)
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 交付时间
     */
    private LocalDateTime deliveryTime;

    // ---------------------------------------------------------
    // 以下为非数据库字段 (用于列表展示)，必须加 @TableField(exist = false)
    // ---------------------------------------------------------

    /**
     * 客户姓名
     */
    @TableField(exist = false)
    private String customerName;

    /**
     * 客户电话
     */
    @TableField(exist = false)
    private String customerPhone;

    /**
     * 操作员姓名
     */
    @TableField(exist = false)
    private String operatorName;

    // ---------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
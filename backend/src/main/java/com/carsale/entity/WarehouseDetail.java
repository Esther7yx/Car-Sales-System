package com.carsale.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 仓库明细表实体类
 * 对应数据库中的warehouse_detail表，记录库存变动历史
 */
@TableName("warehouse_detail")
public class WarehouseDetail {
    /**
     * 明细ID，主键
     */
    @TableId(value = "detail_id", type = IdType.AUTO)
    private Integer detailId;

    /**
     * 仓库ID，外键关联warehouse表
     */
    @TableField("warehouse_id")
    private Integer warehouseId;

    /**
     * VIN码，外键关联vehicle表
     */
    @TableField("vin")
    private String vin;

    /**
     * 操作类型：in(入库)、out(出库)、transfer(调拨)
     */
    @TableField("operation_type")
    private String operationType;

    /**
     * 操作数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 操作前库存
     */
    @TableField("before_stock")
    private Integer beforeStock;

    /**
     * 操作后库存
     */
    @TableField("after_stock")
    private Integer afterStock;

    /**
     * 操作金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 操作员ID
     */
    @TableField("operator_id")
    private Integer operatorId;

    /**
     * 相关订单号（采购订单号或销售订单号）
     */
    @TableField("related_order")
    private String relatedOrder;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time", fill = FieldFill.INSERT)
    private LocalDateTime operationTime;

    // getter and setter methods
    public Integer getDetailId() {
        return detailId;
    }

    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getBeforeStock() {
        return beforeStock;
    }

    public void setBeforeStock(Integer beforeStock) {
        this.beforeStock = beforeStock;
    }

    public Integer getAfterStock() {
        return afterStock;
    }

    public void setAfterStock(Integer afterStock) {
        this.afterStock = afterStock;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getRelatedOrder() {
        return relatedOrder;
    }

    public void setRelatedOrder(String relatedOrder) {
        this.relatedOrder = relatedOrder;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }
}
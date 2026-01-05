package com.carsale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

@TableName("sale_detail")
public class SaleDetail {
    @TableId(type = IdType.AUTO)
    private Integer detailId;
    private Integer saleId;
    private String vin;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    // 【新增】操作员ID字段
    private Integer operatorId;

    // Getters and Setters
    public Integer getDetailId() { return detailId; }
    public void setDetailId(Integer detailId) { this.detailId = detailId; }

    public Integer getSaleId() { return saleId; }
    public void setSaleId(Integer saleId) { this.saleId = saleId; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    // 【新增】Getter and Setter
    public Integer getOperatorId() { return operatorId; }
    public void setOperatorId(Integer operatorId) { this.operatorId = operatorId; }
}
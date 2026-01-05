package com.carsale.entity;

import java.math.BigDecimal;

/**
 * 进销存统计实体类
 * 对应视图: view_invoicing_stats
 */
public class InvoicingStats {
    private String monthStr;
    private Integer purchaseCount;
    private BigDecimal purchaseAmount;
    private Integer salesCount;
    private BigDecimal salesAmount;

    // Getters and Setters
    public String getMonthStr() { return monthStr; }
    public void setMonthStr(String monthStr) { this.monthStr = monthStr; }

    public Integer getPurchaseCount() { return purchaseCount; }
    public void setPurchaseCount(Integer purchaseCount) { this.purchaseCount = purchaseCount; }

    public BigDecimal getPurchaseAmount() { return purchaseAmount; }
    public void setPurchaseAmount(BigDecimal purchaseAmount) { this.purchaseAmount = purchaseAmount; }

    public Integer getSalesCount() { return salesCount; }
    public void setSalesCount(Integer salesCount) { this.salesCount = salesCount; }

    public BigDecimal getSalesAmount() { return salesAmount; }
    public void setSalesAmount(BigDecimal salesAmount) { this.salesAmount = salesAmount; }
}
package com.carsale.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {

    // 获取核心指标 (直接查视图8)
    @Select("SELECT * FROM view_dashboard_summary")
    Map<String, Object> getSummary();

    // 获取库存分布 (直接查视图6)
    @Select("SELECT * FROM view_stock_stats")
    List<Map<String, Object>> getStockStats();

    // 获取销售趋势 (直接查视图7)
    @Select("SELECT * FROM view_sales_trend")
    List<Map<String, Object>> getSalesTrend();
}
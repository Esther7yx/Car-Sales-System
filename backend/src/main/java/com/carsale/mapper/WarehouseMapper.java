package com.carsale.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.InventorySummary;
import com.carsale.entity.InvoicingStats;
import com.carsale.entity.WarehouseDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WarehouseMapper {

    // 1. 查询库存汇总 (分页)
    @Select("SELECT * FROM view_inventory_summary")
    IPage<InventorySummary> selectInventorySummaryPage(Page<?> page);

    // 2. 查询仓库明细 (分页 + 简单的搜索条件)
    // 这里使用动态SQL需要配合XML，为简单起见，这里先演示注解方式，
    // 如果需要复杂搜索，建议放入 XML 文件中。
    @Select("<script>" +
            "SELECT * FROM view_warehouse_details " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND (vin LIKE CONCAT('%', #{keyword}, '%') OR model_name LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "</where>" +
            "ORDER BY entry_date DESC" +
            "</script>")
    IPage<WarehouseDetail> selectWarehouseDetailsPage(Page<?> page, String keyword);

    // 3. 查询进销存统计
    @Select("SELECT * FROM view_invoicing_stats LIMIT 12")
    List<InvoicingStats> selectInvoicingStats();
}
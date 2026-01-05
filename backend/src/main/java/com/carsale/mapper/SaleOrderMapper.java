package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.SaleDetail;
import com.carsale.entity.SaleOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SaleOrderMapper extends BaseMapper<SaleOrder> {

    IPage<SaleOrder> selectOrderPage(Page<SaleOrder> page, @Param("orderNumber") String orderNumber);

    List<Map<String, Object>> selectOrderDetails(@Param("saleId") Integer saleId);

    // 【修改】SQL 语句中加入 operator_id
    @Insert("INSERT INTO sale_detail (sale_id, vin, unit_price, subtotal, operator_id) " +
            "VALUES (#{saleId}, #{vin}, #{unitPrice}, #{subtotal}, #{operatorId})")
    int insertDetail(SaleDetail detail);

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as month, SUM(total_amount) as total_sales " +
            "FROM sale_order WHERE status != 'cancelled' " +
            "GROUP BY month ORDER BY month DESC LIMIT 12")
    List<Map<String, Object>> selectMonthlySales();
}
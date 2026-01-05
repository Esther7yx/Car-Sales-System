package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.PurchaseOrder;
import com.carsale.entity.PurchaseOrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {

    IPage<PurchaseOrder> selectOrderPage(Page<PurchaseOrder> page, @Param("status") String status);

    List<Map<String, Object>> selectOrderDetails(@Param("orderId") Integer orderId);

    @Insert("INSERT INTO purchase_order_detail (order_id, model_id, quantity, unit_price, subtotal) " +
            "VALUES (#{orderId}, #{modelId}, #{quantity}, #{unitPrice}, #{subtotal})")
    int insertDetail(PurchaseOrderDetail detail);

    // 【修改】删除 receivePurchaseBatch 方法，因为逻辑已移至 Service 层
    // void receivePurchaseBatch(Map<String, Object> params);

    @Update("UPDATE purchase_order SET status = 'cancelled', cancel_time = NOW() WHERE order_id = #{orderId} AND status = 'pending'")
    int cancelOrder(@Param("orderId") Integer orderId);
}
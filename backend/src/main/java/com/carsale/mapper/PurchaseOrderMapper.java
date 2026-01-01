package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {

    // 分页查询采购单（关联查询厂商名）
    IPage<PurchaseOrder> selectOrderPage(Page<PurchaseOrder> page, @Param("status") String status);

    // 查询订单明细（返回 Map 列表，包含车型名称等信息）
    List<Map<String, Object>> selectOrderDetails(@Param("orderId") Integer orderId);

    // 调用存储过程：创建采购单
    // 调用存储过程：创建采购单，返回包含 order_id 和 order_number 的 Map
    Map<String, Object> createPurchaseOrder(Map<String, Object> params);

    // 调用存储过程：批量入库
    void receivePurchaseBatch(Map<String, Object> params);
}
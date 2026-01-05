package com.carsale.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.PurchaseOrder;
import com.carsale.entity.PurchaseOrderDetail;
import com.carsale.entity.Vehicle;
import com.carsale.mapper.PurchaseOrderMapper;
import com.carsale.mapper.VehicleMapper;
import com.carsale.service.PurchaseOrderService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public Result<IPage<PurchaseOrder>> getList(Integer current, Integer size, String status) {
        Page<PurchaseOrder> page = new Page<>(current, size);
        return Result.success(purchaseOrderMapper.selectOrderPage(page, status));
    }

    @Override
    public Result<List<Map<String, Object>>> getDetails(Integer orderId) {
        return Result.success(purchaseOrderMapper.selectOrderDetails(orderId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> create(Map<String, Object> params) {
        try {
            Integer manufacturerId = (Integer) params.get("manufacturerId");
            Integer operatorId = (Integer) params.get("operatorId");
            List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");

            if (items == null || items.isEmpty()) {
                return Result.error("采购明细不能为空");
            }

            // 1. 计算总金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Map<String, Object> item : items) {
                BigDecimal unitPrice = new BigDecimal(item.get("unit_price").toString());
                BigDecimal quantity = new BigDecimal(item.get("quantity").toString());
                totalAmount = totalAmount.add(unitPrice.multiply(quantity));
            }

            // 2. 生成订单号 (PO + 时间戳 + 随机数)
            String orderNumber = "PO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + String.format("%03d", new Random().nextInt(1000));

            // 3. 插入主表
            PurchaseOrder po = new PurchaseOrder();
            po.setOrderNumber(orderNumber);
            po.setManufacturerId(manufacturerId);
            po.setOperatorId(operatorId);
            po.setTotalAmount(totalAmount);
            po.setStatus("pending");
            po.setCreateTime(LocalDateTime.now());

            purchaseOrderMapper.insert(po);

            // 4. 插入明细表
            for (Map<String, Object> item : items) {
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setOrderId(po.getOrderId());
                detail.setModelId((Integer) item.get("model_id"));
                detail.setQuantity((Integer) item.get("quantity"));
                detail.setUnitPrice(new BigDecimal(item.get("unit_price").toString()));

                BigDecimal qty = new BigDecimal(item.get("quantity").toString());
                BigDecimal price = new BigDecimal(item.get("unit_price").toString());
                detail.setSubtotal(qty.multiply(price));

                purchaseOrderMapper.insertDetail(detail);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", po.getOrderId());
            result.put("orderNumber", po.getOrderNumber());

            return Result.success(result);

        } catch (Exception e) {
            e.printStackTrace();
            // 手动回滚，确保主从表一致性
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> receiveBatch(Map<String, Object> params) {
        try {
            Integer orderId = (Integer) params.get("orderId");
            List<Map<String, Object>> vehicleList = (List<Map<String, Object>>) params.get("vehicleList");

            if (vehicleList == null || vehicleList.isEmpty()) {
                return Result.error("入库车辆列表不能为空");
            }

            // 1. 检查订单状态
            PurchaseOrder order = purchaseOrderMapper.selectById(orderId);
            if (order == null) {
                return Result.error("采购单不存在");
            }
            if (!"pending".equals(order.getStatus())) {
                return Result.error("该订单状态不是待处理，无法入库");
            }

            // 2. 循环插入车辆 (先做最容易出错的步骤)
            for (Map<String, Object> item : vehicleList) {
                Vehicle v = new Vehicle();
                v.setVin((String) item.get("vin"));
                v.setModelId((Integer) item.get("model_id"));

                BigDecimal price = new BigDecimal(item.get("price").toString());
                v.setPurchasePrice(price);
                // 默认售价暂定等于进价，实际业务中可在入库时调整
                v.setSalePrice(price);

                v.setStatus("in_stock");
                v.setWarehouseLocation((String) item.get("location"));
                v.setPurchaseDate(LocalDate.now());

                vehicleMapper.insert(v);
            }

            // 3. 车辆全部插入成功后，再更新订单状态
            order.setStatus("received");
            purchaseOrderMapper.updateById(order);

            return Result.success("已同意并完成入库，库存已更新");

        } catch (Exception e) {
            e.printStackTrace();
            // 【关键修改】手动标记事务回滚！
            // 这样即使 catch 住了异常，Spring 也会回滚之前插入的所有车辆，防止出现脏数据。
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            if (msg != null && msg.contains("Duplicate entry")) {
                return Result.error("入库失败: 存在重复的VIN码，请刷新重试");
            }
            return Result.error("入库操作失败: " + msg);
        }
    }

    @Override
    public Result<String> cancel(Integer id) {
        int rows = purchaseOrderMapper.cancelOrder(id);
        if (rows > 0) {
            return Result.success("采购单已撤销");
        } else {
            return Result.error("撤销失败，订单状态可能已改变");
        }
    }
}
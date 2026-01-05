package com.carsale.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.SaleDetail;
import com.carsale.entity.SaleOrder;
import com.carsale.entity.Vehicle;
import com.carsale.mapper.SaleOrderMapper;
import com.carsale.mapper.VehicleMapper;
import com.carsale.service.SaleOrderService;
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
public class SaleOrderServiceImpl implements SaleOrderService {

    @Autowired
    private SaleOrderMapper saleOrderMapper;

    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public Result<IPage<SaleOrder>> getList(Integer current, Integer size, String orderNumber) {
        Page<SaleOrder> page = new Page<>(current, size);
        return Result.success(saleOrderMapper.selectOrderPage(page, orderNumber));
    }

    @Override
    public Result<List<Map<String, Object>>> getDetails(Integer saleId) {
        return Result.success(saleOrderMapper.selectOrderDetails(saleId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> create(Map<String, Object> params) {
        try {
            Integer customerId = (Integer) params.get("customerId");

            // 【修改点 1】获取 operatorId 并增加判空兜底
            Integer operatorId = null;
            if (params.get("operatorId") != null) {
                operatorId = Integer.parseInt(params.get("operatorId").toString());
            }

            if (operatorId == null) {
                operatorId = 1;
            }

            List<Map<String, Object>> vehicles = (List<Map<String, Object>>) params.get("vehicles");

            if (vehicles == null || vehicles.isEmpty()) {
                return Result.error("请选择至少一辆要销售的车辆");
            }

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (Map<String, Object> item : vehicles) {
                String vin = (String) item.get("vin");
                Vehicle v = vehicleMapper.selectById(vin);
                if (v == null) {
                    throw new RuntimeException("车辆 " + vin + " 不存在");
                }
                if (!"in_stock".equals(v.getStatus())) {
                    throw new RuntimeException("车辆 " + vin + " 不是在库状态，无法销售");
                }

                BigDecimal price = new BigDecimal(item.get("actualPrice").toString());
                totalAmount = totalAmount.add(price);
            }

            String orderNumber = "SO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + String.format("%03d", new Random().nextInt(1000));

            SaleOrder so = new SaleOrder();
            so.setOrderNumber(orderNumber);
            so.setCustomerId(customerId);

            // 【修改点 2】使用确保不为空的 operatorId
            so.setOperatorId(operatorId);

            so.setTotalAmount(totalAmount);
            so.setPaymentMethod("cash");
            so.setStatus("delivered");
            so.setCreateTime(LocalDateTime.now());
            so.setDeliveryTime(LocalDateTime.now());

            saleOrderMapper.insert(so);

            for (Map<String, Object> item : vehicles) {
                String vin = (String) item.get("vin");
                BigDecimal price = new BigDecimal(item.get("actualPrice").toString());

                SaleDetail detail = new SaleDetail();
                detail.setSaleId(so.getSaleId());
                detail.setVin(vin);
                detail.setUnitPrice(price);
                detail.setSubtotal(price);

                // 【修改点 3】设置 operatorId (数据库字段非空)
                detail.setOperatorId(operatorId);

                saleOrderMapper.insertDetail(detail);

                Vehicle vUpdate = new Vehicle();
                vUpdate.setVin(vin);
                vUpdate.setStatus("sold");
                vUpdate.setSalePrice(price);
                vUpdate.setSaleDate(LocalDate.now());
                vehicleMapper.updateById(vUpdate);
            }

            Map<String, Object> resMap = new HashMap<>();
            resMap.put("saleId", so.getSaleId());
            resMap.put("orderNumber", so.getOrderNumber());

            return Result.success(resMap);

        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.error("销售失败: " + e.getMessage());
        }
    }
}
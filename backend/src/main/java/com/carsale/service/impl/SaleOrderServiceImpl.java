package com.carsale.service.impl;

import com.carsale.mapper.SaleOrderMapper;
import com.carsale.service.SaleOrderService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
public class SaleOrderServiceImpl implements SaleOrderService {

    @Autowired
    private SaleOrderMapper saleOrderMapper;

    @Override
    @Transactional
    public Result<Map<String, Object>> createOrder(Map<String, Object> params) {
        try {
            // 调用存储过程
            saleOrderMapper.createSaleOrder(params);

            // 存储过程如果成功，MyBatis 会把 OUT 参数或结果集映射回来，
            // 或者如果没有报错，就代表成功。
            // 这里我们直接返回参数中的数据，实际生产中可以从 OUT 参数获取 orderId
            return Result.success(params);
        } catch (Exception e) {
            // 捕获存储过程抛出的 SIGNAL SQLSTATE '45000' 异常
            e.printStackTrace();
            return Result.error("销售失败: " + e.getCause().getMessage());
        }
    }
}
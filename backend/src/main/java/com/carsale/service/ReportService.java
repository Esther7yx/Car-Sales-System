package com.carsale.service;

import com.carsale.mapper.ReportMapper;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private ReportMapper reportMapper;

    public Result<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 并行或串行获取三个视图的数据
        data.put("summary", reportMapper.getSummary());
        data.put("stockPie", reportMapper.getStockStats());
        data.put("salesLine", reportMapper.getSalesTrend());

        return Result.success(data);
    }
}
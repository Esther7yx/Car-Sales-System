package com.carsale.controller;

import com.carsale.annotation.RequiresRole;
import com.carsale.service.ReportService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/report")
@RequiresRole({"admin", "manager", "finance"})
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardData() {
        return reportService.getDashboardData();
    }
}
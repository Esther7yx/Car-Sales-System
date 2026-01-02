package com.carsale.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.Customer;
import com.carsale.service.CustomerService;
import com.carsale.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户信息Controller
 * 处理客户信息相关的HTTP请求
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 分页查询客户信息
     */
    @GetMapping("/page")
    public Result<IPage<Customer>> pageQuery(@RequestParam(defaultValue = "1") long current,
                                             @RequestParam(defaultValue = "10") long size,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) String customerType,
                                             @RequestParam(required = false) String creditRating) {
        Page<Customer> page = new Page<>(current, size);
        IPage<Customer> pageResult = customerService.selectPage(page, name, customerType, creditRating);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询客户信息
     */
    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable Integer id) {
        Customer customer = customerService.getById(id);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        return Result.success(customer);
    }

    /**
     * 根据身份证号查询客户信息
     */
    @GetMapping("/id-number/{idNumber}")
    public Result<Customer> getByIdNumber(@PathVariable String idNumber) {
        Customer customer = customerService.getByIdNumber(idNumber);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        return Result.success(customer);
    }

    /**
     * 根据手机号查询客户信息
     */
    @GetMapping("/phone/{phone}")
    public Result<Customer> getByPhone(@PathVariable String phone) {
        Customer customer = customerService.getByPhone(phone);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        return Result.success(customer);
    }

    /**
     * 添加客户信息
     */
    @PostMapping
    public Result<String> add(@RequestBody Customer customer) {
        // 验证身份证号是否已存在
        if (customerService.isIdNumberExists(customer.getIdNumber())) {
            return Result.error("身份证号已存在");
        }
        
        // 验证手机号是否已存在
        if (customerService.isPhoneExists(customer.getPhone())) {
            return Result.error("手机号已存在");
        }
        
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        
        boolean result = customerService.save(customer);
        if (result) {
            return Result.success("添加客户成功");
        } else {
            return Result.error("添加客户失败");
        }
    }

    /**
     * 修改客户信息
     */
    @PutMapping
    public Result<String> update(@RequestBody Customer customer) {
        Customer existingCustomer = customerService.getById(customer.getCustomerId());
        if (existingCustomer == null) {
            return Result.error("客户不存在");
        }
        
        // 验证身份证号是否被其他客户使用
        if (!existingCustomer.getIdNumber().equals(customer.getIdNumber()) && 
            customerService.isIdNumberExists(customer.getIdNumber())) {
            return Result.error("身份证号已被其他客户使用");
        }
        
        // 验证手机号是否被其他客户使用
        if (!existingCustomer.getPhone().equals(customer.getPhone()) && 
            customerService.isPhoneExists(customer.getPhone())) {
            return Result.error("手机号已被其他客户使用");
        }
        
        customer.setUpdatedAt(LocalDateTime.now());
        
        boolean result = customerService.updateById(customer);
        if (result) {
            return Result.success("修改客户成功");
        } else {
            return Result.error("修改客户失败");
        }
    }

    /**
     * 删除客户信息
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        Customer customer = customerService.getById(id);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        
        boolean result = customerService.removeById(id);
        if (result) {
            return Result.success("删除客户成功");
        } else {
            return Result.error("删除客户失败");
        }
    }

    /**
     * 批量删除客户信息
     */
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的客户");
        }
        
        boolean result = customerService.removeByIds(ids);
        if (result) {
            return Result.success("批量删除客户成功");
        } else {
            return Result.error("批量删除客户失败");
        }
    }

    /**
     * 获取所有客户列表
     */
    @GetMapping("/list")
    public Result<List<Customer>> list() {
        List<Customer> customerList = customerService.list();
        return Result.success(customerList);
    }
}
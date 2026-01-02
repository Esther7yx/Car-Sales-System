package com.carsale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carsale.entity.Customer;
import com.carsale.mapper.CustomerMapper;
import com.carsale.service.CustomerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 客户信息服务实现类
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public IPage<Customer> selectPage(Page<Customer> page, String name, String customerType, String creditRating) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(Customer::getName, name);
        }
        
        if (StringUtils.isNotBlank(customerType)) {
            queryWrapper.eq(Customer::getCustomerType, customerType);
        }
        
        if (StringUtils.isNotBlank(creditRating)) {
            queryWrapper.eq(Customer::getCreditRating, creditRating);
        }
        
        queryWrapper.orderByDesc(Customer::getUpdatedAt);
        
        return this.page(page, queryWrapper);
    }

    @Override
    public Customer getByIdNumber(String idNumber) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Customer::getIdNumber, idNumber);
        return this.getOne(queryWrapper);
    }

    @Override
    public Customer getByPhone(String phone) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Customer::getPhone, phone);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isIdNumberExists(String idNumber) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Customer::getIdNumber, idNumber);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean isPhoneExists(String phone) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Customer::getPhone, phone);
        return this.count(queryWrapper) > 0;
    }
}
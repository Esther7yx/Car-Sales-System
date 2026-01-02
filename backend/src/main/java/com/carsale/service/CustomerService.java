package com.carsale.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.carsale.entity.Customer;

/**
 * 客户信息服务接口
 */
public interface CustomerService extends IService<Customer> {
    
    /**
     * 分页查询客户信息
     */
    IPage<Customer> selectPage(Page<Customer> page, String name, String customerType, String creditRating);
    
    /**
     * 根据身份证号查询客户
     */
    Customer getByIdNumber(String idNumber);
    
    /**
     * 根据手机号查询客户
     */
    Customer getByPhone(String phone);
    
    /**
     * 检查身份证号是否已存在
     */
    boolean isIdNumberExists(String idNumber);
    
    /**
     * 检查手机号是否已存在
     */
    boolean isPhoneExists(String phone);
}
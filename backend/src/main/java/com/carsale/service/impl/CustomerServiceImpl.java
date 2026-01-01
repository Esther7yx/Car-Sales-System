package com.carsale.service.impl;

import com.carsale.entity.Customer;
import com.carsale.mapper.CustomerMapper;
import com.carsale.service.CustomerService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public Result<List<Customer>> getList(String keyword) {
        return Result.success(customerMapper.selectAll(keyword));
    }

    @Override
    public Result<Customer> getById(Integer id) {
        Customer customer = customerMapper.selectById(id);
        if (customer != null) {
            return Result.success(customer);
        }
        return Result.error("客户不存在");
    }

    @Override
    public Result<String> create(Customer customer) {
        customerMapper.insert(customer);
        return Result.success("创建成功");
    }

    @Override
    public Result<String> update(Customer customer) {
        customerMapper.update(customer);
        return Result.success("更新成功");
    }

    @Override
    public Result<String> delete(Integer id) {
        customerMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
package com.carsale.service;

import com.carsale.entity.Customer;
import com.carsale.utils.Result;
import java.util.List;

public interface CustomerService {
    Result<List<Customer>> getList(String keyword);
    Result<Customer> getById(Integer id);
    Result<String> create(Customer customer);
    Result<String> update(Customer customer);
    Result<String> delete(Integer id);
}
package com.carsale.mapper;

import com.carsale.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CustomerMapper {
    List<Customer> selectAll(@Param("keyword") String keyword);
    Customer selectById(Integer customerId);
    int insert(Customer customer);
    int update(Customer customer);
    int deleteById(Integer customerId);
}
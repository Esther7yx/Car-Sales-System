package com.carsale.controller;

import com.carsale.annotation.RequiresRole;
import com.carsale.entity.Customer;
import com.carsale.service.CustomerService;
import com.carsale.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiresRole({"admin", "manager", "sales"})
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Result<List<Customer>> getList(@RequestParam(required = false) String keyword) {
        return customerService.getList(keyword);
    }

    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable Integer id) {
        return customerService.getById(id);
    }

    @PostMapping
    public Result<String> create(@RequestBody Customer customer) {
        return customerService.create(customer);
    }

    @PutMapping
    public Result<String> update(@RequestBody Customer customer) {
        return customerService.update(customer);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        return customerService.delete(id);
    }
}
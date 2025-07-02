package com.pc.customers.dao;

import com.pc.customers.model.Customer;

import java.util.List;
import java.util.UUID;

public interface ICustomerDAO {

    Customer findById(UUID id);
    List<Customer> findByName(String name);
    List<Customer> findByEmail(String email);
    Customer save(Customer customer);
    void deleteById(UUID id);
}

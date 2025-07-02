package com.pc.customers.service;

import com.pc.customers.dao.ICustomerDAO;
import com.pc.customers.dto.CustomerRequest;
import com.pc.customers.dto.CustomerResponse;
import com.pc.customers.model.Customer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@Service
public class CustomerService {

    @Autowired
    private ICustomerDAO customerDAO;

    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerDAO.findById(id);
        if (customer!=null){
            return mapToResponse(customer);
        }else{
            return null;
        }
    }

    public List<CustomerResponse> getCustomerByName(String name) {
        return customerDAO.findByName(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CustomerResponse> getCustomerByEmail(String email) {
        return customerDAO.findByEmail(email).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setAnnualSpend(request.getAnnualSpend());
        customer.setLastPurchaseDate(request.getLastPurchaseDate());
        customer = customerDAO.save(customer);
        return mapToResponse(customer);
    }

    @Transactional //ensure ACID property
    public CustomerResponse updateCustomer(UUID id, CustomerRequest request) {
        Optional<Customer> optionalCustomer = Optional.ofNullable(customerDAO.findById(id));
        if (optionalCustomer.isEmpty()) return null;

        Customer customer = optionalCustomer.get();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setAnnualSpend(request.getAnnualSpend());
        customer.setLastPurchaseDate(request.getLastPurchaseDate());
        customerDAO.save(customer);
        return mapToResponse(customer);
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        customerDAO.deleteById(id);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());
        response.setAnnualSpend(customer.getAnnualSpend());
        response.setLastPurchaseDate(customer.getLastPurchaseDate());
        response.setTier(calculateTier(customer.getAnnualSpend(), customer.getLastPurchaseDate()));
        return response;
    }

    private String calculateTier(Double spend, Date date) {
        if (spend == null || spend < 1000) return "Silver";

        Date now = new Date();
        Calendar cal = Calendar.getInstance();

        if (spend < 10000 && date != null) {
            cal.setTime(now);
            cal.add(Calendar.MONTH, -12); // 12 months ago
            Date twelveMonthsAgo = cal.getTime();

            if (date.after(twelveMonthsAgo)) {
                return "Gold";
            }
        }

        if (spend >= 10000 && date != null) {
            cal.setTime(now);
            cal.add(Calendar.MONTH, -6); // 6 months ago
            Date sixMonthsAgo = cal.getTime();

            if (date.after(sixMonthsAgo)) {
                return "Platinum";
            }
        }

        return "Silver";
    }


}

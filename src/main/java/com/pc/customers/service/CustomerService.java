package com.pc.customers.service;

import com.pc.customers.dao.ICustomerDAO;
import com.pc.customers.dto.CustomerRequest;
import com.pc.customers.dto.CustomerResponse;
import com.pc.customers.exception.CustomerServiceException;
import com.pc.customers.model.Customer;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private ICustomerDAO customerDAO;

    public CustomerResponse getCustomerById(UUID id) {
        logger.info("Fetching customer by ID: {}", id);
        try {
            Customer customer = customerDAO.findById(id);
            if (customer != null) {
                return mapToResponse(customer);
            } else {
                logger.warn("Customer not found with ID: {}", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error fetching customer by ID: {}", id, e);
            throw new CustomerServiceException("Error retrieving customer with ID: " + id, e);
        }
    }

    public List<CustomerResponse> getCustomerByName(String name) {
        logger.info("Fetching customers by name: {}", name);
        try {
            return customerDAO.findByName(name).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching customers by name: {}", name, e);
            throw new CustomerServiceException("Error retrieving customers by name: " + name, e);
        }
    }

    public List<CustomerResponse> getCustomerByEmail(String email) {
        logger.info("Fetching customers by email: {}", email);
        try {
            return customerDAO.findByEmail(email).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching customers by email: {}", email, e);
            throw new CustomerServiceException("Error retrieving customers by email: " + email, e);
        }
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        logger.info("Creating new customer with email: {}", request.getEmail());
        try {
            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setEmail(request.getEmail());
            customer.setAnnualSpend(request.getAnnualSpend());
            customer.setLastPurchaseDate(request.getLastPurchaseDate());
            customer = customerDAO.save(customer);
            logger.debug("Customer created with ID: {}", customer.getId());
            return mapToResponse(customer);
        } catch (Exception e) {
            logger.error("Error creating customer with email: {}", request.getEmail(), e);
            throw new CustomerServiceException("Error creating customer with email: " + request.getEmail(), e);
        }
    }

    @Transactional //ensure ACID property
    public CustomerResponse updateCustomer(UUID id, CustomerRequest request) {
        logger.info("Updating customer with ID: {}", id);
        try {
            Optional<Customer> optionalCustomer = Optional.ofNullable(customerDAO.findById(id));
            if (optionalCustomer.isEmpty()) {
                logger.warn("Customer not found for update with ID: {}", id);
                return null;
            }
            Customer customer = optionalCustomer.get();
            customer.setName(request.getName());
            customer.setEmail(request.getEmail());
            customer.setAnnualSpend(request.getAnnualSpend());
            customer.setLastPurchaseDate(request.getLastPurchaseDate());
            customerDAO.save(customer);
            logger.debug("Customer updated with ID: {}", id);
            return mapToResponse(customer);
        } catch (Exception e) {
            logger.error("Error updating customer with ID: {}", id, e);
            throw new CustomerServiceException("Error updating customer with ID: " + id, e);
        }
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        logger.info("Deleting customer with ID: {}", id);
        try {
            customerDAO.deleteById(id);
            logger.debug("Customer deleted with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting customer with ID: {}", id, e);
            throw new CustomerServiceException("Error deleting customer with ID: " + id, e);
        }
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

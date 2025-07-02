package com.pc.customers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pc.customers.dto.CustomerRequest;
import com.pc.customers.dto.CustomerResponse;
import com.pc.customers.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CustomerControllerTest {

    @InjectMocks
    private CustomerController customerController;

    @Mock
    private CustomerService customerService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHealthcheck() {
        assertEquals("Customer Service is healthy", customerController.healthcheck());
    }

    @Test
    void testCreate() {
        CustomerRequest request = new CustomerRequest(); // populate fields if needed
        CustomerResponse response = new CustomerResponse(); // populate fields

        when(customerService.createCustomer(request)).thenReturn(response);

        ResponseEntity<CustomerResponse> result = customerController.create(request);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    void testGetById_Found() {
        UUID id = UUID.randomUUID();
        CustomerResponse response = new CustomerResponse();
        when(customerService.getCustomerById(id)).thenReturn(response);

        ResponseEntity<CustomerResponse> result = customerController.getById(id);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    void testGetById_NotFound() {
        UUID id = UUID.randomUUID();
        when(customerService.getCustomerById(id)).thenReturn(null);

        ResponseEntity<CustomerResponse> result = customerController.getById(id);

        assertEquals(404, result.getStatusCodeValue());
        assertNull(result.getBody());
    }

    @Test
    void testGetByName() {
        List<CustomerResponse> list = Collections.singletonList(new CustomerResponse());
        when(customerService.getCustomerByName("John")).thenReturn(list);

        ResponseEntity<List<CustomerResponse>> result = customerController.getByName("John");

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(list, result.getBody());
    }

    @Test
    void testGetByEmail() {
        List<CustomerResponse> list = Collections.singletonList(new CustomerResponse());
        when(customerService.getCustomerByEmail("test@example.com")).thenReturn(list);

        ResponseEntity<List<CustomerResponse>> result = customerController.getByEmail("test@example.com");

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(list, result.getBody());
    }

    @Test
    void testUpdate_Found() {
        UUID id = UUID.randomUUID();
        CustomerRequest request = new CustomerRequest();
        CustomerResponse updated = new CustomerResponse();

        when(customerService.updateCustomer(id, request)).thenReturn(updated);

        ResponseEntity<CustomerResponse> result = customerController.update(id, request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(updated, result.getBody());
    }

    @Test
    void testUpdate_NotFound() {
        UUID id = UUID.randomUUID();
        CustomerRequest request = new CustomerRequest();

        when(customerService.updateCustomer(id, request)).thenReturn(null);

        ResponseEntity<CustomerResponse> result = customerController.update(id, request);

        assertEquals(404, result.getStatusCodeValue());
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();

        doNothing().when(customerService).deleteCustomer(id);

        ResponseEntity<Void> result = customerController.delete(id);

        assertEquals(204, result.getStatusCodeValue());
    }



}

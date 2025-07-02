package com.pc.customers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pc.customers.dto.CustomerRequest;
import com.pc.customers.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
public class CustomerControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;
    //name validation
    @Test
    void shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        CustomerRequest validRequest = new CustomerRequest();
        validRequest.setName("");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors[0].defaultMessage").value("Customer name cannot be blank"));
    }

    //email validation
    @Test
    void shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        CustomerRequest validRequest = new CustomerRequest();
        validRequest.setEmail("invalid-email");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors[?(@.field=='email')].defaultMessage").value("Email should be valid"));
    }
}

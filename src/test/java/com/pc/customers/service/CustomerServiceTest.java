package com.pc.customers.service;

import com.pc.customers.dao.ICustomerDAO;
import com.pc.customers.dto.CustomerRequest;
import com.pc.customers.dto.CustomerResponse;
import com.pc.customers.exception.CustomerServiceException;
import com.pc.customers.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private ICustomerDAO customerDAO;

    private UUID customerId;
    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customerId = UUID.randomUUID();
        customer = new Customer();
        customer.setId(customerId);
        customer.setName("Test User");
        customer.setEmail("test@example.com");
        customer.setAnnualSpend(5000.0);
        customer.setLastPurchaseDate(new Date());
    }

    @Test
    void getCustomerById_shouldReturnCustomerResponse_whenCustomerExists() {
        when(customerDAO.findById(customerId)).thenReturn(customer);

        CustomerResponse response = customerService.getCustomerById(customerId);

        assertNotNull(response);
        assertEquals(customerId, response.getId());
        assertEquals("Test User", response.getName());
    }

    @Test
    void getCustomerById_shouldReturnNull_whenCustomerDoesNotExist() {
        when(customerDAO.findById(customerId)).thenReturn(null);

        CustomerResponse response = customerService.getCustomerById(customerId);

        assertNull(response);
    }

    @Test
    void getCustomerByName_shouldReturnListOfCustomerResponses() {
        when(customerDAO.findByName("Test")).thenReturn(List.of(customer));

        List<CustomerResponse> responses = customerService.getCustomerByName("Test");

        assertEquals(1, responses.size());
        assertEquals("Test User", responses.get(0).getName());
    }

    @Test
    void getCustomerByEmail_shouldReturnListOfCustomerResponses() {
        when(customerDAO.findByEmail("test@example.com")).thenReturn(List.of(customer));

        List<CustomerResponse> responses = customerService.getCustomerByEmail("test@example.com");

        assertEquals(1, responses.size());
        assertEquals("test@example.com", responses.get(0).getEmail());
    }

    @Test
    void createCustomer_shouldReturnCustomerResponseFirst() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setAnnualSpend(2000.0);
        request.setLastPurchaseDate(new Date());

        when(customerDAO.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response.getId());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void updateCustomer_shouldReturnUpdatedCustomerResponse_whenCustomerExists() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Updated Name");
        request.setEmail("updated@example.com");
        request.setAnnualSpend(8000.0);
        request.setLastPurchaseDate(new Date());

        when(customerDAO.findById(customerId)).thenReturn(customer);
        when(customerDAO.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.updateCustomer(customerId, request);

        assertEquals("Updated Name", response.getName());
        assertEquals("updated@example.com", response.getEmail());
    }

    @Test
    void updateCustomer_shouldReturnNull_whenCustomerNotFound() {
        when(customerDAO.findById(customerId)).thenReturn(null);

        CustomerRequest request = new CustomerRequest();
        CustomerResponse response = customerService.updateCustomer(customerId, request);

        assertNull(response);
    }

    @Test
    void deleteCustomer_shouldCallDaoDeleteById() {
        doNothing().when(customerDAO).deleteById(customerId);

        customerService.deleteCustomer(customerId);

        verify(customerDAO, times(1)).deleteById(customerId);
    }

    @Test
    void getCustomerByName_shouldReturnEmptyList_whenNameNotFound() {
        when(customerDAO.findByName("Unknown")).thenReturn(Collections.emptyList());

        List<CustomerResponse> result = customerService.getCustomerByName("Unknown");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getCustomerByEmail_shouldReturnEmptyList_whenEmailIsNull() {
        when(customerDAO.findByEmail(null)).thenReturn(Collections.emptyList());

        List<CustomerResponse> result = customerService.getCustomerByEmail(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createCustomer_shouldHandleNullFieldsGracefully() {
        CustomerRequest request = new CustomerRequest(); // all fields null

        when(customerDAO.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getEmail());
        assertEquals("Silver", response.getTier()); // fallback tier
    }

    @Test
    void updateCustomer_shouldNotThrow_whenFieldsAreNull() {
        when(customerDAO.findById(customerId)).thenReturn(customer);
        when(customerDAO.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerRequest request = new CustomerRequest(); // all fields null

        CustomerResponse response = customerService.updateCustomer(customerId, request);

        assertNotNull(response);
        assertNull(response.getName());
        assertEquals("Silver", response.getTier()); // fallback
    }

    @Test
    void createCustomer_shouldReturnSilverTier_whenSpendIsLow() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Silver User");
        request.setEmail("silver@example.com");
        request.setAnnualSpend(200.0); // Low spend
        request.setLastPurchaseDate(new Date());

        when(customerDAO.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        CustomerResponse response = customerService.createCustomer(request);

        assertEquals("Silver", response.getTier());
    }

    @Test
    void createCustomer_shouldThrowNullPointer_whenRequestIsNull() {
        assertThrows(NullPointerException.class, () -> customerService.createCustomer(null));
    }

    @Test
    void updateCustomer_shouldThrowCustomerServiceException_whenRequestIsNull() {
        when(customerDAO.findById(customerId)).thenReturn(customer);

        assertThrows(CustomerServiceException.class, () -> customerService.updateCustomer(customerId, null));
    }


    @Test
    void calculateTier_shouldReturnSilver_whenSpendIsLow() throws Exception {
        Method method = CustomerService.class.getDeclaredMethod("calculateTier", Double.class, Date.class);
        method.setAccessible(true);

        String result = (String) method.invoke(customerService, 500.0, new Date());
        assertEquals("Silver", result);
    }

    @Test
    void calculateTier_shouldReturnGold_whenSpendIsModerateAndRecent() throws Exception {
        Method method = CustomerService.class.getDeclaredMethod("calculateTier", Double.class, Date.class);
        method.setAccessible(true);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);
        Date recentDate = cal.getTime();

        String result = (String) method.invoke(customerService, 5000.0, recentDate);
        assertEquals("Gold", result);
    }

    @Test
    void calculateTier_shouldReturnSilver_whenDateIsOldForGoldTier() throws Exception {
        Method method = CustomerService.class.getDeclaredMethod("calculateTier", Double.class, Date.class);
        method.setAccessible(true);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -2); // 24 months ago, well outside the 12-month window
        Date oldDate = cal.getTime();

        String result = (String) method.invoke(customerService, 5000.0, oldDate);
        assertEquals("Silver", result);
    }

    @Test
    void calculateTier_shouldReturnSilver_whenDateIsNullForGoldTier() throws Exception {
        Method method = CustomerService.class.getDeclaredMethod("calculateTier", Double.class, Date.class);
        method.setAccessible(true);

        String result = (String) method.invoke(customerService, 5000.0, null);
        assertEquals("Silver", result);
    }

    @Test
    void calculateTier_shouldReturnPlatinum_whenSpendIsHighAndVeryRecent() throws Exception {
        Method method = CustomerService.class.getDeclaredMethod("calculateTier", Double.class, Date.class);
        method.setAccessible(true);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        Date recentDate = cal.getTime();

        String result = (String) method.invoke(customerService, 12000.0, recentDate);
        assertEquals("Platinum", result);
    }

    @Test
    void calculateTier_shouldReturnSilver_whenSpendHighButOldDate() throws Exception {
        Method method = CustomerService.class.getDeclaredMethod("calculateTier", Double.class, Date.class);
        method.setAccessible(true);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -8); // Outside the 6-month window for Platinum
        Date oldDate = cal.getTime();

        String result = (String) method.invoke(customerService, 12000.0, oldDate);
        assertEquals("Silver", result);
    }

    @Test
    void calculateTier_shouldReturnSilver_whenDateIsNullForPlatinumTier() throws Exception {
        Method method = CustomerService.class.getDeclaredMethod("calculateTier", Double.class, Date.class);
        method.setAccessible(true);

        String result = (String) method.invoke(customerService, 12000.0, null);
        assertEquals("Silver", result);
    }

    @Test
    @DisplayName("CREATE: Should create and return a new customer")
    void createCustomer_shouldReturnCustomerResponse() {
        CustomerRequest request = new CustomerRequest();
        request.setName("New User");
        request.setEmail("new@example.com");

        when(customerDAO.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response.getId());
        assertEquals("New User", response.getName());
        verify(customerDAO, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("RETRIEVE: Should return a customer by ID when found")
    void getCustomerById_shouldReturnCustomer_whenExists() {
        when(customerDAO.findById(customerId)).thenReturn(customer);
        CustomerResponse response = customerService.getCustomerById(customerId);
        assertNotNull(response);
        assertEquals(customerId, response.getId());
    }

    @Test
    @DisplayName("RETRIEVE: Should return null when customer by ID is not found")
    void getCustomerById_shouldReturnNull_whenNotExists() {
        when(customerDAO.findById(customerId)).thenReturn(null);
        CustomerResponse response = customerService.getCustomerById(customerId);
        assertNull(response);
    }

    @Test
    @DisplayName("UPDATE: Should update and return the customer when found")
    void updateCustomer_shouldReturnUpdatedCustomer_whenExists() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Updated Name");

        when(customerDAO.findById(customerId)).thenReturn(customer);
        when(customerDAO.save(any(Customer.class))).thenReturn(customer);

        CustomerResponse response = customerService.updateCustomer(customerId, request);

        assertEquals("Updated Name", response.getName());
        verify(customerDAO, times(1)).save(customer);
    }

    @Test
    @DisplayName("UPDATE: Should return null when customer to update is not found")
    void updateCustomer_shouldReturnNull_whenNotExists() {
        CustomerRequest request = new CustomerRequest();
        when(customerDAO.findById(customerId)).thenReturn(null);
        CustomerResponse response = customerService.updateCustomer(customerId, request);
        assertNull(response);
    }

    @Test
    @DisplayName("DELETE: Should call the delete method on the DAO")
    void deleteCustomer_shouldCallDaoDelete() {
        doNothing().when(customerDAO).deleteById(customerId);
        customerService.deleteCustomer(customerId);
        verify(customerDAO, times(1)).deleteById(customerId);
    }

    // --- Tier Calculation Logic Tests ---

    // Helper to create a date a certain number of months ago
    private Date getDateMonthsAgo(int months) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -months);
        return cal.getTime();
    }

    @ParameterizedTest(name = "Spend: {0}, Expected Tier: {1}")
    @MethodSource("spendAndExpectedTier")
    @DisplayName("Should calculate correct tier based on spend with recent purchase date")
    void shouldCalculateCorrectTier_forRecentPurchases(Double spend, String expectedTier) {
        Customer testCustomer = new Customer();
        testCustomer.setId(UUID.randomUUID());
        testCustomer.setAnnualSpend(spend);
        testCustomer.setLastPurchaseDate(getDateMonthsAgo(3)); // Recent purchase

        when(customerDAO.findById(any(UUID.class))).thenReturn(testCustomer);

        CustomerResponse response = customerService.getCustomerById(testCustomer.getId());
        assertEquals(expectedTier, response.getTier());
    }

    static Stream<Arguments> spendAndExpectedTier() {
        return Stream.of(
                Arguments.of((Double) null, "Silver"),
                Arguments.of(0.0, "Silver"),
                Arguments.of(999.99, "Silver"),
                Arguments.of(5000.0, "Gold"),
                Arguments.of(9999.99, "Gold"),
                Arguments.of(10000.0, "Platinum"),
                Arguments.of(25000.0, "Platinum")
        );
    }

    @Test
    @DisplayName("Should return Silver for Gold-level spend with old purchase date")
    void shouldReturnSilver_forGoldSpendWithOldDate() {
        customer.setAnnualSpend(8000.0);
        customer.setLastPurchaseDate(getDateMonthsAgo(13)); // 13 months ago (too old for Gold)

        when(customerDAO.findById(customerId)).thenReturn(customer);
        CustomerResponse response = customerService.getCustomerById(customerId);
        assertEquals("Silver", response.getTier());
    }

    @Test
    @DisplayName("Should return Silver for Platinum-level spend with old purchase date")
    void shouldReturnSilver_forPlatinumSpendWithOldDate() {
        customer.setAnnualSpend(15000.0);
        customer.setLastPurchaseDate(getDateMonthsAgo(7)); // 7 months ago (too old for Platinum)

        when(customerDAO.findById(customerId)).thenReturn(customer);
        CustomerResponse response = customerService.getCustomerById(customerId);
        assertEquals("Silver", response.getTier());
    }

    @Test
    void getCustomerById_shouldThrowException_whenDaoFails() {
        UUID id = UUID.randomUUID();
        when(customerDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

        CustomerServiceException ex = assertThrows(CustomerServiceException.class, () -> customerService.getCustomerById(id));
        assertTrue(ex.getMessage().contains("Error retrieving customer with ID"));
    }

    @Test
    void getCustomerByName_shouldThrowException_whenDaoFails() {
        when(customerDAO.findByName("fail")).thenThrow(new RuntimeException("DB error"));

        CustomerServiceException ex = assertThrows(CustomerServiceException.class, () -> customerService.getCustomerByName("fail"));
        assertTrue(ex.getMessage().contains("Error retrieving customers by name"));
    }

    @Test
    void getCustomerByEmail_shouldThrowException_whenDaoFails() {
        when(customerDAO.findByEmail("fail@example.com")).thenThrow(new RuntimeException("DB error"));

        CustomerServiceException ex = assertThrows(CustomerServiceException.class, () -> customerService.getCustomerByEmail("fail@example.com"));
        assertTrue(ex.getMessage().contains("Error retrieving customers by email"));
    }

    @Test
    void deleteCustomer_shouldThrowException_whenDaoFails() {
        doThrow(new RuntimeException("DB error")).when(customerDAO).deleteById(customerId);

        assertThrows(RuntimeException.class, () -> customerService.deleteCustomer(customerId));
    }

    @Test
    void createCustomer_shouldThrowCustomerServiceException_whenDaoFails() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Exception User");
        request.setEmail("exception@example.com");
        request.setAnnualSpend(1000.0);
        request.setLastPurchaseDate(new Date());

        when(customerDAO.save(any(Customer.class))).thenThrow(new RuntimeException("DB failure"));

        CustomerServiceException exception = assertThrows(CustomerServiceException.class, () -> {
            customerService.createCustomer(request);
        });

        assertTrue(exception.getMessage().contains("Error creating customer with email: exception@example.com"));
    }

}

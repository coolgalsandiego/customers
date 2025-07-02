package com.pc.customers.dao;

import com.pc.customers.model.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerDAOJpaImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Customer> typedQuery;

    @InjectMocks
    private CustomerDAOJpaImpl customerDAO;

    private UUID customerId;
    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customerId = UUID.randomUUID();
        customer = new Customer();
        customer.setId(customerId);
        customer.setName("Test Name");
        customer.setEmail("test@example.com");
    }

    @Test
    void findById_shouldReturnCustomer() {
        when(entityManager.find(Customer.class, customerId)).thenReturn(customer);

        Customer result = customerDAO.findById(customerId);

        assertNotNull(result);
        assertEquals(customerId, result.getId());
        verify(entityManager).find(Customer.class, customerId);
    }

    @Test
    void findByName_shouldReturnListOfCustomers() {
        String name = "test";
        List<Customer> expectedList = List.of(customer);

        when(entityManager.createQuery(anyString(), eq(Customer.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("name"), anyString())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedList);

        List<Customer> result = customerDAO.findByName(name);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customer, result.get(0));

        verify(entityManager).createQuery("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(:name)", Customer.class);
        verify(typedQuery).setParameter("name", "%" + name + "%");
        verify(typedQuery).getResultList();
    }

    @Test
    void findByEmail_shouldReturnListOfCustomers() {
        String email = "test@example.com";
        List<Customer> expectedList = List.of(customer);

        when(entityManager.createQuery(anyString(), eq(Customer.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("email"), eq(email))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedList);

        List<Customer> result = customerDAO.findByEmail(email);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customer, result.get(0));

        verify(entityManager).createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class);
        verify(typedQuery).setParameter("email", email);
        verify(typedQuery).getResultList();
    }

    @Test
    void save_shouldReturnMergedCustomer() {
        when(entityManager.merge(customer)).thenReturn(customer);

        Customer result = customerDAO.save(customer);

        assertNotNull(result);
        assertEquals(customer, result);

        verify(entityManager).merge(customer);
    }

    @Test
    void deleteById_shouldRemoveCustomerIfExists() {
        when(entityManager.find(Customer.class, customerId)).thenReturn(customer);

        customerDAO.deleteById(customerId);

        verify(entityManager).find(Customer.class, customerId);
        verify(entityManager).remove(customer);
    }

    @Test
    void deleteById_shouldNotRemoveIfCustomerNotFound() {
        when(entityManager.find(Customer.class, customerId)).thenReturn(null);

        customerDAO.deleteById(customerId);

        verify(entityManager).find(Customer.class, customerId);
        verify(entityManager, never()).remove(any());
    }
}

package com.pc.customers.dao;

import com.pc.customers.model.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CustomerDAOJpaImpl implements ICustomerDAO{

    private final EntityManager entityManager;

    @Autowired
    public CustomerDAOJpaImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }
    /**
     * @param id
     * @return
     */
    @Override
    public Customer findById(UUID id) {
        return entityManager.find(Customer.class, id);
    }

    /**
     * @param name
     * @return
     */
    @Override
    public List<Customer> findByName(String name) {
        String jpql = "SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(:name)";
        TypedQuery<Customer> query = entityManager.createQuery(jpql, Customer.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    /**
     * @param email
     * @return
     */
    @Override
    public List<Customer> findByEmail(String email) {
        String jpql = "SELECT c FROM Customer c WHERE c.email = :email";
        TypedQuery<Customer> query = entityManager.createQuery(jpql, Customer.class);
        query.setParameter("email", email);
        return query.getResultList();
    }

    /**
     * @param customer
     */
    @Override
    public Customer save(Customer customer) {
        return entityManager.merge(customer); // handles both insert and update
    }

    /**
     * @param id
     */
    @Override
    public void deleteById(UUID id) {
        Customer customer = entityManager.find(Customer.class, id);
        if (customer != null) {
            entityManager.remove(customer);
        }
    }
}

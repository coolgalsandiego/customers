package com.pc.customers.model;

import jakarta.persistence.*;
import jakarta.persistence.Id;


import java.util.UUID;
import java.util.Date;
import jakarta.persistence.Entity;

@Entity
@Table(name="customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    @Column(name="name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="annual_spend")
    private Double annualSpend;

    @Column(name="last_purchase_date")
    private Date lastPurchaseDate;

    public Customer() {
    }

    public Customer(UUID id) {
        this.id = id;
    }

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getAnnualSpend() {
        return annualSpend;
    }

    public void setAnnualSpend(Double annualSpend) {
        this.annualSpend = annualSpend;
    }

    public Date getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setLastPurchaseDate(Date lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", annualSpend=" + annualSpend +
                ", lastPurchaseDate=" + lastPurchaseDate +
                '}';
    }
}

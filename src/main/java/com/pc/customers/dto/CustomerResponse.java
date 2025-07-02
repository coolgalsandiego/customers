package com.pc.customers.dto;

import java.util.Date;
import java.util.UUID;

public class CustomerResponse {
    private UUID id;
    private String name;
    private String email;
    private Double annualSpend;
    private Date lastPurchaseDate;
    private String tier;

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

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }
}

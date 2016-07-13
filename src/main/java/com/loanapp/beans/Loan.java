package com.loanapp.beans;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class Loan {
    @Id
    @Column(name="LOAN_ID", unique=true)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="LOAN_AMOUNT")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name="LOAN_CLIENT_ID")
    private Client client;

    @Column(name="LOAN_COUNTRY")
    private String loanCountry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonManagedReference
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getLoanCountry() {
        return loanCountry;
    }

    public void setLoanCountry(String loanCountry) {
        this.loanCountry = loanCountry;
    }

    protected Loan() {}

    public Loan(BigDecimal amount, Client client){
        this.amount = amount;
        this.client = client;
    }
}

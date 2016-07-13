package com.loanapp.beans;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Client {

    @Id
    @Column(name="CLIENT_ID", unique=true)
    @NotNull
    private String id;

    @NotNull
    @Column(name="CLIENT_FIRST_NAME")
    private String firstName;

    @NotNull
    @Column(name="CLIENT_LAST_NAME")
    private String lastName;

    @Column(name="CLIENT_BLACKLIST")
    private boolean blacklisted = false;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="client", cascade = CascadeType.ALL)
    private List<Loan> loans;

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isBlacklisted(){
        return blacklisted;
    }

    public void setBlacklisted(boolean blacklisted){
        this.blacklisted = blacklisted;
    }

    @JsonBackReference
    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

    protected Client() {}

    public Client(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format("Client[id=%s, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

}


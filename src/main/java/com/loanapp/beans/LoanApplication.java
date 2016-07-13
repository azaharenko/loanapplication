package com.loanapp.beans;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class LoanApplication {
    @NotNull
    private String clientId;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    @Digits(fraction = 2,integer = 4, message="Incorrect amount format")
    @Min(value=1, message="Loan amount should not be less than 1")
    @Max(value=1000, message="Loan amount should not be more than 1000")
    private BigDecimal amount;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

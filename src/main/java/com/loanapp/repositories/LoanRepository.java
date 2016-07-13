package com.loanapp.repositories;

import com.loanapp.beans.Loan;
import org.springframework.data.repository.CrudRepository;

public interface LoanRepository extends CrudRepository<Loan, Long> {

}

package com.myapplication.LoanManagementSystem.repository;


import com.myapplication.LoanManagementSystem.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLoan_Id(Long loanId);

}

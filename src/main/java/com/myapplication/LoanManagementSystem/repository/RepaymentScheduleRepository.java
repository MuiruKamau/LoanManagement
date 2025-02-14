package com.myapplication.LoanManagementSystem.repository;



import com.myapplication.LoanManagementSystem.model.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, Long> {
    List<RepaymentSchedule> findByLoan_Id(Long loanId);
}

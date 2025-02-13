package com.myapplication.LoanManagementSystem.repository;



import com.myapplication.LoanManagementSystem.model.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, Long> {
    // Add custom queries if needed.
}

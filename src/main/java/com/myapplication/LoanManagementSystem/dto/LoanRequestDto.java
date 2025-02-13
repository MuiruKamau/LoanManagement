package com.myapplication.LoanManagementSystem.dto;

import com.myapplication.LoanManagementSystem.model.Frequency;
import com.myapplication.LoanManagementSystem.model.LoanStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanRequestDto {
    private Long customerId;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private LocalDate dueDate;
    private int repaymentPeriod;
    private Frequency repaymentFrequency;
    private BigDecimal totalRepayableAmount;
    private LoanStatus status;
}

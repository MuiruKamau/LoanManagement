package com.myapplication.LoanManagementSystem.dto;

import com.myapplication.LoanManagementSystem.model.Frequency;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanRequestDto {
    private Long customerId;
    private BigDecimal principalAmount;
    // Interest rate input as a whole number (e.g., 10 for 10%)
    private int interestRate;
    // Repayment period is in months
    private int repaymentPeriod;
    private Frequency repaymentFrequency;
}


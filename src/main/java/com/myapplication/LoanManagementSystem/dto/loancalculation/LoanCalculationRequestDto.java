package com.myapplication.LoanManagementSystem.dto.loancalculation;

import com.myapplication.LoanManagementSystem.model.Frequency;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanCalculationRequestDto {
    private BigDecimal principalAmount;
    // Repayment period is in months
    private int repaymentPeriod;
    // Interest rate input as a whole number, e.g., 10 for 10%
    private int interestRate;
    // Frequency: either WEEKLY or MONTHLY
    private Frequency frequency;
}

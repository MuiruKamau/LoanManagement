package com.myapplication.LoanManagementSystem.dto.loancalculation;



import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanCalculationRequestDto {

    private BigDecimal principalAmount;
    // Repayment period is now in months.
    private int repaymentPeriod;
    // Interest rate is inputted by the user (e.g., 0.10 for 10% per annum)
    private BigDecimal interestRate;
}
package com.myapplication.LoanManagementSystem.dto.stats;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class LoanSummaryDto {
    private int totalLoans;
    private BigDecimal totalAmountDisbursed; // Sum of principal amounts
    private Map<String, Integer> loansByStatus; // e.g., {"ACTIVE": 100, "PAID": 40, "DEFAULTED": 10}
    private BigDecimal averageLoanAmount;       // Average of principal amounts
    private BigDecimal averageInterestRate;       // Average interest rate (as percentage)
    private double averageRepaymentPeriodMonths;  // Average repayment period (in months)
}
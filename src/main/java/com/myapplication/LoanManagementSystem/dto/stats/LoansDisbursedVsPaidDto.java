package com.myapplication.LoanManagementSystem.dto.stats;



import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoansDisbursedVsPaidDto {
    private BigDecimal totalAmountDisbursed; // Sum of principal amounts
    private BigDecimal totalAmountPaid;       // Sum of payments received (aggregated from schedules)
    private double percentagePaid;            // (totalAmountPaid / totalAmountDisbursed) * 100
}


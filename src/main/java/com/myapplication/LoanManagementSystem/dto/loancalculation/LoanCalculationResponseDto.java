package com.myapplication.LoanManagementSystem.dto.loancalculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)  // This will exclude any null fields from the JSON response
public class LoanCalculationResponseDto {
    private BigDecimal totalRepayableAmount;
    private LocalDate dueDate;
    // If frequency is MONTHLY, EMI is populated; if WEEKLY, it remains null.
    private BigDecimal emi;
    // If frequency is WEEKLY, EWI is populated; if MONTHLY, it remains null.
    private BigDecimal ewi;
    // Total number of installments (months for monthly, or calculated weeks for weekly)
    private int numberOfInstallments;
}

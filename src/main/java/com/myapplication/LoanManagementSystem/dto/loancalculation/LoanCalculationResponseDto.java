package com.myapplication.LoanManagementSystem.dto.loancalculation;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanCalculationResponseDto {
    private BigDecimal totalRepayableAmount;
    private LocalDate dueDate;
    private BigDecimal emi; // Equal Monthly Installment
    private BigDecimal ewi; // Equal Weekly Installment
}
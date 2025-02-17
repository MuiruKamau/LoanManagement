package com.myapplication.LoanManagementSystem.dto.stats;



import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentSummaryDto {
    private BigDecimal totalAmountRepayable;    // Sum of loan.totalRepayableAmount
    private BigDecimal totalAmountPaid;         // Sum of all payments received (across schedules)
    private BigDecimal totalRemainingBalance;   // totalAmountRepayable - totalAmountPaid
    private BigDecimal averageInstallmentPayment; // Average installment amount (calculated from each loan)
}


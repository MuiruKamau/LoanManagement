package com.myapplication.LoanManagementSystem.utils;

import com.myapplication.LoanManagementSystem.dto.loancalculation.LoanCalculationResponseDto;
import com.myapplication.LoanManagementSystem.model.Frequency;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

public class LoanCalculator {

    /**
     * Calculates loan repayment details.
     * @param principal the principal amount
     * @param repaymentPeriod repayment period in months
     * @param interestRate the interest rate as a whole number (e.g., 10 for 10%)
     * @param frequency either WEEKLY or MONTHLY
     * @return the calculated response including total repayable, due date, EMI/EWI and number of installments
     */
    public static LoanCalculationResponseDto calculateLoan(BigDecimal principal, int repaymentPeriod, int interestRate, Frequency frequency) {
        LoanCalculationResponseDto result = new LoanCalculationResponseDto();

        // Convert whole number interest rate to a decimal (e.g., 10 becomes 0.10)
        BigDecimal interestRateDecimal = new BigDecimal(interestRate).divide(new BigDecimal("100"), MathContext.DECIMAL64);

        // Convert repayment period from months to years for interest calculation
        BigDecimal periodInYears = new BigDecimal(repaymentPeriod)
                .divide(new BigDecimal("12"), MathContext.DECIMAL64);

        // Calculate simple interest: Interest = Principal * Interest Rate * Time (in years)
        BigDecimal interest = principal.multiply(interestRateDecimal)
                .multiply(periodInYears, MathContext.DECIMAL64);

        // Total repayable = Principal + Interest (rounded to 2 decimals)
        BigDecimal totalRepayableAmount = principal.add(interest)
                .setScale(2, RoundingMode.HALF_UP);
        result.setTotalRepayableAmount(totalRepayableAmount);

        // Calculate due date: current date plus repayment period (in months)
        LocalDate createdAt = LocalDate.now();
        LocalDate dueDate = createdAt.plusMonths(repaymentPeriod);
        result.setDueDate(dueDate);

        if (frequency == Frequency.MONTHLY) {
            // For monthly frequency, the number of installments equals the repayment period (in months)
            int installments = repaymentPeriod;
            BigDecimal emi = totalRepayableAmount.divide(new BigDecimal(installments), 2, RoundingMode.HALF_UP);
            result.setEmi(emi);
            result.setNumberOfInstallments(installments);
            result.setEwi(null);  // Not applicable for monthly
        } else if (frequency == Frequency.WEEKLY) {
            // For weekly frequency, we convert the period to an approximate number of weeks.
            // Here, we use 4.345 as the average number of weeks per month.
            int installments = (int) Math.ceil(repaymentPeriod * 4.345);
            BigDecimal ewi = totalRepayableAmount.divide(new BigDecimal(installments), 2, RoundingMode.HALF_UP);
            result.setEwi(ewi);
            result.setNumberOfInstallments(installments);
            result.setEmi(null);  // Not applicable for weekly
        }

        return result;
    }
}

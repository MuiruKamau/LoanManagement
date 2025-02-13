package com.myapplication.LoanManagementSystem.utils;


import com.myapplication.LoanManagementSystem.dto.loancalculation.LoanCalculationResponseDto;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

public class LoanCalculator {

    public static LoanCalculationResponseDto calculateLoan(BigDecimal principal, int repaymentPeriod, BigDecimal interestRate) {
        LoanCalculationResponseDto result = new LoanCalculationResponseDto();

        // Convert repayment period from months to years
        BigDecimal periodInYears = new BigDecimal(repaymentPeriod)
                .divide(new BigDecimal("12"), MathContext.DECIMAL64);

        // Calculate simple interest:
        // Interest = Principal * Interest Rate * Time (in years)
        BigDecimal interest = principal.multiply(interestRate)
                .multiply(periodInYears, MathContext.DECIMAL64);

        // Total repayable = Principal + Interest
        BigDecimal totalRepayableAmount = principal.add(interest)
                .setScale(2, RoundingMode.HALF_UP);
        result.setTotalRepayableAmount(totalRepayableAmount);

        // Calculate due date: current date plus repayment period (in months)
        LocalDate createdAt = LocalDate.now();
        LocalDate dueDate = createdAt.plusMonths(repaymentPeriod);
        result.setDueDate(dueDate);

        // Calculate Equal Monthly Installment (EMI)
        // EMI = Total Repayable Amount / Number of months
        BigDecimal emi = totalRepayableAmount.divide(new BigDecimal(repaymentPeriod), 2, RoundingMode.HALF_UP);
        result.setEmi(emi);

        // Calculate Equal Weekly Installment (EWI)
        // Approximate weeks count = repaymentPeriod * 4.345
        BigDecimal weeksCount = new BigDecimal(repaymentPeriod).multiply(new BigDecimal("4.345"));
        BigDecimal ewi = totalRepayableAmount.divide(weeksCount, 2, RoundingMode.HALF_UP);
        result.setEwi(ewi);

        return result;
    }
}

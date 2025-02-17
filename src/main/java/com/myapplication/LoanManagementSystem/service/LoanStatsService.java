package com.myapplication.LoanManagementSystem.service;



import com.myapplication.LoanManagementSystem.dto.stats.*;
import com.myapplication.LoanManagementSystem.model.Loan;
import com.myapplication.LoanManagementSystem.model.Payment;
import com.myapplication.LoanManagementSystem.model.RepaymentSchedule;
import com.myapplication.LoanManagementSystem.repository.LoanRepository;
import com.myapplication.LoanManagementSystem.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LoanStatsService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public LoanSummaryDto getLoanSummary() {
        List<Loan> loans = loanRepository.findAll();
        LoanSummaryDto summary = new LoanSummaryDto();
        summary.setTotalLoans(loans.size());

        // Total amount disbursed (sum of principal amounts)
        BigDecimal totalPrincipal = loans.stream()
                .map(Loan::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalAmountDisbursed(totalPrincipal);

        // Breakdown by status
        Map<String, Integer> statusCounts = loans.stream()
                .collect(Collectors.groupingBy(l -> l.getStatus().toString(), Collectors.summingInt(l -> 1)));
        summary.setLoansByStatus(statusCounts);

        // Average loan amount
        BigDecimal avgLoan = loans.isEmpty() ? BigDecimal.ZERO :
                totalPrincipal.divide(new BigDecimal(loans.size()), 2, RoundingMode.HALF_UP);
        summary.setAverageLoanAmount(avgLoan);

        // Average interest rate (assumes interestRate is stored as a percentage, e.g., 10)
        BigDecimal totalInterestRate = loans.stream()
                .map(Loan::getInterestRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgInterest = loans.isEmpty() ? BigDecimal.ZERO :
                totalInterestRate.divide(new BigDecimal(loans.size()), 2, RoundingMode.HALF_UP);
        summary.setAverageInterestRate(avgInterest);

        // Average repayment period in months
        double avgPeriod = loans.stream()
                .mapToInt(l -> l.getRepaymentPeriod() == null ? 0 : l.getRepaymentPeriod())
                .average().orElse(0.0);
        summary.setAverageRepaymentPeriodMonths(avgPeriod);

        return summary;
    }

    public LoansDisbursedVsPaidDto getLoansDisbursedVsPaid() {
        List<Loan> loans = loanRepository.findAll();
        LoansDisbursedVsPaidDto dto = new LoansDisbursedVsPaidDto();
        // Using principal as disbursed amount
        BigDecimal totalDisbursed = loans.stream()
                .map(Loan::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmountDisbursed(totalDisbursed);

        // Total paid: sum payments from all repayment schedules
        BigDecimal totalPaid = loans.stream()
                .flatMap(loan -> loan.getRepaymentSchedules().stream())
                .map(rs -> rs.getAmountPaid() == null ? BigDecimal.ZERO : rs.getAmountPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmountPaid(totalPaid);

        double percentagePaid = totalDisbursed.compareTo(BigDecimal.ZERO) > 0 ?
                totalPaid.divide(totalDisbursed, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue() : 0.0;
        dto.setPercentagePaid(percentagePaid);

        return dto;
    }

    public PaymentSummaryDto getPaymentSummary() {
        List<Loan> loans = loanRepository.findAll();
        PaymentSummaryDto summary = new PaymentSummaryDto();

        // Total amount repayable (sum of loan.totalRepayableAmount)
        BigDecimal totalRepayable = loans.stream()
                .map(Loan::getTotalRepayableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalAmountRepayable(totalRepayable);

        // Total amount paid from all schedules
        BigDecimal totalPaid = loans.stream()
                .flatMap(loan -> loan.getRepaymentSchedules().stream())
                .map(rs -> rs.getAmountPaid() == null ? BigDecimal.ZERO : rs.getAmountPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalAmountPaid(totalPaid);

        // Remaining balance = totalRepayable - totalPaid
        summary.setTotalRemainingBalance(totalRepayable.subtract(totalPaid));

        // Average installment payment (we average the installment amount of each loan)
        List<BigDecimal> installmentAmounts = loans.stream()
                .map(loan -> {
                    if (loan.getNumberOfInstallments() > 0) {
                        return loan.getTotalRepayableAmount()
                                .divide(new BigDecimal(loan.getNumberOfInstallments()), 2, RoundingMode.HALF_UP);
                    }
                    return BigDecimal.ZERO;
                })
                .collect(Collectors.toList());

        BigDecimal sumInstallments = installmentAmounts.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgInstallment = installmentAmounts.isEmpty() ? BigDecimal.ZERO :
                sumInstallments.divide(new BigDecimal(installmentAmounts.size()), 2, RoundingMode.HALF_UP);
        summary.setAverageInstallmentPayment(avgInstallment);

        return summary;
    }

    public TrendsDto getTrends() {
        List<Loan> loans = loanRepository.findAll();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        // Disbursement trends: group loans by the month of their creation (or dueDate) and sum principal amounts and counts.
        Map<String, TrendData> disbursementMap = loans.stream().collect(Collectors.groupingBy(
                loan -> loan.getCreatedAt().toLocalDate().format(formatter),
                Collectors.collectingAndThen(Collectors.toList(), list -> {
                    TrendData td = new TrendData();
                    td.setPeriod(list.get(0).getCreatedAt().toLocalDate().format(formatter));
                    td.setCount(list.size());
                    BigDecimal amount = list.stream()
                            .map(Loan::getPrincipalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    td.setAmount(amount);
                    return td;
                })
        ));

        // Payment trends: group payments by the month of paymentDate (from PaymentRepository)
        // For simplicity, we can derive from repayment schedules that have a non-null paymentDate.
        List<RepaymentSchedule> allSchedules = loans.stream()
                .flatMap(loan -> loan.getRepaymentSchedules().stream())
                .filter(rs -> rs.getPaymentDate() != null)
                .collect(Collectors.toList());

        Map<String, TrendData> paymentMap = allSchedules.stream().collect(Collectors.groupingBy(
                rs -> rs.getPaymentDate().format(formatter),
                Collectors.collectingAndThen(Collectors.toList(), list -> {
                    TrendData td = new TrendData();
                    td.setPeriod(list.get(0).getPaymentDate().format(formatter));
                    td.setCount(list.size());
                    BigDecimal totalPaid = list.stream()
                            .map(rs -> rs.getAmountPaid() == null ? BigDecimal.ZERO : rs.getAmountPaid())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    td.setAmount(totalPaid);
                    return td;
                })
        ));

        TrendsDto trends = new TrendsDto();
        trends.setDisbursementTrends(disbursementMap.values().stream().sorted((a, b) -> a.getPeriod().compareTo(b.getPeriod())).collect(Collectors.toList()));
        trends.setPaymentTrends(paymentMap.values().stream().sorted((a, b) -> a.getPeriod().compareTo(b.getPeriod())).collect(Collectors.toList()));
        return trends;
    }
}


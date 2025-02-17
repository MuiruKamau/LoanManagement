package com.myapplication.LoanManagementSystem.service;

import com.myapplication.LoanManagementSystem.dto.payments.BulkPaymentRequestDto;
import com.myapplication.LoanManagementSystem.dto.payments.PaymentDto;
import com.myapplication.LoanManagementSystem.dto.payments.PaymentRequestDto;
import com.myapplication.LoanManagementSystem.dto.payments.PaymentSummaryDto;
import com.myapplication.LoanManagementSystem.dto.payments.RepaymentScheduleDto;
import com.myapplication.LoanManagementSystem.model.*;
import com.myapplication.LoanManagementSystem.repository.LoanRepository;
import com.myapplication.LoanManagementSystem.repository.PaymentRepository;
import com.myapplication.LoanManagementSystem.repository.RepaymentScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RepaymentScheduleRepository scheduleRepository;

    @Autowired
    private LoanRepository loanRepository;

    /**
     * Pay a single installment by scheduleId.
     */
    public void payInstallment(Long scheduleId, PaymentRequestDto dto) {
        RepaymentSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Repayment schedule not found with id " + scheduleId));

        // Update schedule: add payment to existing amountPaid.
        BigDecimal newAmountPaid = (schedule.getAmountPaid() == null ? BigDecimal.ZERO : schedule.getAmountPaid())
                .add(dto.getPaymentAmount());
        schedule.setAmountPaid(newAmountPaid);

        // Update payment status:
        if (newAmountPaid.compareTo(schedule.getAmountDue()) >= 0) {
            schedule.setPaymentStatus(RepaymentStatus.PAID);
            schedule.setPaymentDate(dto.getPaymentDate());
        } else if (newAmountPaid.compareTo(BigDecimal.ZERO) > 0) {
            schedule.setPaymentStatus(RepaymentStatus.PARTIALLY_PAID);
        }
        scheduleRepository.save(schedule);

        // Create a Payment record for auditing
        Payment payment = new Payment();
        payment.setPaymentAmount(dto.getPaymentAmount());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setRepaymentSchedule(schedule);
        payment.setLoan(schedule.getLoan());
        paymentRepository.save(payment);

        // Check if entire loan is now paid
        Loan loan = schedule.getLoan();
        boolean allPaid = loan.getRepaymentSchedules().stream()
                .allMatch(rs -> rs.getPaymentStatus() == RepaymentStatus.PAID);
        if (allPaid) {
            loan.setStatus(LoanStatus.PAID);
            loanRepository.save(loan);
        }
    }

    /**
     * Make a bulk payment against a loan, allocating the payment from earliest to latest pending installment.
     */
    public void bulkPayment(BulkPaymentRequestDto dto) {
        Loan loan = loanRepository.findById(dto.getLoanId())
                .orElseThrow(() -> new RuntimeException("Loan not found with id " + dto.getLoanId()));

        // Sort installments by due date
        List<RepaymentSchedule> schedules = loan.getRepaymentSchedules().stream()
                .filter(rs -> rs.getDueDate() != null)
                .filter(rs -> rs.getPaymentStatus() != RepaymentStatus.PAID)
                .sorted(Comparator.comparing(RepaymentSchedule::getDueDate))
                .collect(Collectors.toList());

        BigDecimal remainingPayment = dto.getPaymentAmount();

        for (RepaymentSchedule schedule : schedules) {
            if (remainingPayment.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal alreadyPaid = schedule.getAmountPaid() == null ? BigDecimal.ZERO : schedule.getAmountPaid();
            BigDecimal installmentBalance = schedule.getAmountDue().subtract(alreadyPaid);
            BigDecimal paymentForThisInstallment = remainingPayment.min(installmentBalance);

            // Update schedule amountPaid and status
            BigDecimal newAmountPaid = alreadyPaid.add(paymentForThisInstallment);
            schedule.setAmountPaid(newAmountPaid);
            if (newAmountPaid.compareTo(schedule.getAmountDue()) >= 0) {
                schedule.setPaymentStatus(RepaymentStatus.PAID);
                schedule.setPaymentDate(dto.getPaymentDate());
            } else if (newAmountPaid.compareTo(BigDecimal.ZERO) > 0) {
                schedule.setPaymentStatus(RepaymentStatus.PARTIALLY_PAID);
            }
            scheduleRepository.save(schedule);

            // Create a Payment record for auditing
            Payment payment = new Payment();
            payment.setPaymentAmount(paymentForThisInstallment);
            payment.setPaymentDate(dto.getPaymentDate());
            payment.setRepaymentSchedule(schedule);
            payment.setLoan(loan);
            paymentRepository.save(payment);

            remainingPayment = remainingPayment.subtract(paymentForThisInstallment);
        }

        // Update loan status if all schedules are PAID
        boolean allPaid = loan.getRepaymentSchedules().stream()
                .allMatch(rs -> rs.getPaymentStatus() == RepaymentStatus.PAID);
        if (allPaid) {
            loan.setStatus(LoanStatus.PAID);
        }
        loanRepository.save(loan);
    }

    /**
     * Retrieves all repayment schedules for a given loan as DTOs.
     */
    public List<RepaymentScheduleDto> getSchedulesByLoan(Long loanId) {
        List<RepaymentSchedule> schedules = scheduleRepository.findByLoan_Id(loanId);
        List<RepaymentScheduleDto> dtos = new ArrayList<>();
        for (RepaymentSchedule s : schedules) {
            RepaymentScheduleDto dto = new RepaymentScheduleDto();
            dto.setScheduleId(s.getId());
            dto.setDueDate(s.getDueDate());
            dto.setAmountDue(s.getAmountDue());
            // Depending on the loan frequency, show either emi or ewi
            if (s.getLoan().getRepaymentFrequency() == Frequency.MONTHLY) {
                dto.setEmi(s.getEmi());
            } else if (s.getLoan().getRepaymentFrequency() == Frequency.WEEKLY) {
                dto.setEwi(s.getEwi());
            }
            dto.setAmountPaid(s.getAmountPaid());
            dto.setPaymentDate(s.getPaymentDate());
            dto.setPaymentStatus(s.getPaymentStatus());
            dto.setCreatedAt(s.getCreatedAt());
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * Retrieves all payment records for a given loan as DTOs.
     */
    public List<PaymentDto> getPaymentsByLoan(Long loanId) {
        List<Payment> payments = paymentRepository.findByLoan_Id(loanId);
        List<PaymentDto> dtos = new ArrayList<>();
        for (Payment p : payments) {
            PaymentDto dto = new PaymentDto();
            dto.setPaymentId(p.getPaymentId());
            dto.setPaymentAmount(p.getPaymentAmount());
            dto.setPaymentDate(p.getPaymentDate());
            dto.setCreatedAt(p.getCreatedAt());
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * Calculates a summary for a given loan including:
     * - TotalAmountRepayable (the loan’s total repayable amount)
     * - TotalAmountPaid (sum of all payments made)
     * - RemainingInstallments (number of schedules not fully paid)
     * - RemainingBalance (totalRepayable - totalPaid)
     */
    public PaymentSummaryDto calculatePaymentSummary(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with id " + loanId));
        BigDecimal totalPaid = loan.getRepaymentSchedules().stream()
                .map(rs -> rs.getAmountPaid() == null ? BigDecimal.ZERO : rs.getAmountPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long remainingInstallments = loan.getRepaymentSchedules().stream()
                .filter(rs -> rs.getPaymentStatus() != RepaymentStatus.PAID)
                .count();
        BigDecimal remainingBalance = loan.getTotalRepayableAmount().subtract(totalPaid);
        PaymentSummaryDto summary = new PaymentSummaryDto();
        summary.setTotalAmountRepayable(loan.getTotalRepayableAmount());
        summary.setTotalAmountPaid(totalPaid);
        summary.setRemainingInstallments((int) remainingInstallments);
        summary.setRemainingBalance(remainingBalance);
        return summary;
    }
}



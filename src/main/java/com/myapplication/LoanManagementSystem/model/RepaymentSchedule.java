package com.myapplication.LoanManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "repayment_schedule")
public class RepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    // Many repayment schedule entries belong to one loan.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    @JsonBackReference
    private Loan loan;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "amount_due")
    private BigDecimal amountDue;

    // For monthly installments, this field will hold the EMI value.
    @Column(name = "emi")
    private BigDecimal emi;

    // For weekly installments, this field can hold the EWI value.
    @Column(name = "ewi")
    private BigDecimal ewi;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid = BigDecimal.ZERO;

    // The date when payment was made.
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    // e.g., "PENDING", "PARTIALLY_PAID", "PAID"
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private RepaymentStatus paymentStatus;

    // --- New Fields for the Updated Amortization Table ---

    // Per-installment principal amount (replacing principalPortion)
    @Column(name = "principal")
    private BigDecimal principal;

    // Per-installment interest amount (replacing interestPortion)
    @Column(name = "interest")
    private BigDecimal interest;

    // The overall total repayable amount for the loan. This is constant for each schedule entry.
    @Column(name = "total_amount_payable")
    private BigDecimal totalAmountPayable;

    // These fields are intended for the summary record (typically the last element in the schedules array)
    // They will be updated as payments are made.
    @Column(name = "total_amount_paid")
    private BigDecimal totalAmountPaid;

    @Column(name = "total_remaining_balance")
    private BigDecimal totalRemainingBalance;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

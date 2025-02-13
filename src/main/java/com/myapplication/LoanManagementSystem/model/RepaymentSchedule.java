package com.myapplication.LoanManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repayment_schedule")
public class RepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long Id;

    // Many repayment schedule entries belong to one loan.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    @JsonBackReference // To avoid circular reference during JSON serialization
    private Loan loan;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "amount_due")
    private BigDecimal amountDue;

    // Equated Monthly Installment or EMI amount.
    private BigDecimal emi;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    // The date when payment was made.
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    // e.g., "Paid", "Pending", etc.
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private RepaymentStatus paymentStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();


}

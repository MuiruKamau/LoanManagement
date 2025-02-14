package com.myapplication.LoanManagementSystem.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    // How much was paid in this transaction
    private BigDecimal paymentAmount;

    // Date the payment was made
    private LocalDate paymentDate;

    // Timestamp for when this record was created
    private LocalDateTime createdAt = LocalDateTime.now();

    // Reference to the loan being paid (if you want to store it)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    // Reference to the specific installment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repayment_schedule_id", nullable = false)
    private RepaymentSchedule repaymentSchedule;
}

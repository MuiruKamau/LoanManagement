package com.myapplication.LoanManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many loans belong to one customer.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonManagedReference
    private Customer customer;

    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // For example: number of months/weeks/years.
    @Column(name = "repayment_period")
    private Integer repaymentPeriod;

    // e.g., "WEEKLY", "MONTHLY"
    @Enumerated(EnumType.STRING)
    private Frequency repaymentFrequency;

    // Computed value (e.g., principal + interest)
    @Column(name = "total_repayable_amount")
    private BigDecimal totalRepayableAmount;

    // e.g., "ACTIVE", "PAID", "DEFAULTED"
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // New field to store the number of installments generated (e.g., from your LoanCalculator)
    @Column(name = "number_of_installments")
    private int numberOfInstallments;

    // One loan can have many repayment schedules.
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepaymentSchedule> repaymentSchedules;
}


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
    @JsonManagedReference // Add JsonManagedReference here
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

    // e.g., "weeks", "months", "years"
    @Enumerated(EnumType.STRING)
    @Column(name = "repayment_frequency")
    private Frequency repaymentFrequency;


    // Computed value (can be computed in the service or stored if required)
    @Column(name = "total_repayable_amount")
    private BigDecimal totalRepayableAmount;

    // e.g., "Active", "Paid", "Defaulted"
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(name = "created_at")
    //private LocalDateTime createdAt;
    private LocalDateTime createdAt = LocalDateTime.now();


    //  One loan can have many repayment schedules.
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepaymentSchedule> repaymentSchedules;
}

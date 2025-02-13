package com.myapplication.LoanManagementSystem.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;

    @Column(name = "national_identity_card")
    private String nationalIdentityCard;

    @Column(name = "phone_number")
    private String phoneNumber;

    // This field represents the date the customer was created/registered.
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    // Optional: One customer can have many loans.
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Loan> loans;
}

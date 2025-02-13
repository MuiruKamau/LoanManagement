package com.myapplication.LoanManagementSystem.dto;



import lombok.Data;
import java.time.LocalDate;

@Data
public class CustomerRegistrationDto {
    private String firstname;
    private String lastname;
    private String nationalIdentityCard;
    private String phoneNumber;
    private LocalDate registrationDate;
}

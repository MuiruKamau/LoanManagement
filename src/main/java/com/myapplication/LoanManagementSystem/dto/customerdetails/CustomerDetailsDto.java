package com.myapplication.LoanManagementSystem.dto.customerdetails;



import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerDetailsDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String nationalIdentityCard;
    private String phoneNumber;
    private LocalDate registrationDate;
    private List<LoanDetailsDto> loans;
}

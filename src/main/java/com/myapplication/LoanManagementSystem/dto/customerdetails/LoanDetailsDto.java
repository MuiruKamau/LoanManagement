package com.myapplication.LoanManagementSystem.dto.customerdetails;



import com.myapplication.LoanManagementSystem.model.LoanStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LoanDetailsDto {
    private Long id;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private int repaymentPeriod;
    private BigDecimal totalRepayableAmount;
    private LoanStatus status;

    //private String status;
    private LocalDateTime createdAt;
    private List<RepaymentScheduleDto> repaymentSchedules;
}

package com.myapplication.LoanManagementSystem.dto.payments;

import com.myapplication.LoanManagementSystem.model.RepaymentStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RepaymentScheduleDto {
    private Long scheduleId;
    private LocalDate dueDate;
    private BigDecimal amountDue;
    private BigDecimal emi;
    private BigDecimal ewi;
    private BigDecimal amountPaid;
    private LocalDate paymentDate;
    private RepaymentStatus paymentStatus;
    private LocalDateTime createdAt;
}

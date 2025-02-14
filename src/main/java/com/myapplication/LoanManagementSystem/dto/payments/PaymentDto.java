
package com.myapplication.LoanManagementSystem.dto.payments;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private Long paymentId;
    private BigDecimal paymentAmount;
    private LocalDate paymentDate;
    private LocalDateTime createdAt;
}

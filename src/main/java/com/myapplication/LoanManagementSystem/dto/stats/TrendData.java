package com.myapplication.LoanManagementSystem.dto.stats;



import lombok.Data;
import java.math.BigDecimal;

@Data
public class TrendData {
    private String period; // e.g., "2025-01" for January 2025
    private int count;     // e.g., number of loans disbursed in that period
    private BigDecimal amount; // e.g., total principal disbursed or total payments made in that period
}


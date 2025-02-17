package com.myapplication.LoanManagementSystem.dto.stats;



import lombok.Data;
import java.util.List;

@Data
public class TrendsDto {
    private List<TrendData> disbursementTrends;
    private List<TrendData> paymentTrends;
}

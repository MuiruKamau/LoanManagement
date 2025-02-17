package com.myapplication.LoanManagementSystem.controller;



import com.myapplication.LoanManagementSystem.dto.stats.LoanSummaryDto;
import com.myapplication.LoanManagementSystem.dto.stats.LoansDisbursedVsPaidDto;
import com.myapplication.LoanManagementSystem.dto.stats.PaymentSummaryDto;
import com.myapplication.LoanManagementSystem.dto.stats.TrendsDto;
import com.myapplication.LoanManagementSystem.service.LoanStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stats")
public class LoanStats {

    @Autowired
    private LoanStatsService loanStatsService;

    @GetMapping("/loans-summary")
    public ResponseEntity<LoanSummaryDto> getLoanSummary() {
        LoanSummaryDto summary = loanStatsService.getLoanSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/loans-disbursed-vs-paid")
    public ResponseEntity<LoansDisbursedVsPaidDto> getLoansDisbursedVsPaid() {
        LoansDisbursedVsPaidDto dto = loanStatsService.getLoansDisbursedVsPaid();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/payment-summary")
    public ResponseEntity<PaymentSummaryDto> getPaymentSummary() {
        PaymentSummaryDto summary = loanStatsService.getPaymentSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/trends")
    public ResponseEntity<TrendsDto> getTrends() {
        TrendsDto trends = loanStatsService.getTrends();
        return ResponseEntity.ok(trends);
    }
}

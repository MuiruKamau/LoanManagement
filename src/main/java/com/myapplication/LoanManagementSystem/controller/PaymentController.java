package com.myapplication.LoanManagementSystem.controller;

import com.myapplication.LoanManagementSystem.dto.payments.BulkPaymentRequestDto;
import com.myapplication.LoanManagementSystem.dto.payments.PaymentRequestDto;
import com.myapplication.LoanManagementSystem.dto.payments.PaymentDto;
import com.myapplication.LoanManagementSystem.dto.payments.RepaymentScheduleDto;
import com.myapplication.LoanManagementSystem.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PaymentController handles both single-installment and bulk payments.
 * It also provides endpoints for retrieving payment history and repayment schedules for a loan.
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Single installment payment.
     * e.g., PUT /payments/repayment-schedules/1/pay
     * Request Body: { "paymentAmount": 10909.09, "paymentDate": "2025-03-15" }
     */
    @PutMapping("/repayment-schedules/{scheduleId}/pay")
    public ResponseEntity<?> payInstallment(
            @PathVariable Long scheduleId,
            @RequestBody PaymentRequestDto dto) {

        paymentService.payInstallment(scheduleId, dto);
        return ResponseEntity.ok("Installment paid successfully");
    }

    /**
     * Bulk payment endpoint.
     * e.g., POST /payments
     * Request Body: { "loanId": 5, "paymentAmount": 20000, "paymentDate": "2025-03-15" }
     */
    @PostMapping
    public ResponseEntity<?> bulkPayment(@RequestBody BulkPaymentRequestDto dto) {
        paymentService.bulkPayment(dto);
        return ResponseEntity.ok("Bulk payment processed successfully");
    }

    /**
     * Endpoint to retrieve all repayment schedules for a given loan.
     * This allows the user to select a loan first, view its installments, and then select one to pay.
     * e.g., GET /payments/loan/5/schedules
     */
    @GetMapping("/loan/{loanId}/schedules")
    public ResponseEntity<List<RepaymentScheduleDto>> getSchedulesByLoan(@PathVariable Long loanId) {
        List<RepaymentScheduleDto> schedules = paymentService.getSchedulesByLoan(loanId);
        return ResponseEntity.ok(schedules);
    }

    /**
     * Endpoint to retrieve all payment records for a given loan.
     * This shows a detailed audit history of all payment transactions for the loan.
     * e.g., GET /payments/loan/5
     */
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByLoan(@PathVariable Long loanId) {
        List<PaymentDto> payments = paymentService.getPaymentsByLoan(loanId);
        return ResponseEntity.ok(payments);
    }
}



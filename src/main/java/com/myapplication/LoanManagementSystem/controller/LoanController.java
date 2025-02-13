package com.myapplication.LoanManagementSystem.controller;

import com.myapplication.LoanManagementSystem.dto.loancalculation.*;
import com.myapplication.LoanManagementSystem.dto.LoanRequestDto;
import com.myapplication.LoanManagementSystem.model.Customer;
import com.myapplication.LoanManagementSystem.model.Loan;
import com.myapplication.LoanManagementSystem.repository.CustomerRepository;
import com.myapplication.LoanManagementSystem.service.LoanService;
import com.myapplication.LoanManagementSystem.utils.LoanCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    // Inject the CustomerRepository to fetch Customer entities by id.
    @Autowired
    private CustomerRepository customerRepository;

    // GET all loans: /loans/all
    @GetMapping("/all")
    public ResponseEntity<List<Loan>> getAllLoans(){
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    // GET a loan by ID: /loans/get/{id}
    @GetMapping("/get/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id){
        return loanService.getLoanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST endpoint for creating a new loan using LoanRequestDto: /loans/post
    @PostMapping("/post")
    public ResponseEntity<Loan> createLoan(@RequestBody LoanRequestDto dto) {
        // Fetch the customer by id provided in the DTO
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + dto.getCustomerId()));

        Loan loan = new Loan();
        // Map the DTO fields to the Loan entity
        loan.setCustomer(customer);
        loan.setPrincipalAmount(dto.getPrincipalAmount());
        loan.setInterestRate(dto.getInterestRate());
        loan.setDueDate(dto.getDueDate());
        loan.setRepaymentPeriod(dto.getRepaymentPeriod());
        loan.setRepaymentFrequency(dto.getRepaymentFrequency());
        loan.setTotalRepayableAmount(dto.getTotalRepayableAmount());
        loan.setStatus(dto.getStatus());

        Loan createdLoan = loanService.createLoan(loan);
        return ResponseEntity.ok(createdLoan);
    }

    // PUT endpoint for updating an existing loan using LoanRequestDto: /loans/update/{id}
    @PutMapping("/update/{id}")
    public ResponseEntity<Loan> updateLoan(@PathVariable Long id, @RequestBody LoanRequestDto dto) {
        Loan loanToUpdate = new Loan();
        // Optionally, you might not allow updating the customer reference.
        // If updating the customer is allowed, fetch the customer similarly as above.
        // For this example, we assume the customer remains the same.
        loanToUpdate.setPrincipalAmount(dto.getPrincipalAmount());
        loanToUpdate.setInterestRate(dto.getInterestRate());
        loanToUpdate.setDueDate(dto.getDueDate());
        loanToUpdate.setRepaymentPeriod(dto.getRepaymentPeriod());
        loanToUpdate.setRepaymentFrequency(dto.getRepaymentFrequency());
        loanToUpdate.setTotalRepayableAmount(dto.getTotalRepayableAmount());
        loanToUpdate.setStatus(dto.getStatus());
        Loan updatedLoan = loanService.updateLoan(id, loanToUpdate);
        return ResponseEntity.ok(updatedLoan);
    }

    // DELETE endpoint for removing a loan by ID: /loans/delete/{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id){
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }

    // POST endpoint for calculating loan details: /loans/calculate
    @PostMapping("/calculate")
    public ResponseEntity<LoanCalculationResponseDto> calculateLoan(@RequestBody LoanCalculationRequestDto requestDto) {
        LoanCalculationResponseDto response = LoanCalculator.calculateLoan(
                requestDto.getPrincipalAmount(),
                requestDto.getRepaymentPeriod(),
                requestDto.getInterestRate()  // Now inputted by the user
        );
        return ResponseEntity.ok(response);
    }
}



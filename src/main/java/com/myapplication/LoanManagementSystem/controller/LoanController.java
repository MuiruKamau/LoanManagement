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

import java.math.BigDecimal;
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
        loan.setInterestRate(BigDecimal.valueOf(dto.getInterestRate()));
        loan.setRepaymentPeriod(dto.getRepaymentPeriod());
        loan.setRepaymentFrequency(dto.getRepaymentFrequency());


        Loan createdLoan = loanService.createLoan(dto);
        return ResponseEntity.ok(createdLoan);
    }

    // PUT endpoint for updating an existing loan using LoanRequestDto: /loans/update/{id}
    @PutMapping("/update/{id}")
    public ResponseEntity<Loan> updateLoan(@PathVariable Long id, @RequestBody LoanRequestDto dto) {
        // Pass the DTO directly to the service update method.
        Loan updatedLoan = loanService.updateLoan(id, dto);
        return ResponseEntity.ok(updatedLoan);
    }


    /*public ResponseEntity<Loan> updateLoan(@PathVariable Long id, @RequestBody LoanRequestDto dto) {
        // First, retrieve the existing loan (which contains the full customer details)
        Loan existingLoan = loanService.getLoanById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found with id " + id));

        // Update only the updatable fields from the DTO
        existingLoan.setPrincipalAmount(dto.getPrincipalAmount());
        existingLoan.setInterestRate(BigDecimal.valueOf(dto.getInterestRate())); // converts int to BigDecimal
        existingLoan.setRepaymentPeriod(dto.getRepaymentPeriod());
        existingLoan.setRepaymentFrequency(dto.getRepaymentFrequency());

        // Optionally, you might want to recalculate totalRepayableAmount and dueDate here,
        // or leave them as is if they should not change on update.
        // For example, if you wish to re-calc, you could call:
        // LoanCalculationResponseDto calc = LoanCalculator.calculateLoan(
        //        dto.getPrincipalAmount(), dto.getRepaymentPeriod(), dto.getInterestRate(), dto.getRepaymentFrequency());
        // existingLoan.setTotalRepayableAmount(calc.getTotalRepayableAmount());
        // existingLoan.setDueDate(calc.getDueDate());
        // existingLoan.setNumberOfInstallments(calc.getNumberOfInstallments());

        // Do not update the customer field.
        Loan updatedLoan = loanService.updateLoan(id, existingLoan);
        return ResponseEntity.ok(updatedLoan);
    }*/


    /*public ResponseEntity<Loan> updateLoan(@PathVariable Long id, @RequestBody LoanRequestDto dto) {
        Loan loanToUpdate = new Loan();
        // Optionally, you might not allow updating the customer reference.
        // If updating the customer is allowed, fetch the customer similarly as above.
        // For this example, we assume the customer remains the same.
        loanToUpdate.setPrincipalAmount(dto.getPrincipalAmount());
        loanToUpdate.setInterestRate(BigDecimal.valueOf(dto.getInterestRate()));
        loanToUpdate.setRepaymentPeriod(dto.getRepaymentPeriod());
        loanToUpdate.setRepaymentFrequency(dto.getRepaymentFrequency());


        Loan updatedLoan = loanService.updateLoan(id, loanToUpdate);
        return ResponseEntity.ok(updatedLoan);
    }*/

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
                requestDto.getInterestRate(),
                requestDto.getFrequency()// Now inputted by the user
        );
        return ResponseEntity.ok(response);
    }
}



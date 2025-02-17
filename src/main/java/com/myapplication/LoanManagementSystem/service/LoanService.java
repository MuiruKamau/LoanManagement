package com.myapplication.LoanManagementSystem.service;

import com.myapplication.LoanManagementSystem.dto.LoanRequestDto;
import com.myapplication.LoanManagementSystem.model.*;
import com.myapplication.LoanManagementSystem.repository.CustomerRepository;
import com.myapplication.LoanManagementSystem.repository.LoanRepository;
import com.myapplication.LoanManagementSystem.repository.RepaymentScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    /**
     * Creates a new loan using flat interest calculations and generates an amortization schedule.
     */
    public Loan createLoan(LoanRequestDto dto) {
        // 1. Fetch the customer details
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + dto.getCustomerId()));

        // 2. Create the Loan entity
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setPrincipalAmount(dto.getPrincipalAmount());
        loan.setInterestRate(new BigDecimal(dto.getInterestRate()));
        loan.setRepaymentPeriod(dto.getRepaymentPeriod());
        loan.setRepaymentFrequency(dto.getRepaymentFrequency());
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setCreatedAt(LocalDateTime.now());

        // 3. Determine number of installments based on frequency
        int installments;
        if (loan.getRepaymentFrequency() == Frequency.MONTHLY) {
            installments = dto.getRepaymentPeriod();
        } else if (loan.getRepaymentFrequency() == Frequency.WEEKLY) {
            // Using an average of 4.33 weeks per month
            installments = (int) Math.ceil(dto.getRepaymentPeriod() * 4.33);
        } else {
            installments = dto.getRepaymentPeriod();
        }
        loan.setNumberOfInstallments(installments);

        // 4. Calculate total repayable amount using flat interest:
        // totalInterest = principal * (interestRate / 100)
        // totalRepayable = principal + totalInterest
        BigDecimal totalInterest = dto.getPrincipalAmount()
                .multiply(new BigDecimal(dto.getInterestRate()))
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal totalRepayable = dto.getPrincipalAmount().add(totalInterest);
        loan.setTotalRepayableAmount(totalRepayable);

        // 5. Set the overall loan due date based on frequency
        LocalDate baseDate = LocalDate.now();
        if (loan.getRepaymentFrequency() == Frequency.MONTHLY) {
            loan.setDueDate(baseDate.plusMonths(installments));
        } else if (loan.getRepaymentFrequency() == Frequency.WEEKLY) {
            loan.setDueDate(baseDate.plusWeeks(installments));
        }

        // 6. Generate the amortization schedule
        List<RepaymentSchedule> scheduleList = generateAmortizationSchedule(loan, installments, totalRepayable);
        loan.setRepaymentSchedules(scheduleList);

        return loanRepository.save(loan);
    }

    /**
     * Generates a flat interest amortization schedule.
     * For MONTHLY frequency, sets the installment in the "emi" field.
     * For WEEKLY frequency, sets the installment in the "ewi" field.
     */
    private List<RepaymentSchedule> generateAmortizationSchedule(Loan loan, int installments, BigDecimal totalRepayable) {
        List<RepaymentSchedule> schedules = new ArrayList<>();
        LocalDate baseDate = LocalDate.now();

        // Compute installment amount as totalRepayable divided by the number of installments
        BigDecimal installmentAmount = totalRepayable.divide(new BigDecimal(installments), 2, RoundingMode.HALF_UP);

        if (loan.getRepaymentFrequency() == Frequency.MONTHLY) {
            for (int i = 1; i <= installments; i++) {
                RepaymentSchedule schedule = new RepaymentSchedule();
                schedule.setLoan(loan);
                schedule.setDueDate(baseDate.plusMonths(i));
                schedule.setEmi(installmentAmount); // For monthly, store in emi
                // Optionally, you may set ewi to null explicitly
                schedule.setEwi(null);
                schedule.setAmountDue(installmentAmount);
                schedule.setAmountPaid(BigDecimal.ZERO);
                schedule.setPaymentStatus(RepaymentStatus.PENDING);
                schedule.setCreatedAt(LocalDateTime.now());
                // Set the overall loan total on each schedule row

                schedules.add(schedule);
            }
        } else if (loan.getRepaymentFrequency() == Frequency.WEEKLY) {
            for (int i = 1; i <= installments; i++) {
                RepaymentSchedule schedule = new RepaymentSchedule();
                schedule.setLoan(loan);
                schedule.setDueDate(baseDate.plusWeeks(i));
                schedule.setEwi(installmentAmount); // For weekly, store in ewi
                // Optionally, set emi to null explicitly
                schedule.setEmi(null);
                schedule.setAmountDue(installmentAmount);
                schedule.setAmountPaid(BigDecimal.ZERO);
                schedule.setPaymentStatus(RepaymentStatus.PENDING);
                schedule.setCreatedAt(LocalDateTime.now());

                schedules.add(schedule);
            }
        }
        return schedules;
    }

    /**
     * Updates an existing loan, recalculating computed fields and regenerating the repayment schedules.
     */
    public Loan updateLoan(Long id, LoanRequestDto dto) {
        Loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found with id " + id));

        // Update allowed fields (customer remains unchanged)
        existingLoan.setPrincipalAmount(dto.getPrincipalAmount());
        existingLoan.setInterestRate(new BigDecimal(dto.getInterestRate()));
        existingLoan.setRepaymentPeriod(dto.getRepaymentPeriod());
        existingLoan.setRepaymentFrequency(dto.getRepaymentFrequency());

        // Recalculate computed fields
        int installments;
        if (existingLoan.getRepaymentFrequency() == Frequency.MONTHLY) {
            installments = dto.getRepaymentPeriod();
        } else if (existingLoan.getRepaymentFrequency() == Frequency.WEEKLY) {
            installments = (int) Math.ceil(dto.getRepaymentPeriod() * 4.33);
        } else {
            installments = dto.getRepaymentPeriod();
        }
        existingLoan.setNumberOfInstallments(installments);

        BigDecimal totalInterest = dto.getPrincipalAmount()
                .multiply(new BigDecimal(dto.getInterestRate()))
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal totalRepayable = dto.getPrincipalAmount().add(totalInterest);
        existingLoan.setTotalRepayableAmount(totalRepayable);

        LocalDate baseDate = LocalDate.now();
        if (existingLoan.getRepaymentFrequency() == Frequency.MONTHLY) {
            existingLoan.setDueDate(baseDate.plusMonths(installments));
        } else if (existingLoan.getRepaymentFrequency() == Frequency.WEEKLY) {
            existingLoan.setDueDate(baseDate.plusWeeks(installments));
        }

        // Regenerate repayment schedules based on new parameters
        List<RepaymentSchedule> newSchedules = generateAmortizationSchedule(existingLoan, installments, totalRepayable);

        // Update the existing collection in place to avoid orphan removal issues
        if (existingLoan.getRepaymentSchedules() != null) {
            existingLoan.getRepaymentSchedules().clear();
            existingLoan.getRepaymentSchedules().addAll(newSchedules);
        } else {
            existingLoan.setRepaymentSchedules(newSchedules);
        }

        return loanRepository.save(existingLoan);
    }



    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }
}


/*package com.myapplication.LoanManagementSystem.service;

import com.myapplication.LoanManagementSystem.dto.LoanRequestDto;
import com.myapplication.LoanManagementSystem.dto.loancalculation.LoanCalculationResponseDto;
import com.myapplication.LoanManagementSystem.model.*;
import com.myapplication.LoanManagementSystem.repository.LoanRepository;
import com.myapplication.LoanManagementSystem.repository.RepaymentScheduleRepository;
import com.myapplication.LoanManagementSystem.utils.LoanCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private RepaymentScheduleRepository repaymentScheduleRepository;

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public Loan createLoan(LoanRequestDto dto) {
        // Create a new Loan entity
        Loan loan = new Loan();
        // Set provided fields from the DTO
        // (Assume that the customer lookup is handled elsewhere; here we only set the customer ID)
        Customer customer = new Customer();
        customer.setId(dto.getCustomerId());
        loan.setCustomer(customer);
        loan.setPrincipalAmount(dto.getPrincipalAmount());
        // Save interestRate as the whole number (or convert to BigDecimal if needed for display/calculations)
        loan.setInterestRate(new BigDecimal(dto.getInterestRate()));
        loan.setRepaymentPeriod(dto.getRepaymentPeriod());
        loan.setRepaymentFrequency(dto.getRepaymentFrequency());
        // Set loan status to ACTIVE by default
        loan.setStatus(LoanStatus.ACTIVE);
        // Set createdAt to current time
        loan.setCreatedAt(java.time.LocalDateTime.now());

        // Calculate loan details using the LoanCalculator
        LoanCalculationResponseDto calc = LoanCalculator.calculateLoan(
                dto.getPrincipalAmount(),
                dto.getRepaymentPeriod(),
                dto.getInterestRate(),  // whole number interest rate
                dto.getRepaymentFrequency()
        );

        // Set computed fields on the loan
        loan.setTotalRepayableAmount(calc.getTotalRepayableAmount());
        loan.setDueDate(calc.getDueDate());
        // Optionally, you might want to store the number of installments somewhere or let the front-end calculate it

        // Generate repayment schedules based on the chosen frequency
        List<RepaymentSchedule> scheduleList = new ArrayList<>();
        LocalDate baseDate = LocalDate.now(); // using current date as starting point

        if (dto.getRepaymentFrequency() == Frequency.MONTHLY) {
            // Monthly: generate one installment per month equal to EMI
            BigDecimal installmentAmount = calc.getEmi();
            for (int i = 1; i <= calc.getNumberOfInstallments(); i++) {
                RepaymentSchedule schedule = new RepaymentSchedule();
                schedule.setLoan(loan);
                schedule.setDueDate(baseDate.plusMonths(i));
                schedule.setEmi(installmentAmount);
                schedule.setAmountDue(installmentAmount);
                schedule.setPaymentStatus(RepaymentStatus.PENDING);
                scheduleList.add(schedule);
            }
        } else if (dto.getRepaymentFrequency() == Frequency.WEEKLY) {
            // Weekly: generate installments on a weekly basis using the EWI amount
            BigDecimal installmentAmount = calc.getEwi();
            for (int i = 1; i <= calc.getNumberOfInstallments(); i++) {
                RepaymentSchedule schedule = new RepaymentSchedule();
                schedule.setLoan(loan);
                schedule.setDueDate(baseDate.plusWeeks(i));
                schedule.setEmi(installmentAmount); // Stored in the same field, but later in response you can map as EWI
                schedule.setAmountDue(installmentAmount);
                schedule.setPaymentStatus(RepaymentStatus.PENDING);
                scheduleList.add(schedule);
            }
        }
        loan.setRepaymentSchedules(scheduleList);

        // Save the loan (cascading saves repayment schedules)
        return loanRepository.save(loan);
    }

    public Loan updateLoan(Long id, Loan loanDetails) {
        return loanRepository.findById(id).map(loan -> {
            loan.setPrincipalAmount(loanDetails.getPrincipalAmount());
            loan.setInterestRate(loanDetails.getInterestRate());
            loan.setRepaymentPeriod(loanDetails.getRepaymentPeriod());
            loan.setRepaymentFrequency(loanDetails.getRepaymentFrequency());
            loan.setTotalRepayableAmount(loanDetails.getTotalRepayableAmount());
            loan.setStatus(loanDetails.getStatus());
            loan.setCreatedAt(loanDetails.getCreatedAt());
            loan.setCustomer(loanDetails.getCustomer());
            return loanRepository.save(loan);
        }).orElseThrow(() -> new RuntimeException("Loan not found with id " + id));
    }

    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }
}
*/


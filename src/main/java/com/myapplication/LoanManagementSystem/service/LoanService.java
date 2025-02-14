package com.myapplication.LoanManagementSystem.service;

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


/*package com.myapplication.LoanManagementSystem.service;
import com.myapplication.LoanManagementSystem.model.Loan;
import com.myapplication.LoanManagementSystem.model.RepaymentSchedule;
import com.myapplication.LoanManagementSystem.model.RepaymentStatus;
import com.myapplication.LoanManagementSystem.repository.LoanRepository;
import com.myapplication.LoanManagementSystem.repository.RepaymentScheduleRepository;
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

    public Loan createLoan(Loan loan) {
        // Calculate EMI using totalRepayableAmount divided by repayment period
        BigDecimal emi = loan.getTotalRepayableAmount()
                .divide(new BigDecimal(loan.getRepaymentPeriod()), 2, RoundingMode.HALF_UP);

        // Generate repayment schedule entries for each month in the repayment period
        List<RepaymentSchedule> scheduleList = new ArrayList<>();
        LocalDate baseDate = loan.getCreatedAt().toLocalDate(); // Use the loan's creation date as the base date

        for (int i = 1; i <= loan.getRepaymentPeriod(); i++) {
            RepaymentSchedule schedule = new RepaymentSchedule();
            schedule.setLoan(loan);
            schedule.setDueDate(baseDate.plusMonths(i));
            schedule.setEmi(emi);
            schedule.setAmountDue(emi); // Each installment is equal to the EMI (adjust if needed)
            schedule.setPaymentStatus(RepaymentStatus.PENDING);
            // amountPaid remains at its default (BigDecimal.ZERO) and createdAt is set automatically
            scheduleList.add(schedule);
        }

        // Attach the generated repayment schedule to the loan
        loan.setRepaymentSchedules(scheduleList);

        // Persist the loan (cascade settings on the Loan entity will save the schedules too)
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
            // Update the associated customer if needed
            loan.setCustomer(loanDetails.getCustomer());
            return loanRepository.save(loan);
        }).orElseThrow(() -> new RuntimeException("Loan not found with id " + id));
    }

    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }
}
*/

/*import com.myapplication.LoanManagementSystem.model.Loan;
import com.myapplication.LoanManagementSystem.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    public List<Loan> getAllLoans(){
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id){
        return loanRepository.findById(id);
    }

    public Loan createLoan(Loan loan){
        return loanRepository.save(loan);
    }

    public Loan updateLoan(Long id, Loan loanDetails){
        return loanRepository.findById(id).map(loan -> {
            loan.setPrincipalAmount(loanDetails.getPrincipalAmount());
            loan.setInterestRate(loanDetails.getInterestRate());
            loan.setRepaymentPeriod(loanDetails.getRepaymentPeriod());
            loan.setRepaymentFrequency(loanDetails.getRepaymentFrequency());
            loan.setTotalRepayableAmount(loanDetails.getTotalRepayableAmount());
            loan.setStatus(loanDetails.getStatus());
            loan.setCreatedAt(loanDetails.getCreatedAt());
            // If necessary, update the associated customer.
            loan.setCustomer(loanDetails.getCustomer());
            return loanRepository.save(loan);
        }).orElseThrow(() -> new RuntimeException("Loan not found with id " + id));
    }

    public void deleteLoan(Long id){
        loanRepository.deleteById(id);
    }
}

 */

package com.myapplication.LoanManagementSystem.controller;

import com.myapplication.LoanManagementSystem.dto.customerdetails.CustomerDetailsDto;
import com.myapplication.LoanManagementSystem.dto.customerdetails.LoanDetailsDto;
import com.myapplication.LoanManagementSystem.dto.customerdetails.RepaymentScheduleDto;
import com.myapplication.LoanManagementSystem.dto.CustomerRegistrationDto;
import com.myapplication.LoanManagementSystem.dto.CustomerUpdateDto;
import com.myapplication.LoanManagementSystem.model.Customer;
import com.myapplication.LoanManagementSystem.model.Loan;
import com.myapplication.LoanManagementSystem.model.RepaymentSchedule;
import com.myapplication.LoanManagementSystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Basic GET for all customers (without nested details)
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers(){
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // GET a customer by ID with details (loans and repayment schedules)
    @GetMapping("/details/{id}")
    public ResponseEntity<CustomerDetailsDto> getCustomerDetailsById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + id));
        CustomerDetailsDto customerDto = mapCustomerToDetailsDto(customer);
        return ResponseEntity.ok(customerDto);
    }

    // GET all customers with details (loans and repayment schedules)
    @GetMapping("/details")
    public ResponseEntity<List<CustomerDetailsDto>> getAllCustomerDetails() {
        List<Customer> customers = customerService.getAllCustomers();
        List<CustomerDetailsDto> detailsDtos = new ArrayList<>();
        for (Customer customer : customers) {
            detailsDtos.add(mapCustomerToDetailsDto(customer));
        }
        return ResponseEntity.ok(detailsDtos);
    }

    // POST endpoint for customer registration using the registration DTO.
    @PostMapping("/register")
    public ResponseEntity<Customer> registerCustomer(@RequestBody CustomerRegistrationDto dto) {
        Customer customer = new Customer();
        customer.setFirstname(dto.getFirstname());
        customer.setLastname(dto.getLastname());
        customer.setNationalIdentityCard(dto.getNationalIdentityCard());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setRegistrationDate(dto.getRegistrationDate());
        Customer created = customerService.createCustomer(customer);
        return ResponseEntity.ok(created);
    }

    // PUT endpoint for updating a customer using the update DTO.
    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody CustomerUpdateDto dto) {
        Customer customerToUpdate = new Customer();
        customerToUpdate.setFirstname(dto.getFirstname());
        customerToUpdate.setLastname(dto.getLastname());
        customerToUpdate.setNationalIdentityCard(dto.getNationalIdentityCard());
        customerToUpdate.setPhoneNumber(dto.getPhoneNumber());
        customerToUpdate.setRegistrationDate(dto.getRegistrationDate());
        Customer updated = customerService.updateCustomer(id, customerToUpdate);
        return ResponseEntity.ok(updated);
    }

    // DELETE endpoint to remove a customer.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id){
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    // Helper method to map a Customer entity to CustomerDetailsDto (including nested loans and repayment schedules)
    private CustomerDetailsDto mapCustomerToDetailsDto(Customer customer) {
        CustomerDetailsDto customerDto = new CustomerDetailsDto();
        customerDto.setId(customer.getId());
        customerDto.setFirstname(customer.getFirstname());
        customerDto.setLastname(customer.getLastname());
        customerDto.setNationalIdentityCard(customer.getNationalIdentityCard());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setRegistrationDate(customer.getRegistrationDate());

        List<LoanDetailsDto> loanDtos = new ArrayList<>();
        if (customer.getLoans() != null) {
            for (Loan loan : customer.getLoans()) {
                LoanDetailsDto loanDto = new LoanDetailsDto();
                loanDto.setId(loan.getId());
                loanDto.setPrincipalAmount(loan.getPrincipalAmount());
                loanDto.setInterestRate(loan.getInterestRate());
                loanDto.setRepaymentPeriod(loan.getRepaymentPeriod());
                loanDto.setTotalRepayableAmount(loan.getTotalRepayableAmount());
                // Convert enum to String if necessary; for example:
                loanDto.setStatus(loan.getStatus());
                loanDto.setCreatedAt(loan.getCreatedAt());

                // Map repayment schedules for this loan
                List<RepaymentScheduleDto> scheduleDtos = new ArrayList<>();
                if (loan.getRepaymentSchedules() != null) {
                    for (RepaymentSchedule schedule : loan.getRepaymentSchedules()) {
                        RepaymentScheduleDto scheduleDto = new RepaymentScheduleDto();
                        scheduleDto.setScheduleId(schedule.getId());
                        scheduleDto.setDueDate(schedule.getDueDate());
                        scheduleDto.setAmountDue(schedule.getAmountDue());
                        scheduleDto.setEmi(schedule.getEmi());
                        scheduleDto.setAmountPaid(schedule.getAmountPaid());
                        scheduleDto.setPaymentDate(schedule.getPaymentDate());
                        scheduleDto.setPaymentStatus(schedule.getPaymentStatus());
                        scheduleDto.setCreatedAt(schedule.getCreatedAt());
                        scheduleDtos.add(scheduleDto);
                    }
                }
                loanDto.setRepaymentSchedules(scheduleDtos);
                loanDtos.add(loanDto);
            }
        }
        customerDto.setLoans(loanDtos);
        return customerDto;
    }
}



/*package com.myapplication.LoanManagementSystem.controller;

import com.myapplication.LoanManagementSystem.dto.customerdetails.*;

import com.myapplication.LoanManagementSystem.model.Customer;
import com.myapplication.LoanManagementSystem.model.Loan;
import com.myapplication.LoanManagementSystem.model.RepaymentSchedule;
import com.myapplication.LoanManagementSystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // GET all customers remains unchanged
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers(){
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // Modified GET by ID to include loans and repayment schedules.
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDetailsDto> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + id));

        CustomerDetailsDto customerDto = new CustomerDetailsDto();
        customerDto.setId(customer.getId());
        customerDto.setFirstname(customer.getFirstname());
        customerDto.setLastname(customer.getLastname());
        customerDto.setNationalIdentityCard(customer.getNationalIdentityCard());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setRegistrationDate(customer.getRegistrationDate());

        List<LoanDetailsDto> loanDtos = new ArrayList<>();
        if (customer.getLoans() != null) {
            for (Loan loan : customer.getLoans()) {
                LoanDetailsDto loanDto = new LoanDetailsDto();
                loanDto.setId(loan.getId());
                loanDto.setPrincipalAmount(loan.getPrincipalAmount());
                loanDto.setInterestRate(loan.getInterestRate());
                loanDto.setRepaymentPeriod(loan.getRepaymentPeriod());
                loanDto.setTotalRepayableAmount(loan.getTotalRepayableAmount());
                loanDto.setStatus(loan.getStatus());
                loanDto.setCreatedAt(loan.getCreatedAt());

                // Map repayment schedules for the loan
                List<RepaymentScheduleDto> scheduleDtos = new ArrayList<>();
                if (loan.getRepaymentSchedules() != null) {
                    for (RepaymentSchedule schedule : loan.getRepaymentSchedules()) {
                        RepaymentScheduleDto scheduleDto = new RepaymentScheduleDto();
                        scheduleDto.setScheduleId(schedule.getId());
                        scheduleDto.setDueDate(schedule.getDueDate());
                        scheduleDto.setAmountDue(schedule.getAmountDue());
                        scheduleDto.setEmi(schedule.getEmi());
                        scheduleDto.setAmountPaid(schedule.getAmountPaid());
                        scheduleDto.setPaymentDate(schedule.getPaymentDate());
                        scheduleDto.setPaymentStatus(schedule.getPaymentStatus());
                        scheduleDto.setCreatedAt(schedule.getCreatedAt());
                        scheduleDtos.add(scheduleDto);
                    }
                }
                loanDto.setRepaymentSchedules(scheduleDtos);
                loanDtos.add(loanDto);
            }
        }
        customerDto.setLoans(loanDtos);
        return ResponseEntity.ok(customerDto);
    }

    // POST endpoint for customer registration using the registration DTO.
    @PostMapping("/register")
    public ResponseEntity<Customer> registerCustomer(@RequestBody com.myapplication.LoanManagementSystem.dto.CustomerRegistrationDto dto) {
        Customer customer = new Customer();
        customer.setFirstname(dto.getFirstname());
        customer.setLastname(dto.getLastname());
        customer.setNationalIdentityCard(dto.getNationalIdentityCard());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setRegistrationDate(dto.getRegistrationDate());

        Customer created = customerService.createCustomer(customer);
        return ResponseEntity.ok(created);
    }

    // PUT endpoint for updating a customer using the update DTO.
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody com.myapplication.LoanManagementSystem.dto.CustomerUpdateDto dto) {
        Customer customerToUpdate = new Customer();
        customerToUpdate.setFirstname(dto.getFirstname());
        customerToUpdate.setLastname(dto.getLastname());
        customerToUpdate.setNationalIdentityCard(dto.getNationalIdentityCard());
        customerToUpdate.setPhoneNumber(dto.getPhoneNumber());
        customerToUpdate.setRegistrationDate(dto.getRegistrationDate());

        Customer updated = customerService.updateCustomer(id, customerToUpdate);
        return ResponseEntity.ok(updated);
    }

    // DELETE endpoint to remove a customer.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id){
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}

*/






/*package com.myapplication.LoanManagementSystem.controller;

import com.myapplication.LoanManagementSystem.dto.CustomerRegistrationDto;
import com.myapplication.LoanManagementSystem.dto.CustomerUpdateDto;
import com.myapplication.LoanManagementSystem.model.Customer;
import com.myapplication.LoanManagementSystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // GET all customers
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers(){
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // GET a customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id){
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST endpoint for customer registration using the registration DTO.
    @PostMapping("/register")
    public ResponseEntity<Customer> registerCustomer(@RequestBody CustomerRegistrationDto dto) {
        // Map DTO to the Customer entity
        Customer customer = new Customer();
        customer.setFirstname(dto.getFirstname());
        customer.setLastname(dto.getLastname());
        customer.setNationalIdentityCard(dto.getNationalIdentityCard());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setRegistrationDate(dto.getRegistrationDate());

        Customer created = customerService.createCustomer(customer);
        return ResponseEntity.ok(created);
    }

    // PUT endpoint for updating a customer using the update DTO.
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody CustomerUpdateDto dto) {
        // Map the update DTO to a Customer entity (only updating the allowed fields)
        Customer customerToUpdate = new Customer();
        customerToUpdate.setFirstname(dto.getFirstname());
        customerToUpdate.setLastname(dto.getLastname());
        customerToUpdate.setNationalIdentityCard(dto.getNationalIdentityCard());
        customerToUpdate.setPhoneNumber(dto.getPhoneNumber());
        customerToUpdate.setRegistrationDate(dto.getRegistrationDate());

        Customer updated = customerService.updateCustomer(id, customerToUpdate);
        return ResponseEntity.ok(updated);
    }

    // DELETE endpoint to remove a customer.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id){
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
*/

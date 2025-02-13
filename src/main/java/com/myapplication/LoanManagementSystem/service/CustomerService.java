package com.myapplication.LoanManagementSystem.service;


import com.myapplication.LoanManagementSystem.model.Customer;
import com.myapplication.LoanManagementSystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id){
        return customerRepository.findById(id);
    }

    public Customer createCustomer(Customer customer){
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customerDetails){
        return customerRepository.findById(id).map(customer -> {
            customer.setFirstname(customerDetails.getFirstname());
            customer.setLastname(customerDetails.getLastname());
            customer.setNationalIdentityCard(customerDetails.getNationalIdentityCard());
            customer.setPhoneNumber(customerDetails.getPhoneNumber());
            customer.setRegistrationDate(customerDetails.getRegistrationDate());
            return customerRepository.save(customer);
        }).orElseThrow(() -> new RuntimeException("Customer not found with id " + id));
    }

    public void deleteCustomer(Long id){
        customerRepository.deleteById(id);
    }
}

package com.myapplication.LoanManagementSystem.service;


import com.myapplication.LoanManagementSystem.model.RepaymentSchedule;
import com.myapplication.LoanManagementSystem.repository.RepaymentScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepaymentScheduleService {

    @Autowired
    private RepaymentScheduleRepository repaymentScheduleRepository;

    public List<RepaymentSchedule> getAllSchedules(){
        return repaymentScheduleRepository.findAll();
    }

    public Optional<RepaymentSchedule> getScheduleById(Long id){
        return repaymentScheduleRepository.findById(id);
    }

    public RepaymentSchedule createSchedule(RepaymentSchedule schedule){
        return repaymentScheduleRepository.save(schedule);
    }

    public RepaymentSchedule updateSchedule(Long id, RepaymentSchedule scheduleDetails){
        return repaymentScheduleRepository.findById(id).map(schedule -> {
            schedule.setDueDate(scheduleDetails.getDueDate());
            schedule.setAmountDue(scheduleDetails.getAmountDue());
            schedule.setEmi(scheduleDetails.getEmi());
            schedule.setAmountPaid(scheduleDetails.getAmountPaid());
            schedule.setPaymentDate(scheduleDetails.getPaymentDate());
            schedule.setPaymentStatus(scheduleDetails.getPaymentStatus());
            schedule.setCreatedAt(scheduleDetails.getCreatedAt());
            // Update the associated loan if needed.
            schedule.setLoan(scheduleDetails.getLoan());
            return repaymentScheduleRepository.save(schedule);
        }).orElseThrow(() -> new RuntimeException("Repayment Schedule not found with id " + id));
    }

    public void deleteSchedule(Long id){
        repaymentScheduleRepository.deleteById(id);
    }
}

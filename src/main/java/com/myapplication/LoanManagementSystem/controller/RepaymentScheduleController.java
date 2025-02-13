package com.myapplication.LoanManagementSystem.controller;



import com.myapplication.LoanManagementSystem.model.RepaymentSchedule;
import com.myapplication.LoanManagementSystem.service.RepaymentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/repayment-schedules")
public class RepaymentScheduleController {

    @Autowired
    private RepaymentScheduleService repaymentScheduleService;

    @GetMapping
    public ResponseEntity<List<RepaymentSchedule>> getAllSchedules(){
        return ResponseEntity.ok(repaymentScheduleService.getAllSchedules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepaymentSchedule> getScheduleById(@PathVariable Long id){
        return repaymentScheduleService.getScheduleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RepaymentSchedule> createSchedule(@RequestBody RepaymentSchedule schedule){
        RepaymentSchedule created = repaymentScheduleService.createSchedule(schedule);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepaymentSchedule> updateSchedule(@PathVariable Long id, @RequestBody RepaymentSchedule schedule){
        RepaymentSchedule updated = repaymentScheduleService.updateSchedule(id, schedule);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id){
        repaymentScheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}

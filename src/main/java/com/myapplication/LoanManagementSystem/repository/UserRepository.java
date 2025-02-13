package com.myapplication.LoanManagementSystem.repository;


import org.springframework.stereotype.Repository;
import com.myapplication.LoanManagementSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
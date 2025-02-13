package com.myapplication.LoanManagementSystem.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="users")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String email;


}

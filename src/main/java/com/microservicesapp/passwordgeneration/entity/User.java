package com.microservicesapp.passwordgeneration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;
    private String password;
    private String email;
    private Date createAt;
    private LocalDateTime loginAt;
    private LocalDateTime updateAt;
    private LocalDateTime logoutAt;
    private LocalDateTime exiryDate;
}

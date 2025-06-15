package com.example.SWP391.entity.Otp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "otp_verification")
public class OtpVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String otp;
    private LocalDateTime expirationTime;
    private LocalDateTime createdAt;
    @PrePersist
    public void prePersit(){
        this.createdAt=LocalDateTime.now();
    }
}

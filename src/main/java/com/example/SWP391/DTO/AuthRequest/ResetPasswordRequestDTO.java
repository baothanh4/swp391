package com.example.SWP391.DTO.AuthRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Data
public class ResetPasswordRequestDTO {
    private String email;
    private String otp;
    private String newPassword;
    private String confirmPassword;
}

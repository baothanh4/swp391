package com.example.SWP391.DTO.AuthRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Data
public class ResetPasswordRequestDTO {
    @Email
    @NotBlank
    private String email;
    private String otp;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String confirmPassword;
}

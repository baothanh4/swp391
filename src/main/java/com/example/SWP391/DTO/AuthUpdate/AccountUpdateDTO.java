package com.example.SWP391.DTO.AuthUpdate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AccountUpdateDTO {
    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone phải có 10 chữ số")
    private String phone;

    private String role;
    private boolean enabled;
}

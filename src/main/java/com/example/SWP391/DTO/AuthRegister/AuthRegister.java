package com.example.SWP391.DTO.AuthRegister;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class AuthRegister {
    @NotBlank(message = "username không được để trống")
    private String username;

    @NotBlank(message = "không được để trống")
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone phải có 10 chữ số")
    private String phone;

    @NotBlank
    private String fullName;

    private String role;
    @NotBlank
    private LocalDate dob;
    @NotBlank
    private String address;

    @NotNull
    @Min(value = 0, message = "Giới tính chỉ được là 0 (Nam) hoặc 1 (Nữ)")
    @Max(value = 1, message = "Giới tính chỉ được là 0 (Nam) hoặc 1 (Nữ)")
    private Integer gender;
}

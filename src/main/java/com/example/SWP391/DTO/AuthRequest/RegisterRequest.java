package com.example.SWP391.DTO.AuthRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class RegisterRequest {
    @NotBlank
    private String username;
    private String password;
    @Email
    private String email;
    private String phone;

    @JsonProperty("fullname")
    private String fullName;
    private String role;
}



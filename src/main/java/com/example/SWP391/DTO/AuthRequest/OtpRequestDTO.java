package com.example.SWP391.DTO.AuthRequest;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OtpRequestDTO {
    private String email;
    private String otp;
}

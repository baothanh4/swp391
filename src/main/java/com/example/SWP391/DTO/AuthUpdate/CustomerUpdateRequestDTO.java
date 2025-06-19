package com.example.SWP391.DTO.AuthUpdate;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
public class CustomerUpdateRequestDTO {
    private String fullName;
    private LocalDate DOB;
    private String email;
    private String phone;
    private String address;
    private Integer gender;
}

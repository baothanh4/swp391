package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StaffDTO {
    private String staffID;
    private String fullname;
    private LocalDate DOB;
    private String email;
    private String phone;
    private String address;
    private int gender;
}

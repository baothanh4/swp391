package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TestSubjectInfoDTO {
    private String fullname;
    private LocalDate dateOfBirth;
    private int gender;
    private String phone;
    private String email;
    private String relationship;
    private String sampleType;
    private String idNumber;
}

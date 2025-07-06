package com.example.SWP391.DTO.EntityDTO;

import com.example.SWP391.entity.Otp.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerDTO {
    private String fullName;
    private LocalDate dob;
    private String email;
    private String phone;
    private String address;
    private Integer gender;
    private LocalDate createAt;
    private List<FeedbackDTO> feedbackList;
}

package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingUpdateDTO {
    private String status;
    private LocalDate appointmentTime;
    private String timeRange;
}

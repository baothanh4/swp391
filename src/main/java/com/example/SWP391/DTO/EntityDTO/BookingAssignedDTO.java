package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingAssignedDTO {
    private Long assignedID;
    private int bookingID;
    private String customerName;
    private String staffName;
    private LocalDateTime lastUpdate;
    private String appointmentTime;
    private LocalDate appointmentDate;
    private String serviceType;
    private String status;
}


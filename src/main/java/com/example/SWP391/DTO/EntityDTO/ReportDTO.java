package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportDTO {
    private int reportID;
    private String appointmentTime;
    private String customerName;
    private String note;
    private String status;
    private Long assignedID;
    private String managerID;
    private String staffID;
    private int bookingID;
    private LocalDate appointmentDate;
}

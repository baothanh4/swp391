package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerBookingDTO {
    private String collectionMethod;
    private String paymentMethod;
    private String kitName;
    private String serviceName;
    private LocalDate appointmentTime;
    private String Address;
    private String status;
    private String note;
    private String mediationMethod;
    private String timeRange;
}

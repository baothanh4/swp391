package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookingDTO {
    private int bookingId;

    private String collectionMethod;
    private String paymentMethod;

    private LocalDate appointmentTime;
    private String timeRange;

    private String status;
    private String note;

    private float cost;
    private String mediationMethod;
    private float additionalCost;
    private float totalCost;

    private boolean expressService;
    private String address;
    private String paymentCode;
    private String kitID;         // dùng String nếu BioKit có ID kiểu chuỗi như K001
    private String serviceID;
    private String customerID;

    private String customerName;

 // Hai người test
    private List<TestSubjectInfoDTO> testSubjects;
}
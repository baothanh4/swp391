package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class BookingResponseDTO {
    private int bookingId;

    private String collectionMethod;
    private String paymentMethod;
    private String paymentCode;
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

    private String kitID;         // dùng String nếu BioKit có ID kiểu chuỗi như K001
    private String serviceID;
    private String customerID;

    private String customerName;
    private String vnpUrl;
    private String qrCode;
    // Hai người test
    private List<TestSubjectInfoDTO> testSubjects;
}

package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;
@Data
public class OrderDTO {
    private int bookingID;
    private String customerName;
    private String service;
    private String kitID;
    private String status;
    private String staffID;
    private LocalDate date;
    private String timeRange;
}

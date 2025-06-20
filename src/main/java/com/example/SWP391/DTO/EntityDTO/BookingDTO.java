package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class BookingDTO {
    private int bookingID;
    private String bookingType;
    private String paymentMethod;
    private String sampleMethod;
    private LocalDate request_date;
    private String status;
    private String mediationMethod;
    private String note;
    private String customerID;
    private String serviceID;

    private String kitID;
    private boolean expressService;
    private float cost;
    private float additionalCost;


}

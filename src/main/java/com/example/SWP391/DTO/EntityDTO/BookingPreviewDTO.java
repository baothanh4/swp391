package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

@Data
public class BookingPreviewDTO {
    private String customerID;
    private String serviceID;
    private String kitID;
    private boolean expressService;
    private String mediationMethod;
    private float totalCost;
    private String generatedPaymentCode;
    private String qrUrl;
}

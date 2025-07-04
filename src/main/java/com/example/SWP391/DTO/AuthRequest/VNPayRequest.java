package com.example.SWP391.DTO.AuthRequest;

import lombok.Data;

@Data
public class VNPayRequest {
    private String paymentCode;
    private long totalCost;
}

package com.example.SWP391.DTO.AuthRequest;

import lombok.Data;

@Data
public class VNPayRequest {
    private String orderId;
    private long amount;
}

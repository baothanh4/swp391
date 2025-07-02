package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

@Data
public class KitTransactionDTO {
    private Long transactionID;
    private String kitID;
    private boolean isReceived;
    private int BookingID;
}

package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingUpdateDTO {

    private String kitID;
    private String mediationMethod;
    private String status;
    private String serviceID;
}

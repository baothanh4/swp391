package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SlotDTO {
    private Long id;
    private String timeRange;
    private LocalDate date;

    private int currentBooking;
}

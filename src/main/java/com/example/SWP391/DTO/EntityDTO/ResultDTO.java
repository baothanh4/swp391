package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ResultDTO {
    private int resultID;
    private int bookingID;
    private String relationship;
    private String conclusion;
    private float confidencePercentage;

    private boolean isAvailable;
    private LocalDateTime updateAt;
    private LocalDate createAt;
    private String staffID;
}

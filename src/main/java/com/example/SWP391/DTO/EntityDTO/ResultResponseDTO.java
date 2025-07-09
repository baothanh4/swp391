package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ResultResponseDTO {
    private int resultID;
    private int bookingID;
    private boolean isAvailable;
    private LocalDate createAt;
    private LocalDateTime updateAt;
    private String staffID;
    private String relationship;
    private String conclusion;
    private float confidencePercentage;

}

package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

@Data
public class ResultResponseDTO {
    private String relationship;
    private String conclusion;
    private float confidencePercentage;

}

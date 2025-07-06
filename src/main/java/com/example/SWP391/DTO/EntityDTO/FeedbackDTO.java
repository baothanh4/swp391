package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FeedbackDTO {
    private String customerID;
    private int bookingID;
    private String title;
    private String content;
    private int rating;
    private LocalDate createAt;

}

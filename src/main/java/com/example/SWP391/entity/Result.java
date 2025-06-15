package com.example.SWP391.entity;

import jakarta.persistence.Id;

import java.time.LocalDate;

public class Result {
    @Id
    private int resultId;

    private int bookingId;
    private String relationship;
    private int isAvailable;
    private LocalDate create_at;
    private String file_path;
}

package com.example.SWP391.entity;

import jakarta.persistence.Id;

import java.time.LocalDate;

public class Sample {
    @Id
    private int sampleId;

    private int bookingId;
    private String code;
    private LocalDate collection_date;
    private int isDeleted;
    private String sampel_type;
}

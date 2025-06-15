package com.example.SWP391.entity;

import jakarta.persistence.Id;

import java.time.LocalDate;

public class TestSubjectInfoHistory {
    @Id
    private int historyId;

    private int infoID;
    private LocalDate updated_at;
    private String status;
}

package com.example.SWP391.entity;

import jakarta.persistence.Id;

import java.time.LocalDate;

public class Blog {
    @Id
    private int blogId;

    private String title;
    private String content;
    private LocalDate create_at;
    private int isDeleted;
}

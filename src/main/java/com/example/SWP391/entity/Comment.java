package com.example.SWP391.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity

public class Comment {

    @Id
    private int commentID;

    private int blogID;
    private String customerID;
    private String content;
    private LocalDate create_at;
}

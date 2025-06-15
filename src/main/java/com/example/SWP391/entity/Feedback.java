package com.example.SWP391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Feedback")
@Getter
@Setter
public class Feedback {
    @Id
    private int feedbackID;


    private String customerID;

    private int rating;
    private String comment;
    private LocalDate create_at;
    private int isDeleted;

}

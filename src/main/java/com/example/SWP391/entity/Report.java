package com.example.SWP391.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "Report")
public class Report {
    @Id
    private int ReportID;

    private String Type;
    private String Content;
    private LocalDate create_at;
    private String create_by;
    private int quantity;
    private int number_of_customer;
}

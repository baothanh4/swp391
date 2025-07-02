package com.example.SWP391.entity;

import com.example.SWP391.entity.Booking.Booking;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String timeRange;
    private LocalDate date;

    private int currentBooking=0;



}

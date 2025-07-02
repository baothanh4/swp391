package com.example.SWP391.entity;

import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.User.Customer;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feedbackID;


    private String title;
    private String content;
    private int rating;
    private LocalDate createAt;

    @ManyToOne
    @JoinColumn(name = "customerID",nullable = false)
    private Customer customer;

    @OneToOne
    @JoinColumn(name = "bookingID",unique = true,nullable = false)
    private Booking booking;


}

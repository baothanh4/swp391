package com.example.SWP391.entity;

import com.example.SWP391.entity.Booking.Booking;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Setter
@Getter
@Table(name = "Sample")
public class Sample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sampleId;

    @OneToOne
    @JoinColumn(name = "BookingID",nullable = false)
    private Booking booking;

    @Column(name = "Code")
    private String code;
    @Column(name = "CollectionDate")
    private LocalDate collectionDate;
    @Column(name = "fullname")
    private String fullname;
    @Column(name = "SampleName")
    private String sampleName;
}

package com.example.SWP391.entity;

import com.example.SWP391.entity.Booking.Booking;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "TestSubjectInfo")
@Getter
@Setter
public class TestSubjectInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long infoId;

    @ManyToOne
    @JoinColumn(name = "BookingID", nullable = false)
    private Booking booking;

    @Column(name = "FullName")
    private String fullname;

    @Column(name = "DateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "Gender")
    private int gender; // 1 = Nam, 2 = Nữ

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Email")
    private String email;

    @Column(name = "Relationship")
    private String relationship;

    @Column(name = "SampleType")
    private String sampleType; // tóc / móng / niêm mạc miệng / máu

    @Column(name = "IdNumber")
    private String idNumber; // CCCD/CMND

    @OneToMany(mappedBy = "testSubjectInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestSubjectInfoHistory> histories;
}
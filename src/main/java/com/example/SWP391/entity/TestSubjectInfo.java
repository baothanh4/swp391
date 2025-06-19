package com.example.SWP391.entity;

import com.example.SWP391.entity.Booking.Booking;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @Column(name = "fullname")
    private String fullname;
    @Column(name="relationship")
    private String relationship;
    @Column(name="BioType")
    private String bioType;

    // ✅ Quan hệ ngược: 1 TestSubjectInfo - N TestSubjectInfoHistory
    @OneToMany(mappedBy = "testSubjectInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestSubjectInfoHistory> histories;
}

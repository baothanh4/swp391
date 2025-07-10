package com.example.SWP391.entity;

import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.User.Staff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "Result")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int resultId;

    @OneToOne
    @JoinColumn(name = "BookingID", referencedColumnName = "BookingID", nullable = false, unique = true)
    private Booking booking;

    @Column(name = "Relationship")
    private String relationship;

    @Column(name = "Conclusion")
    private String conclusion; // Ví dụ: "Có quan hệ huyết thống"

    @Column(name = "ConfidencePercentage")
    private float confidencePercentage;

    @Column(name = "MatchingPercentage")
    private String matchingPercentage;

    @Column(name = "IsAvailable")
    private boolean isAvailable;

    @Column(name = "CreateAt")
    private LocalDate createAt;

    @Column(name="Deadline")
    private LocalDate deadline;

    @Column(name = "UpdateAt")
    private LocalDateTime updateAt;

    @Column(name = "StaffID")
    private String staffID;
}

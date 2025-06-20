package com.example.SWP391.entity.Booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class BookingAssigned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignedID;

    private String customerName;
    private String serviceType;
    private String status;

    @JsonProperty("StaffFullName")
    private String assignedStaff;
    @UpdateTimestamp
    private LocalDateTime lastUpdate;

    @OneToOne
    @JoinColumn(name = "BookingID",nullable = false)
    private Booking booking;
}

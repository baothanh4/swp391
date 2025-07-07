package com.example.SWP391.entity.Booking;

import com.example.SWP391.entity.User.Manager;
import com.example.SWP391.entity.User.Staff;
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

    @Column(name = "AppointmentTime")
    private String appointmentTime;

    @Column(name = "AppointmentDate")
    private LocalDate appointmentDate;

    @OneToOne
    @JoinColumn(name = "BookingID",nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "StaffID")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "ManagerID")
    private Manager manager;
}

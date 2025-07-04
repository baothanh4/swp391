package com.example.SWP391.entity.Booking;

import com.example.SWP391.entity.*;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.entity.User.Staff;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Booking")
public class Booking {
        @Id
        @Column(name = "BookingID")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int bookingId;

        @Column(name = "CollectionMethod")
        private String collectionMethod;

        @Column(name = "PaymentCode", unique = true, nullable = true)
        private String paymentCode;

        @Column(name = "paymentMethod")
        private String paymentMethod;

        @Column(name = "AppointmentTime")
        private LocalDate appointmentTime;

        @Column(name = "TimeRange")
        private String timeRange;

        @Column(name = "Status")
        private String status;

        @Column(name = "Note")
        private String note;

        @Column(name = "Cost")
        private float cost;

        @Column(name = "MediationMethod")
        private String mediationMethod;

        @Column(name = "AdditionalCost")
        private float additionalCost;

        @Column(name = "TotalCost")
        private float totalCost;

        @Column(name = "ExpressService")
        private boolean expressService;

        @Column(name = "Address")
        private String address;

        @ManyToOne
        @JoinColumn(name = "KitID", nullable = false)
        private BioKit bioKit;

        @ManyToOne
        @JoinColumn(name = "ServiceID", nullable = false)
        private Service service;

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "customerID")
        @ManyToOne
        @JoinColumn(name = "CustomerID", nullable = false)
        private Customer customer;


        @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
        private Sample sample;

        @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<TestSubjectInfo> testSubjectInfos;

        @ManyToOne
        @JoinColumn(name = "StaffID")
        private Staff staff;

        @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
        private Feedback feedback;

        @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
        private Result result;
}
package com.example.SWP391.entity;

import com.example.SWP391.entity.User.Customer;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@Table(name = "Booking")
public class Booking {
        @Id
        @Column(name = "BookingID")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int bookingId;

        @Column(name="BookingType")
        private String bookingType;
        @Column(name="paymentMethod")
        private String paymentMethod;
        @Column(name="sampleMethod")
        private String sampleMethod;
        @Column(name="RequestDate")
        private LocalDate request_date;
        @Column(name="Status")
        private String status;
        @Column(name="Note")
        private String note;

        @Column(name="Cost")
        private float cost;

        @Column(name = "MediationMethod")
        private String mediationMethod;

        @Column(name="AdditionalCost")
        private float additionalCost;

        @Column(name="TotalCost")
        private float totalCost;

        @ManyToOne
        @JoinColumn(name = "KitID",nullable = false)
        private BioKit bioKit;

        @ManyToOne
        @JoinColumn(name = "ServiceID", nullable = false)
        private Service service;

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "customerID")
        @ManyToOne
        @JoinColumn(name = "CustomerID", nullable = false)
        private Customer customer;

}

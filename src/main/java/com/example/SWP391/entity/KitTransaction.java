package com.example.SWP391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class KitTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID")
    private Long TransactionID;

    @OneToOne
    @JoinColumn(name = "BookingID",nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "KitID",nullable = false)
    private BioKit bioKit;

    @Column(name = "IsReceived")
    private boolean isReceived;
}

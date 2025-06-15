package com.example.SWP391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Service")
@Setter
@Getter
public class Service {
    @Id
    private String serviceId;

    @Column(name = "Name")
    private String name;
    @Column(name="Type")
    private String type;
    @Column(name="Cost")
    private float cost;
    @Column(name="EstimatedTime")
    private String estimatedTime;
    @Column(name="ExpressService")
    private float expressService;

}

package com.example.SWP391.entity.User;

import com.example.SWP391.entity.Otp.Account;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name="Staff")
public class Staff {
    @Id
    private String staffID;

    @OneToOne
    @JoinColumn(name = "AccountID",nullable = false)
    private Account account;

    @Column(name = "full_name")
    @JsonProperty("full_name")
    private String fullName;

    @Column(name="isAvaliable")
    private boolean isAvaliable;

    @Column(name = "DOB")
    private LocalDate DOB;
    @Column(name = "Email")
    private String email;
    @Column(name = "Phone")
    private String phone;
    @Column(name = "Address")
    private String address;
    @Column(name="Gender")
    private int gender;


}

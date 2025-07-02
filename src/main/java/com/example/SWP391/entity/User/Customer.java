package com.example.SWP391.entity.User;

import com.example.SWP391.entity.Feedback;
import com.example.SWP391.entity.Otp.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
public class Customer {

    @Id
    private String customerID;

    @OneToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @Column(name = "full_name")
    private String fullName;
    private LocalDate dob;
    private String email;
    private String phone;
    private String address;
    private Integer gender;
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<Feedback> feedbackList;
}

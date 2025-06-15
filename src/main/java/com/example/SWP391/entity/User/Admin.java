package com.example.SWP391.entity.User;

import com.example.SWP391.entity.Otp.Account;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Admin")
public class Admin {
    @Id
    private String adminID;

    @OneToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @Column(name = "full_name")
    @JsonProperty("full_name")
    private String fullName;
}

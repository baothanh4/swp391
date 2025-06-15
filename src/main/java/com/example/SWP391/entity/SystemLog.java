package com.example.SWP391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "SystemLog")
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String action;
    private LocalDateTime timestamp;

    private String ipAddress;

    public SystemLog(Long id, String username, String action, LocalDateTime timestamp, String ipAddress) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
    }

    public SystemLog() {
    }
}

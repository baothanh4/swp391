package com.example.SWP391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@Table(name = "TestSubjectInfoHistory")
public class TestSubjectInfoHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "InfoID", nullable = false)
    private TestSubjectInfo testSubjectInfo;

    @Column(name="UpdatedAt")
    private LocalDate updated_at;

}

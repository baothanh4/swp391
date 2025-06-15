package com.example.SWP391.entity;

import jakarta.persistence.Id;

public class TestSubjectInfo {
    @Id
    private int InfoId;

    private int bookingId;
    private int testSubjectId;
}

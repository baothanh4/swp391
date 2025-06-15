package com.example.SWP391.entity;

import jakarta.persistence.Id;

import javax.xml.crypto.Data;
import java.util.Date;

public class TestSubject {
    @Id
    private int testSubjectId;

    private int bookingId;
    private String full_name;
    private String relationship;
    private Date DOB;
    private String phone;
    private String email;
    private String address;
}

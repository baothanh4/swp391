    package com.example.SWP391.entity;

    import com.example.SWP391.entity.Booking.BookingAssigned;
    import com.example.SWP391.entity.User.Manager;
    import com.example.SWP391.entity.User.Staff;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.time.LocalDate;

    @Entity
    @Table(name = "Report")
    @Getter
    @Setter
    public class Report {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int reportID;

        @Column(name = "AppointmentTime")
        private String appointmentTime;

        @Column(name = "BookingID")
        private int bookingID;

        @Column(name = "CustomerName")
        private String customerName;

        @Column(name = "Status")
        private String status;

        @Column(name ="AppointmentDate")
        private LocalDate appointmentDate;

        @Column(name = "Note", columnDefinition = "TEXT")
        private String note;

        @Column(name = "IsApproved",nullable = false)
        private Boolean isApproved;

        //  Gắn report với nhân viên đã viết báo cáo
        @ManyToOne
        @JoinColumn(name = "StaffID")
        private Staff staff;

        // 👇 Gắn với phân công nào (BookingAssigned)
        @OneToOne
        @JoinColumn(name = "AssignedID")
        private BookingAssigned bookingAssigned;

        // 👇 Manager nhận báo cáo
        @ManyToOne
        @JoinColumn(name = "ManagerID")
        private Manager manager;



    }

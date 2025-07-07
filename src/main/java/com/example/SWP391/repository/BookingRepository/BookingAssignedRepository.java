package com.example.SWP391.repository.BookingRepository;

import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Booking.BookingAssigned;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingAssignedRepository extends JpaRepository<BookingAssigned,Long> {
    BookingAssigned findByBooking(Booking booking);
    @Query("SELECT ba FROM BookingAssigned ba " +
            "WHERE ba.staff.staffID = :staffID " +
            "AND ba.appointmentDate = :date")
    List<BookingAssigned> findByStaffAndAppointmentDate(@Param("staffID") String staffID,
                                                        @Param("date") LocalDate date);
}

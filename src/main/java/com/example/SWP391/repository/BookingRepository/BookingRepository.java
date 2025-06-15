package com.example.SWP391.repository.BookingRepository;

import com.example.SWP391.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Query("SELECT COALESCE(MAX(b.bookingId), 0) FROM Booking b")
    int findMaxBookingId();
}

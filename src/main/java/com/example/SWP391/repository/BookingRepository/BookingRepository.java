package com.example.SWP391.repository.BookingRepository;

import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.User.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Integer> {
    @Query("SELECT COALESCE(MAX(b.bookingId), 0) FROM Booking b")
    int findMaxBookingId();
    List<Booking> findByCustomer(Customer customer);
}

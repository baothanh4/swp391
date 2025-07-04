package com.example.SWP391.repository.BookingRepository;

import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result,Integer> {
    Optional<Result> findByBooking(Booking booking);
    List<Result> findByStaffID(String staffID);
}

package com.example.SWP391.repository.BookingRepository;

import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Booking.BookingAssigned;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingAssignedRepository extends JpaRepository<BookingAssigned,Long> {
    BookingAssigned findByBooking(Booking booking);
}

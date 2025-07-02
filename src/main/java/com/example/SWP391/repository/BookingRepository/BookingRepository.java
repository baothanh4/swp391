package com.example.SWP391.repository.BookingRepository;

import com.example.SWP391.DTO.EntityDTO.BookingDTO;
import com.example.SWP391.DTO.EntityDTO.OrderDTO;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.User.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Integer> {
    @Query("SELECT COALESCE(MAX(b.bookingId), 0) FROM Booking b")
    int findMaxBookingId();
    List<Booking> findByCustomer(Customer customer);
    @Query("SELECT b FROM Booking b WHERE b.staff.staffID = :staffID")
    List<Booking> getAllBookingsByStaffId(@Param("staffID") String staffID);
    List<Booking> findByCustomer_CustomerID(String customerID);
    @Query(value = "SELECT TOP 1 PaymentCode FROM Booking WHERE PaymentCode LIKE 'B____' ORDER BY PaymentCode DESC", nativeQuery = true)
    String findLastPaymentCode();

    Optional<Booking> findByPaymentCode(String paymentCode);
}

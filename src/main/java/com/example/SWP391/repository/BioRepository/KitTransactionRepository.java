package com.example.SWP391.repository.BioRepository;

import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.KitTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KitTransactionRepository extends JpaRepository<KitTransaction, Long> {
    Optional<KitTransaction>findByBooking(Booking booking);
}

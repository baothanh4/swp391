package com.example.SWP391.repository.BookingRepository;

import com.example.SWP391.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot,Long> {
    Optional<Slot> findByTimeRangeAndDate(String timeRange, LocalDate date);
}

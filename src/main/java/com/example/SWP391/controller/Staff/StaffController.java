package com.example.SWP391.controller.Staff;

import com.example.SWP391.DTO.EntityDTO.BookingUpdateDTO;
import com.example.SWP391.entity.Booking;
import com.example.SWP391.service.Booking.BookingService;
import com.example.SWP391.service.Staff.UpdateBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    @Autowired
    private UpdateBooking updateBooking;


    @PatchMapping("/updateBooking/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable("id") int bookingID, @RequestBody BookingUpdateDTO dto){
        try {
            Booking booking= updateBooking.updateBookingFromDTO(bookingID,dto);
            return ResponseEntity.ok(booking);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

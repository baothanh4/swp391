package com.example.SWP391.controller.Staff;

import com.example.SWP391.DTO.EntityDTO.BookingUpdateDTO;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.KitTransaction;
import com.example.SWP391.service.Kit.KitTransactionService;
import com.example.SWP391.service.Staff.UpdateBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    @Autowired
    private UpdateBooking updateBooking;
    @Autowired private KitTransactionService kitTransactionService;

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
    @PatchMapping("/KitTransaction/{id}")
    public ResponseEntity<?> updateIsReceived(@PathVariable("id") Long transactionID, @RequestBody KitTransaction kit){
        try {
            KitTransaction kitTransaction=kitTransactionService.updateIsReceived(transactionID,kit.isReceived());
            return ResponseEntity.ok("Update kit transaction successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body("Update kit transaction failed");
        }
    }
}

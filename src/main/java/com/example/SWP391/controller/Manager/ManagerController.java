package com.example.SWP391.controller.Manager;

import com.example.SWP391.entity.BioKit;
import com.example.SWP391.entity.Booking.BookingAssigned;
import com.example.SWP391.repository.BookingRepository.BookingAssignedRepository;
import com.example.SWP391.service.Kit.KitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    @Autowired BookingAssignedRepository bookingAssignedRepository;
    @Autowired
    KitService kitService;
    @PatchMapping("/assign-staff/{assignedId}")
    public ResponseEntity<?> assignStaff(@PathVariable(name = "assignedId") Long assignedId, @RequestBody String staffFullName){
        staffFullName = staffFullName.replace("\"", "").trim();
        BookingAssigned assigned=bookingAssignedRepository.findById(assignedId).orElseThrow(() -> new IllegalArgumentException("Booking assigned not found"));
        assigned.setAssignedStaff(staffFullName);
        bookingAssignedRepository.save(assigned);
        return ResponseEntity.ok("Assigned successfully");
    }
    @PatchMapping("/kit/{KitID}")
    public ResponseEntity<?> updateKitQuantity(@PathVariable(name="KitID") String kitID, @RequestBody BioKit bioKit){
        kitService.updateQuantity(kitID,bioKit.getQuantity());
        return ResponseEntity.ok("Add Kit successfully");
    }

}

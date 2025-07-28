package com.example.SWP391.controller.Booking;

import com.example.SWP391.DTO.EntityDTO.BookingDTO;
import com.example.SWP391.DTO.EntityDTO.BookingResponseDTO;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Booking.BookingAssigned;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.service.Booking.BookingService;
import com.example.SWP391.service.Booking.QRService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    @Autowired private final BookingRepository bookingRepository;
    @Autowired private final BookingService bookingService;
    @Autowired private final QRService qrService;


    @GetMapping("/bookings")
    public ResponseEntity<?> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        List<BookingDTO> bookingDTOS = bookings.stream()
                .map(this::convertDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingDTOS);
    }


    @PostMapping("/bookings/{serviceID}/{customerID}")
    public ResponseEntity<?> createBooking(
            @PathVariable String serviceID,
            @PathVariable String customerID,
            @RequestBody BookingDTO bookingDTO,
            HttpServletRequest request) {
        try {
            BookingResponseDTO response = bookingService.createBookingFromDTO2(
                    bookingDTO, serviceID, customerID, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal Error: " + e.getMessage());
        }
    }
    @PatchMapping("/{bookingID}/status")
    public ResponseEntity<?> UpdateStatus(@PathVariable(name = "bookingID") int bookingID){
        Booking booking=bookingRepository.findById(bookingID).orElseThrow(()->new IllegalArgumentException("Booking not found"));

        booking.setStatus("Payment Confirmed");
        bookingRepository.save(booking);
        return ResponseEntity.ok("Update status successfully");
    }

    public BookingDTO convertDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setBookingId(booking.getBookingId());
        bookingDTO.setCollectionMethod(booking.getCollectionMethod());
        bookingDTO.setPaymentMethod(booking.getPaymentMethod());
        bookingDTO.setAppointmentTime(booking.getAppointmentTime());
        bookingDTO.setTimeRange(booking.getTimeRange());
        bookingDTO.setStatus(booking.getStatus());
        bookingDTO.setNote(booking.getNote());
        bookingDTO.setCost(booking.getCost());
        bookingDTO.setMediationMethod(booking.getMediationMethod());
        bookingDTO.setAdditionalCost(booking.getAdditionalCost());
        bookingDTO.setTotalCost(booking.getTotalCost());
        bookingDTO.setExpressService(booking.isExpressService());
        bookingDTO.setAddress(booking.getAddress());
        bookingDTO.setPaymentCode(booking.getPaymentCode());

        if (booking.getBioKit() != null) {
            bookingDTO.setKitID(booking.getBioKit().getKitID());
        }

        if (booking.getService() != null) {
            bookingDTO.setServiceID(booking.getService().getServiceId());
        }

        if (booking.getCustomer() != null) {
            bookingDTO.setCustomerID(booking.getCustomer().getCustomerID());
        }

        return bookingDTO;
    }
}

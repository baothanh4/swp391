package com.example.SWP391.controller.Booking;

import com.example.SWP391.DTO.EntityDTO.BookingDTO;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.service.Booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    @Autowired
    private BookingRepository bookingRepository;
    private final BookingService bookingService;
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDTO>> getAllBooking(){

        List<Booking> bookings = bookingRepository.findAll();
        List<BookingDTO> bookingDTOs = bookings.stream().map(this::convertDTO).collect(Collectors.toList());
        return ResponseEntity.ok(bookingDTOs);

    }
    public BookingDTO convertDTO(Booking booking){
        BookingDTO dto=new BookingDTO();
        dto.setBookingID(booking.getBookingId());
        dto.setBookingType(booking.getBookingType());
        dto.setPaymentMethod(booking.getPaymentMethod());
        dto.setSampleMethod(booking.getSampleMethod());
        dto.setRequest_date(booking.getRequest_date());
        dto.setNote(booking.getNote());
        dto.setStatus(booking.getStatus());
        dto.setReturnResultMethod(booking.getReturnResultMethod());
        dto.setMediationMethod(booking.getMediationMethod());
        dto.setExpressService(booking.isExpressService());
        if(booking.getCustomer()!=null){
            dto.setCustomerID(booking.getCustomer().getCustomerID());
            dto.setCost(booking.getService().getCost());
        }
        dto.setAdditionalCost(booking.getAdditionalCost());

        if(booking.getService()!=null){
            dto.setServiceID(booking.getService().getServiceId());
        }
        if (booking.getBioKit() != null) {
            dto.setKitID(booking.getBioKit().getKitID());
        }
        return dto;
    }

    @PostMapping("/{serviceID}/{customerID}")
    public ResponseEntity<?> createBookingWithoutQR(@PathVariable String serviceID,
                                                    @PathVariable String customerID,
                                                    @RequestBody BookingDTO dto) {
        try {
            Booking booking = bookingService.createBookingFromDTO2(dto, serviceID, customerID);
            BookingDTO response = bookingService.convertDTO(booking);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
        }
    }

}

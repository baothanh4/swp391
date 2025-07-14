package com.example.SWP391.service.Staff;

import com.example.SWP391.DTO.AuthUpdate.UpdateRequestDTO;
import com.example.SWP391.DTO.EntityDTO.BookingUpdateDTO;
import com.example.SWP391.DTO.EntityDTO.OrderDTO;
import com.example.SWP391.entity.BioKit;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Booking.BookingAssigned;
import com.example.SWP391.entity.KitTransaction;
import com.example.SWP391.entity.User.Staff;
import com.example.SWP391.repository.BioRepository.BioKitRepository;
import com.example.SWP391.repository.BioRepository.KitTransactionRepository;
import com.example.SWP391.repository.BookingRepository.BookingAssignedRepository;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.repository.BookingRepository.ServiceRepository;
import com.example.SWP391.repository.UserRepository.StaffRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateBooking {

    @Autowired private BookingRepository bookingRepo;
    @Autowired private BioKitRepository bioKitRepo;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private BookingAssignedRepository bookingAssignedRepository;
    @Autowired private StaffRepository staffRepository;
    @Autowired private KitTransactionRepository kitTransactionRepository;

    @Transactional
    public Booking updateBookingFromDTO(int bookingId, BookingUpdateDTO dto) throws Exception {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if ("COMPLETED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Cannot update a completed booking.");
        }

        // Cập nhật status (nếu có)
        if (dto.getStatus() != null) {
            booking.setStatus(dto.getStatus());

            if("In Progress".equalsIgnoreCase(dto.getStatus())){
                KitTransaction kitTransaction=kitTransactionRepository.findByBooking(booking).orElseThrow(()->new IllegalArgumentException("Kit transaction not found"));
                if(kitTransaction!=null){
                    kitTransaction.setReceived(true);
                    kitTransactionRepository.save(kitTransaction);
                }

            }

            BookingAssigned assigned = bookingAssignedRepository.findByBooking(booking);
            if (assigned != null) {
                assigned.setStatus(dto.getStatus());
                assigned.setLastUpdate(LocalDateTime.now());
                bookingAssignedRepository.save(assigned);
            }
        }

        // Cập nhật ngày và khung giờ nếu có
        if (dto.getAppointmentTime()!=null) {
            booking.setAppointmentTime(dto.getAppointmentTime());

        }

        if (dto.getTimeRange() != null) {
            booking.setTimeRange(dto.getTimeRange());
        }

        return bookingRepo.save(booking);
    }

    public Staff updateInfo(String staffID, UpdateRequestDTO request) {
        Staff staff = staffRepository.findById(staffID)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        if (request.getFullName() != null) staff.setFullName(request.getFullName());
        if (request.getDOB() != null) staff.setDOB(request.getDOB());
        if (request.getEmail() != null) {
            Optional<Staff> existing = staffRepository.findByEmail(request.getEmail());
            if (existing.isPresent() && !existing.get().getStaffID().equals(staffID)) {
                throw new IllegalStateException("Email already used in another account");
            }
            staff.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            Optional<Staff> existing = staffRepository.findByPhone(request.getPhone());
            if (existing.isPresent() && !existing.get().getStaffID().equals(staffID)) {
                throw new IllegalStateException("Phone already used in another account");
            }
            staff.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) staff.setAddress(request.getAddress());
        if (request.getGender() != null) staff.setGender(request.getGender());

        return staffRepository.save(staff);
    }

    private float getMediationFee(String method) {
        if (method == null) return 0;
        return switch (method.trim().toLowerCase()) {
            case "home" -> 300_000f;
            case "at facility" -> 0f;
            case "postal delivery" -> 50_000f;
            default -> 0f;
        };
    }

    public OrderDTO convertDTO(Booking booking) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBookingID(booking.getBookingId());
        orderDTO.setCustomerName(booking.getCustomer().getFullName());
        orderDTO.setService(booking.getService().getType());
        orderDTO.setStatus(booking.getStatus());
        orderDTO.setDate(booking.getAppointmentTime());
        orderDTO.setTimeRange(booking.getTimeRange());

        if (booking.getBioKit() != null) {
            orderDTO.setKitID(booking.getBioKit().getKitID());
        }
        if (booking.getStaff() != null) {
            orderDTO.setStaffID(booking.getStaff().getStaffID());
        }

        return orderDTO;
    }
}

package com.example.SWP391.service.Staff;

import com.example.SWP391.DTO.EntityDTO.BookingUpdateDTO;
import com.example.SWP391.entity.BioKit;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Booking.BookingAssigned;
import com.example.SWP391.entity.KitTransaction;
import com.example.SWP391.entity.Service;
import com.example.SWP391.repository.BioRepository.BioKitRepository;
import com.example.SWP391.repository.BioRepository.KitTransactionRepository;
import com.example.SWP391.repository.BookingRepository.BookingAssignedRepository;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.repository.BookingRepository.ServiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class UpdateBooking {
    @Autowired private BookingRepository bookingRepo;
    @Autowired private BioKitRepository bioKitRepo;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private KitTransactionRepository kitTransactionRepo;
    @Autowired private BookingAssignedRepository bookingAssignedRepository;

    @Transactional
    public Booking updateBookingFromDTO(int bookingId, BookingUpdateDTO dto) throws Exception {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Không cho cập nhật nếu đã completed
        if ("COMPLETED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Cannot update a completed booking.");
        }

        // 1. Cập nhật Service nếu có
        if (dto.getServiceID() != null) {
            Service service = serviceRepository.findById(dto.getServiceID())
                    .orElseThrow(() -> new IllegalArgumentException("Service not found"));
            booking.setService(service);
            booking.setCost(service.getCost());
        }

        // 2. Cập nhật Kit nếu có
        if (dto.getKitID() != null && !dto.getKitID().equals(booking.getBioKit().getKitID())) {
            // Hoàn lại Kit cũ
            BioKit oldKit = booking.getBioKit();
            oldKit.setQuantity(oldKit.getQuantity() + 1);
            oldKit.setIsSelled(oldKit.getIsSelled() - 1);
            oldKit.setAvailable(true);
            bioKitRepo.save(oldKit);

            // Trừ Kit mới
            BioKit newKit = bioKitRepo.findById(dto.getKitID())
                    .orElseThrow(() -> new IllegalArgumentException("Kit not found"));
            if (!newKit.isAvailable() || newKit.getQuantity() <= 0) {
                throw new IllegalStateException("New kit is not available or out of stock");
            }
            newKit.setQuantity(newKit.getQuantity() - 1);
            newKit.setIsSelled(newKit.getIsSelled() + 1);
            newKit.setAvailable(newKit.getQuantity() > 0);
            bioKitRepo.save(newKit);

            booking.setBioKit(newKit);

            // Cập nhật KitTransaction
            KitTransaction tx = kitTransactionRepo.findByBooking(booking)
                    .orElseThrow(() -> new IllegalStateException("KitTransaction not found"));
            tx.setBioKit(newKit);
            kitTransactionRepo.save(tx);
        }

        // 3. Cập nhật Mediation Method
        if (dto.getMediationMethod() != null) {
            booking.setMediationMethod(dto.getMediationMethod());
        }

        // 4. Cập nhật Status
        if (dto.getStatus() != null) {
            booking.setStatus(dto.getStatus());

            BookingAssigned assigned=bookingAssignedRepository.findByBooking(booking);
            if(assigned!=null){
                assigned.setStatus(dto.getStatus());
                assigned.setLastUpdate(LocalDateTime.now());
                bookingAssignedRepository.save(assigned);
            }
        }

        // 5. Tính lại chi phí
        float mediationFee = getMediationFee(booking.getMediationMethod());
        float cost = booking.getService().getCost();
        float additionalCost = mediationFee;
        booking.setAdditionalCost(additionalCost);
        booking.setTotalCost(cost + additionalCost);

        return bookingRepo.save(booking);
    }
    private float getMediationFee(String method) {
        if (method == null) return 0;
        return switch (method.trim().toLowerCase()) {
            case "Home" -> 300_000f;
            case "at facility" -> 0f;
            case "postal delivery" -> 50_000f;
            default -> 0f;
        };
    }
}

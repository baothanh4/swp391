package com.example.SWP391.service.Booking;

import com.example.SWP391.DTO.EntityDTO.BookingDTO;
import com.example.SWP391.entity.BioKit;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Booking.BookingAssigned;
import com.example.SWP391.entity.KitTransaction;
import com.example.SWP391.entity.Service;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.repository.BioRepository.BioKitRepository;
import com.example.SWP391.repository.BioRepository.KitTransactionRepository;
import com.example.SWP391.repository.BookingRepository.BookingAssignedRepository;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.repository.BookingRepository.ServiceRepository;
import com.example.SWP391.repository.UserRepository.CustomerRepository;
import com.example.SWP391.service.Email.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;


@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BookingService {
    @Autowired private final BioKitRepository bioKitRepo;
    @Autowired private final BookingRepository bookingRepo;
    @Autowired private final KitTransactionRepository kitTransactionRepo;
    @Autowired private final CustomerRepository customerRepository;
    @Autowired private final ServiceRepository serviceRepository;
    @Autowired private final BookingAssignedRepository bookingAssignedRepository;
    @Autowired private final EmailService emailService;
    public BookingDTO convertDTO(Booking booking){
        int lastId = bookingRepo.findMaxBookingId();
        int newId = lastId + 1;
        BookingDTO dto=new BookingDTO();
        booking.setBookingId(newId);
        dto.setBookingType(booking.getBookingType());
        dto.setPaymentMethod(booking.getPaymentMethod());
        dto.setSampleMethod(booking.getSampleMethod());
        dto.setRequest_date(booking.getRequest_date());
        dto.setStatus(booking.getStatus());
        dto.setMediationMethod(booking.getMediationMethod());

        if(booking.getCustomer()!=null){
            dto.setCustomerID(booking.getCustomer().getCustomerID());
        }

        if(booking.getService()!=null){
            dto.setServiceID(booking.getService().getServiceId());
        }
        return dto;
    }


    @Transactional
    public Booking createBookingFromDTO2(BookingDTO dto, String serviceID, String customerID) throws Exception {
        Customer customer = customerRepository.findById(customerID)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Service service = serviceRepository.findById(serviceID)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        BioKit kit = bioKitRepo.findById(dto.getKitID())
                .orElseThrow(() -> new IllegalArgumentException("Kit not found"));

        if (!kit.isAvailable() || kit.getQuantity() <= 0) {
            throw new IllegalStateException("Kit is not available or out of stock");
        }

        float cost = service.getCost();
        float mediationFee = getMediationFee(dto.getMediationMethod());

        float expressFee=0;
        if(dto.isExpressService()){
            expressFee=service.getExpressPrice();
        }
        float additionalCost = dto.getAdditionalCost() + mediationFee+expressFee;
        float totalCost = cost + additionalCost;

        Booking booking = new Booking();
        booking.setBookingType(dto.getBookingType());
        booking.setPaymentMethod(dto.getPaymentMethod());
        booking.setSampleMethod(dto.getSampleMethod());
        booking.setRequest_date(dto.getRequest_date() != null ? dto.getRequest_date() : LocalDate.now());
        booking.setNote(dto.getNote());

        booking.setMediationMethod(dto.getMediationMethod());
        booking.setStatus("Pending payment");

        booking.setExpressService(dto.isExpressService());
        booking.setCost(cost);
        booking.setAdditionalCost(additionalCost);
        booking.setTotalCost(totalCost);
        booking.setCustomer(customer);
        booking.setService(service);
        booking.setBioKit(kit);

        Booking saved = bookingRepo.save(booking);

        // Ghi nhận Kit Transaction
        KitTransaction tx = new KitTransaction();
        tx.setBooking(saved);
        tx.setBioKit(kit);
        tx.setReceived(false);
        kitTransactionRepo.save(tx);

        // Cập nhật tồn kho Kit
        kit.setQuantity(kit.getQuantity() - 1);
        kit.setIsSelled(kit.getIsSelled() + 1);
        kit.setAvailable(kit.getQuantity() > 0);
        bioKitRepo.save(kit);

        String subject = "Xác nhận đặt lịch xét nghiệm thành công";
        String content = "Thông tin đặt lịch:\n"
                + "Mã khách hàng: " + customer.getCustomerID() + "\n"
                + "Tên khách hàng: " + customer.getFullName() + "\n"
                + "Loại đặt lịch: " + dto.getBookingType() + "\n"
                + "Dịch vụ: " + service.getName() + "\n"
                + "Phương thức thanh toán: " + dto.getPaymentMethod() + "\n"
                + "Ngày yêu cầu: " + booking.getRequest_date() + "\n"

                + "Bộ kit: " + kit.getName() + "\n"
                + "Chi phí dịch vụ: " + cost + "\n"
                + "Chi phí thêm: " + additionalCost + "\n"
                + "Dịch vụ nhanh: " + (dto.isExpressService() ? "Có" : "Không") + "\n"
                + "Tổng cộng: " + totalCost + " VND";
        emailService.sendBookingConfirmation(customer.getEmail(), subject, content);

        return saved;
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
    public Booking createBookingAssigned(Booking booking){
        Booking saved=bookingRepo.save(booking);

        BookingAssigned assigned=new BookingAssigned();
        assigned.setBooking(saved);
        assigned.setCustomerName(saved.getCustomer().getFullName());
        assigned.setServiceType(saved.getService().getType());
        assigned.setStatus(saved.getStatus());
        assigned.setAssignedStaff(null);

        bookingAssignedRepository.save(assigned);

        return saved;
    }


}

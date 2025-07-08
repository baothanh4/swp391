// BookingService.java
package com.example.SWP391.service.Booking;

import com.example.SWP391.DTO.EntityDTO.BookingDTO;
import com.example.SWP391.DTO.EntityDTO.BookingResponseDTO;
import com.example.SWP391.DTO.EntityDTO.ResultDTO;
import com.example.SWP391.DTO.EntityDTO.TestSubjectInfoDTO;
import com.example.SWP391.entity.*;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Booking.BookingAssigned;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.repository.BioRepository.*;
import com.example.SWP391.repository.BookingRepository.*;
import com.example.SWP391.repository.TestSubject.TestSubjectInfoRepository;
import com.example.SWP391.repository.UserRepository.CustomerRepository;
import com.example.SWP391.service.Email.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    @Autowired private final BioKitRepository bioKitRepo;
    @Autowired private final BookingRepository bookingRepo;
    @Autowired private final KitTransactionRepository kitTransactionRepo;
    @Autowired private final CustomerRepository customerRepository;
    @Autowired private final ServiceRepository serviceRepository;
    @Autowired private final BookingAssignedRepository bookingAssignedRepository;
    @Autowired private final EmailService emailService;
    @Autowired private final SlotRepository slotRepository;
    @Autowired private final SampleRepository sampleRepository;
    @Autowired private final TestSubjectInfoRepository testSubjectInfoRepository;
    @Autowired private final QRService qrService;
    @Autowired private final VNPayService vnPayService;
    @Autowired private final ResultRepository resultRepository;

    @Transactional
    public BookingResponseDTO createBookingFromDTO2(BookingDTO dto, String serviceID, String customerID, HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Customer customer = customerRepository.findById(customerID)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        com.example.SWP391.entity.Service service = serviceRepository.findById(serviceID)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        BioKit kit = bioKitRepo.findById(dto.getKitID())
                .orElseThrow(() -> new IllegalArgumentException("Kit not found"));

        if (!kit.isAvailable() || kit.getQuantity() <= 0) {
            throw new IllegalStateException("Kit is not available or out of stock");
        }

        float cost = service.getCost();
        float mediationFee = getMediationFee(dto.getMediationMethod(), dto.isExpressService());
        float expressPrice = dto.isExpressService() ? service.getExpressPrice() : 0f;
        float additionalCost = mediationFee + expressPrice;
        float totalCost = cost + additionalCost;

        Booking booking = new Booking();
        booking.setCollectionMethod(dto.getCollectionMethod());
        booking.setPaymentMethod(dto.getPaymentMethod());

        if ("Cash".equalsIgnoreCase(dto.getPaymentMethod()) ||
                "VNPAY".equalsIgnoreCase(dto.getPaymentMethod())) {
            booking.setPaymentCode(generateNextPaymentCode());
        }

        booking.setAppointmentTime(dto.getAppointmentTime());
        booking.setTimeRange(dto.getTimeRange());
        booking.setNote(dto.getNote());
        booking.setAddress(dto.getAddress());
        booking.setMediationMethod(normalizeMediationMethod(dto.getMediationMethod()));
        booking.setStatus("Awaiting confirm");
        booking.setExpressService(dto.isExpressService());
        booking.setCost(cost);
        booking.setAdditionalCost(additionalCost);
        booking.setTotalCost(totalCost);
        booking.setCustomer(customer);
        booking.setService(service);
        booking.setBioKit(kit);

        Slot slot = slotRepository.findByTimeRangeAndDate(booking.getTimeRange(), booking.getAppointmentTime())
                .orElseGet(() -> {
                    Slot newSlot = new Slot();
                    newSlot.setDate(booking.getAppointmentTime());
                    newSlot.setTimeRange(booking.getTimeRange());
                    newSlot.setCurrentBooking(0);
                    return slotRepository.save(newSlot);
                });

        if (slot.getCurrentBooking() >= 3) {
            throw new IllegalStateException("Slot is full");
        }
        slot.setCurrentBooking(slot.getCurrentBooking() + 1);
        slotRepository.save(slot);

        Booking savedBooking = bookingRepo.save(booking);

        KitTransaction tx = new KitTransaction();
        tx.setBooking(savedBooking);
        tx.setBioKit(kit);
        tx.setReceived(false);
        kitTransactionRepo.save(tx);

        String prefix = kit.getKitID().equalsIgnoreCase("K001") ? "PF" : "GF";
        int count = sampleRepository.countByCodeStartingWith(prefix);
        String generatedCode = String.format("%s%03d", prefix, count + 1);

        Sample sample = new Sample();
        sample.setBooking(savedBooking);
        sample.setCode(generatedCode);
        sample.setCollectionDate(savedBooking.getAppointmentTime());
        sample.setFullname(customer.getFullName());
        sampleRepository.save(sample);

        kit.setQuantity(kit.getQuantity() - 1);
        kit.setIsSelled(kit.getIsSelled() + 1);
        kit.setAvailable(kit.getQuantity() > 0);
        bioKitRepo.save(kit);

        for (TestSubjectInfoDTO infoDTO : dto.getTestSubjects()) {
            TestSubjectInfo info = new TestSubjectInfo();
            info.setBooking(savedBooking);
            info.setFullname(infoDTO.getFullname());
            info.setDateOfBirth(infoDTO.getDateOfBirth());
            info.setGender(infoDTO.getGender());
            info.setPhone(infoDTO.getPhone());
            info.setEmail(infoDTO.getEmail());
            info.setRelationship(infoDTO.getRelationship());
            info.setSampleType(infoDTO.getSampleType());
            info.setIdNumber(infoDTO.getIdNumber());
            testSubjectInfoRepository.save(info);
        }

        try {
            createResult(savedBooking);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        try {
            emailService.sendBookingConfirmationEnglish(customer.getEmail(), savedBooking);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            createBookingAssigned(savedBooking);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // ✅ Trả về BookingResponseDTO
        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(savedBooking.getBookingId());
        response.setPaymentCode(savedBooking.getPaymentCode());
        response.setCollectionMethod(savedBooking.getCollectionMethod());
        response.setPaymentMethod(savedBooking.getPaymentMethod());
        response.setAppointmentTime(savedBooking.getAppointmentTime());
        response.setTimeRange(savedBooking.getTimeRange());
        response.setStatus(savedBooking.getStatus());
        response.setNote(savedBooking.getNote());
        response.setCost(savedBooking.getCost());
        response.setMediationMethod(savedBooking.getMediationMethod());
        response.setAdditionalCost(savedBooking.getAdditionalCost());
        response.setTotalCost(savedBooking.getTotalCost());
        response.setExpressService(savedBooking.isExpressService());
        response.setAddress(savedBooking.getAddress());
        response.setKitID(savedBooking.getBioKit().getKitID());
        response.setServiceID(savedBooking.getService().getServiceId());
        response.setCustomerID(savedBooking.getCustomer().getCustomerID());
        response.setCustomerName(savedBooking.getCustomer().getFullName());

        if ("VNPAY".equalsIgnoreCase(savedBooking.getPaymentMethod())) {
            String clientIp = request.getRemoteAddr();
            String vnpUrl = vnPayService.createVNPayUrl(
                    savedBooking.getService().getServiceId(),
                    Math.round(savedBooking.getTotalCost()),
                    clientIp,savedBooking.getBookingId(),
                    savedBooking.isExpressService()
            );
            response.setVnpUrl(vnpUrl);
        }

        if ("QR".equalsIgnoreCase(savedBooking.getPaymentMethod())) {
            String qrUrl = qrService.generatedQRUrl(savedBooking.getBookingId());
            response.setQrCode(qrUrl);
        }


        List<TestSubjectInfoDTO> subjectDTOs = testSubjectInfoRepository.findByBooking(savedBooking).stream()
                .map(subject -> {
                    TestSubjectInfoDTO dtoSub = new TestSubjectInfoDTO();
                    dtoSub.setFullname(subject.getFullname());
                    dtoSub.setDateOfBirth(subject.getDateOfBirth());
                    dtoSub.setGender(subject.getGender());
                    dtoSub.setPhone(subject.getPhone());
                    dtoSub.setEmail(subject.getEmail());
                    dtoSub.setRelationship(subject.getRelationship());
                    dtoSub.setSampleType(subject.getSampleType());
                    dtoSub.setIdNumber(subject.getIdNumber());
                    return dtoSub;
                }).collect(Collectors.toList());

        response.setTestSubjects(subjectDTOs);

        return response;
    }


    private float getMediationFee(String method, boolean isExpress) {
        if (method == null) return 0;
        return switch (method.trim().toLowerCase()) {
            case "staff-collection" -> isExpress ? 0f : 500_000f;
            case "postal-delivery" -> 250_000f;
            case "walk-in" -> 0f;
            default -> 0f;
        };
    }

    public Booking createResult(Booking booking){
        Booking saved=bookingRepo.save(booking);
        Result result=new Result();
        result.setBooking(saved);
        result.setRelationship(null);
        result.setConclusion(null);
        result.setConfidencePercentage(0);
        result.setAvailable(false);
        result.setCreateAt(LocalDate.now());
        result.setUpdateAt(LocalDateTime.now());
        resultRepository.save(result);
        return saved;
    }

    public Booking createBookingAssigned(Booking booking) {
        Booking saved = bookingRepo.save(booking);
        BookingAssigned assigned = new BookingAssigned();
        assigned.setBooking(saved);
        assigned.setCustomerName(saved.getCustomer().getFullName());
        assigned.setServiceType(saved.getService().getType());
        assigned.setStatus(saved.getStatus());
        assigned.setAppointmentTime(saved.getTimeRange());
        assigned.setAppointmentDate(saved.getAppointmentTime());
        assigned.setAssignedStaff(null);
        bookingAssignedRepository.save(assigned);
        return saved;
    }

    private String normalizeMediationMethod(String method) {
        if (method == null) return "";
        return switch (method.trim().toLowerCase()) {
            case "staffarrival", "staff arrival" -> "Staff Collection";
            case "postal", "postal delivery" -> "Postal Delivery";
            case "walkin", "walk-in" -> "Walk-in Service";
            default -> capitalizeWords(method);
        };
    }

    private String capitalizeWords(String input) {
        if (input == null || input.isBlank()) return "";
        return Arrays.stream(input.trim().split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public String generateNextPaymentCode() {
        String lastCode = bookingRepo.findLastPaymentCode();
        int nextNumber = 1;
        if (lastCode != null && lastCode.matches("B\\d{4}")) {
            nextNumber = Integer.parseInt(lastCode.substring(1)) + 1;
        }
        return String.format("B%04d", nextNumber);
    }
}

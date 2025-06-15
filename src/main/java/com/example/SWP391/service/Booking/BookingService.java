package com.example.SWP391.service.Booking;

import com.example.SWP391.DTO.EntityDTO.BookingDTO;
import com.example.SWP391.entity.BioKit;
import com.example.SWP391.entity.Booking;
import com.example.SWP391.entity.KitTransaction;
import com.example.SWP391.entity.Service;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.repository.BioRepository.BioKitRepository;
import com.example.SWP391.repository.BioRepository.KitTransactionRepository;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.repository.BookingRepository.ServiceRepository;
import com.example.SWP391.repository.UserRepository.CustomerRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageFilter;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Base64;


@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BookingService {
    @Autowired private final BioKitRepository bioKitRepo;
    @Autowired private final BookingRepository bookingRepo;
    @Autowired private final KitTransactionRepository kitTransactionRepo;
    @Autowired private final CustomerRepository customerRepository;
    @Autowired private final ServiceRepository serviceRepository;
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
   public Booking createBookingFromDTO(BookingDTO dto,String customerID) throws Exception {
        Customer customer = customerRepository.findById(customerID)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Service service = serviceRepository.findById(dto.getServiceID())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        BioKit kit = bioKitRepo.findById(dto.getKitID())
                .orElseThrow(() -> new IllegalArgumentException("Kit not found"));

        if (!kit.isAvailable() || kit.getQuantity() <= 0) {
            throw new IllegalStateException("Kit is not available or out of stock");
        }

        float cost = service.getCost();
        float additionalCost = dto.getAdditionalCost();
        float totalCost = cost + additionalCost;

        Booking booking = new Booking();
        booking.setBookingType(dto.getBookingType());
        booking.setPaymentMethod(dto.getPaymentMethod());
        booking.setSampleMethod(dto.getSampleMethod());
        booking.setRequest_date(dto.getRequest_date());
        booking.setNote(dto.getNote());
        booking.setMediationMethod(dto.getMediationMethod());

        booking.setStatus("Pending Payment"); // Consider using Enum
        booking.setCost(cost);
        booking.setAdditionalCost(additionalCost);
        booking.setTotalCost(totalCost);

        booking.setCustomer(customer);
        booking.setService(service);
        booking.setBioKit(kit);



        Booking saved = bookingRepo.save(booking);

        KitTransaction tx = new KitTransaction();
        tx.setBooking(saved);
        tx.setBioKit(kit);
        tx.setReceived(false);
        kitTransactionRepo.save(tx);

        kit.setQuantity(kit.getQuantity() - 1);
        kit.setIsSelled(kit.getIsSelled() + 1);
        kit.setAvailable(kit.getQuantity() > 0);
        bioKitRepo.save(kit);

        return saved;
   }

   @Transactional
    public Booking createBookingFromDTO2(BookingDTO dto,String serviceID,String customerID) throws Exception{
        Customer customer=customerRepository.findById(customerID).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Service service=serviceRepository.findById(serviceID).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        BioKit kit=bioKitRepo.findById(dto.getKitID()).orElseThrow(() -> new IllegalArgumentException("Kit not found"));

       if (!kit.isAvailable() || kit.getQuantity() <= 0) {
           throw new IllegalStateException("Kit is not available or out of stock");
       }

       float cost=service.getCost();
       float additionalCost= dto.getAdditionalCost();
       float totalCost=cost+additionalCost;

       Booking booking=new Booking();
       booking.setBookingType(dto.getBookingType());
       booking.setPaymentMethod(dto.getPaymentMethod());
       booking.setSampleMethod(dto.getSampleMethod());
       booking.setRequest_date(dto.getRequest_date()!=null?dto.getRequest_date(): LocalDate.now());
       booking.setNote(dto.getNote());
       booking.setMediationMethod(dto.getMediationMethod());

       booking.setStatus("Pending payment");
       booking.setCost(cost);
       booking.setAdditionalCost(additionalCost);
       booking.setTotalCost(totalCost);

       booking.setCustomer(customer);
       booking.setService(service);
       booking.setBioKit(kit);

       Booking saved=bookingRepo.save(booking);
       KitTransaction tx=new KitTransaction();
       tx.setBooking(saved);
       tx.setBioKit(kit);
       tx.setReceived(false);
       kitTransactionRepo.save(tx);

       kit.setQuantity(kit.getQuantity()-1);
       kit.setIsSelled(kit.getIsSelled()+1);
       kit.setAvailable(kit.getQuantity()>0);
       bioKitRepo.save(kit);

       return saved;
   }
}

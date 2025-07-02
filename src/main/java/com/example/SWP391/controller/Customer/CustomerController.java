package com.example.SWP391.controller.Customer;


import com.example.SWP391.DTO.AuthRequest.ChangePasswordDTO;
import com.example.SWP391.DTO.AuthUpdate.UpdateRequestDTO;
import com.example.SWP391.DTO.EntityDTO.CustomerBookingDTO;
import com.example.SWP391.DTO.EntityDTO.CustomerDTO;
import com.example.SWP391.DTO.EntityDTO.FeedbackDTO;
import com.example.SWP391.DTO.EntityDTO.SlotDTO;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.Slot;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.repository.BookingRepository.SlotRepository;
import com.example.SWP391.repository.UserRepository.AccountRepository;
import com.example.SWP391.repository.UserRepository.CustomerRepository;
import com.example.SWP391.service.Customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import com.example.SWP391.entity.User.Customer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    SlotRepository slotRepository;
    @PatchMapping("/{id}")
    public ResponseEntity<?>  updateCustomer(@PathVariable("id") String customerId, @RequestBody UpdateRequestDTO request){
        try {
            Customer updated= customerService.updateCustomer(customerId,request);
            Account account= updated.getAccount();
            account.setFullname(request.getFullName());
            accountRepository.save(account);
            return ResponseEntity.ok(updated);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/my-info/{id}")
    public ResponseEntity<List<CustomerDTO>> getMyInfo(@PathVariable(name = "id") String customerID){
        Optional<Customer> info=customerRepository.findById(customerID);
        List<CustomerDTO> customerDTOS = info.stream().map(this::converToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(customerDTOS);
    }

    @GetMapping("/my-booking/{customerID}")
    public ResponseEntity<List<CustomerBookingDTO>> getMyBooking(@PathVariable String customerID) {
        List<Booking> bookings = bookingRepository.findByCustomer_CustomerID(customerID);
        List<CustomerBookingDTO> dtos = bookings.stream()
                .map(this::convertIntoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @PatchMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable(name = "id") String customerID, @RequestBody ChangePasswordDTO dto){
        Customer customer=customerRepository.findById(customerID).orElse(null);
        if(customer==null){
            return ResponseEntity.badRequest().body("Customer not found");
        }
        Account account=customer.getAccount();
        if(account==null){
            return ResponseEntity.badRequest().body("Account not found");
        }

        if(!dto.getCurrentPassword().equals(account.getPassword())){
            return ResponseEntity.badRequest().body("Current password is incorrect");
        }
        if(!dto.getNewPassword().equals(dto.getConfirmPassword())){
            return ResponseEntity.badRequest().body("New password and confirm password do not match");
        }

        account.setPassword(dto.getNewPassword());
        accountRepository.save(account);
        return ResponseEntity.ok("Password changed successfully");

    }
    @PostMapping("/feedback/{bookingID}/{customerID}")
    public ResponseEntity<?> createFeedback(
            @PathVariable int bookingID,
            @PathVariable String customerID,
            @RequestBody FeedbackDTO dto) {
        try {
            customerService.createFeedback(bookingID, customerID, dto);
            return ResponseEntity.ok("✅ Feedback submitted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Not Found: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("❌ Conflict: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error: " + e.getMessage());
        }
    }
    @GetMapping("/all-slot-time")
    public ResponseEntity<List<SlotDTO>> getAllSlot(){
        List<Slot> slots=slotRepository.findAll();
        List<SlotDTO> slotDTOS=slots.stream().map(this::convertToSlotDTO).collect(Collectors.toList());
        return ResponseEntity.ok(slotDTOS);
    }

    public SlotDTO convertToSlotDTO(Slot slot){
        SlotDTO slotDTO=new SlotDTO();
        slotDTO.setId(slot.getId());
        slotDTO.setDate(slot.getDate());
        slotDTO.setTimeRange(slot.getTimeRange());
        slotDTO.setCurrentBooking(slot.getCurrentBooking());
        return slotDTO;
    }

    public CustomerDTO converToDTO(Customer customer){
        CustomerDTO customerDTO=new CustomerDTO();
        customerDTO.setFullName(customer.getFullName());
        customerDTO.setDob(customer.getDob());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setPhone(customer.getPhone());
        customerDTO.setAddress(customer.getAddress());
        customerDTO.setGender(customer.getGender());
        if(customer.getAccount()!=null){
            customerDTO.setCreateAt(customer.getAccount().getCreateAt());
        }
        return customerDTO;
    }

    public CustomerBookingDTO convertIntoDTO(Booking booking) {
        CustomerBookingDTO dto = new CustomerBookingDTO();

        dto.setCollectionMethod(booking.getCollectionMethod());
        dto.setAppointmentTime(booking.getAppointmentTime());
        dto.setNote(booking.getNote());
        dto.setStatus(booking.getStatus());
        dto.setPaymentMethod(booking.getPaymentMethod());
        dto.setMediationMethod(booking.getMediationMethod());
        dto.setTimeRange(booking.getTimeRange());
        dto.setAddress(booking.getAddress());

        // Check null để tránh lỗi
        if (booking.getBioKit() != null) {
            dto.setKitName(booking.getBioKit().getName());
        } else {
            dto.setKitName("N/A");
        }

        if (booking.getService() != null) {
            dto.setServiceName(booking.getService().getName());
        } else {
            dto.setServiceName("N/A");
        }

        return dto;
    }


}

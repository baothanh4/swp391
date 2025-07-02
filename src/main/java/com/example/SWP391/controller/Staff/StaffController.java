package com.example.SWP391.controller.Staff;

import com.example.SWP391.DTO.AuthRequest.ChangePasswordDTO;
import com.example.SWP391.DTO.AuthUpdate.UpdateRequestDTO;
import com.example.SWP391.DTO.EntityDTO.*;
import com.example.SWP391.controller.Booking.BookingController;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.KitTransaction;
import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.Report;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.entity.User.Staff;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.repository.BookingRepository.ReportRepository;
import com.example.SWP391.repository.UserRepository.AccountRepository;
import com.example.SWP391.repository.UserRepository.StaffRepository;
import com.example.SWP391.service.Kit.KitTransactionService;
import com.example.SWP391.service.Staff.UpdateBooking;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    @Autowired
    private UpdateBooking updateBooking;
    @Autowired private KitTransactionService kitTransactionService;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private BookingController bookingController;
    @Autowired private StaffRepository staffRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private ReportRepository reportRepository;
    @PatchMapping("/updateBooking/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable("id") int bookingID, @RequestBody BookingUpdateDTO dto){
        try {
            Booking booking= updateBooking.updateBookingFromDTO(bookingID,dto);
            BookingDTO bookingDTO=bookingController.convertDTO(booking);
            return ResponseEntity.ok(bookingDTO);
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
    @GetMapping("/my-assignment/{StaffID}")
    public ResponseEntity<?> allAsignment(@PathVariable("StaffID") String StaffID){
        List<Booking> bookings=bookingRepository.getAllBookingsByStaffId(StaffID);
        List<OrderDTO> bookingDTOS=bookings.stream().map(this::convertDTO).collect(Collectors.toList());
        return ResponseEntity.ok(bookingDTOS);
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my-info/{id}")
    public ResponseEntity<List<StaffDTO>> getMyInfo(@PathVariable(name = "id")String staffID){
        Optional<Staff> staff =staffRepository.findById(staffID);
        List<StaffDTO> staffDTOS=staff.stream().map(this::convertIntoStaffDTO).collect(Collectors.toList());
        return ResponseEntity.ok(staffDTOS);
    }
    @PatchMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable(name = "id") String staffID, @RequestBody ChangePasswordDTO dto){
        Staff staff=staffRepository.findById(staffID).orElse(null);
        if(staff==null){
            return ResponseEntity.badRequest().body("Staff not found");
        }
        Account account=staff.getAccount();
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
    @PatchMapping("/my-account/{id}")
    public ResponseEntity<?> updateInfo(@PathVariable(name = "id")String staffID, @RequestBody UpdateRequestDTO dto){
        try{
            Staff staff= updateBooking.updateInfo(staffID,dto);
            Account account=staff.getAccount();
            account.setFullname(staff.getFullName());
            accountRepository.save(account);
            return ResponseEntity.ok(staff);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Updated failed");
        }
    }
    @GetMapping("/my-report/{staffID}")
    public ResponseEntity<?> myReport(@PathVariable(name = "staffID") String staffID){
            List<Report> reports=reportRepository.findByStaff_StaffID(staffID);
            List<ReportDTO> reportDTOS=reports.stream().map(this::convertToReportDTO).collect(Collectors.toList());
            return ResponseEntity.ok(reportDTOS);
    }

    @PatchMapping("/my-report/{reportID}")
    public ResponseEntity<?> updateReport(@PathVariable(name = "reportID") int reportID,@RequestBody ReportDTO dto){
        try {
            Report report1=reportRepository.findById(reportID).orElseThrow(()->new IllegalArgumentException("Report not found"));
            report1.setStatus(dto.getStatus());
            report1.setNote(dto.getNote());
            reportRepository.save(report1);
            return ResponseEntity.ok("Update completely");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error:"+e.getMessage());
        }
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
    public ReportDTO convertToReportDTO(Report report){
        ReportDTO reportDTO=new ReportDTO();
        reportDTO.setReportID(report.getReportID());
        reportDTO.setAppointmentTime(report.getAppointmentTime());
        reportDTO.setCustomerName(report.getCustomerName());
        reportDTO.setNote(report.getNote());
        reportDTO.setStatus(report.getStatus());
        reportDTO.setAssignedID(report.getBookingAssigned().getAssignedID());
        reportDTO.setManagerID(report.getManager().getManagerID());
        reportDTO.setStaffID(report.getStaff().getStaffID());
        reportDTO.setBookingID(report.getBookingID());
        return reportDTO;
    }
    public StaffDTO convertIntoStaffDTO(Staff staff){
        StaffDTO staffDTO=new StaffDTO();
        staffDTO.setFullname(staff.getFullName());
        staffDTO.setDOB(staff.getDOB());
        staffDTO.setEmail(staff.getEmail());
        staffDTO.setPhone(staff.getPhone());
        staffDTO.setAddress(staff.getAddress());
        staffDTO.setGender(staff.getGender());
        return staffDTO;
    }


}

package com.example.SWP391.controller.Manager;

import com.example.SWP391.DTO.AuthRequest.AssignRequest;
import com.example.SWP391.DTO.AuthRequest.ChangePasswordDTO;
import com.example.SWP391.DTO.AuthUpdate.UpdateRequestDTO;
import com.example.SWP391.DTO.EntityDTO.*;
import com.example.SWP391.entity.BioKit;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Booking.BookingAssigned;
import com.example.SWP391.entity.KitTransaction;
import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.Report;
import com.example.SWP391.entity.Slot;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.entity.User.Manager;
import com.example.SWP391.entity.User.Staff;
import com.example.SWP391.repository.BioRepository.KitTransactionRepository;
import com.example.SWP391.repository.BookingRepository.BookingAssignedRepository;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.repository.BookingRepository.ReportRepository;
import com.example.SWP391.repository.BookingRepository.SlotRepository;
import com.example.SWP391.repository.UserRepository.AccountRepository;
import com.example.SWP391.repository.UserRepository.ManagerRepository;
import com.example.SWP391.repository.UserRepository.StaffRepository;
import com.example.SWP391.service.Kit.KitService;
import com.example.SWP391.service.Manager.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    @Autowired BookingAssignedRepository bookingAssignedRepository;
    @Autowired KitService kitService;
    @Autowired StaffRepository staffRepository;
    @Autowired KitTransactionRepository kitTransactionRepository;
    @Autowired BookingRepository bookingRepository;
    @Autowired SlotRepository slotRepository;
    @Autowired ManagerRepository managerRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired ManagerService managerService;
    @Autowired ReportRepository reportRepository;
    @PatchMapping("/assign-staff/{assignedId}")
    public ResponseEntity<?> assignStaff(@PathVariable(name = "assignedId") Long assignedId, @RequestBody AssignRequest assignRequest) {
        Staff staff1 = staffRepository.findById(assignRequest.getStaffID())
                .orElseThrow(() -> new RuntimeException("StaffID not found"));

        Manager manager=managerRepository.findById(assignRequest.getManagerID()).orElseThrow(()->new RuntimeException("Manager not found"));
        BookingAssigned assigned = bookingAssignedRepository.findById(assignedId)
                .orElseThrow(() -> new IllegalArgumentException("Booking assigned not found"));

        assigned.setAssignedStaff(staff1.getFullName());
        assigned.setStaff(staff1);
        assigned.setManager(manager);
        assigned.getBooking().setStaff(staff1);
        staff1.setAvaliable(false); //

        bookingRepository.save(assigned.getBooking());
        staffRepository.save(staff1);
        bookingAssignedRepository.save(assigned);

        Report report=new Report();
        report.setAppointmentTime(assigned.getAppointmentTime());
        report.setCustomerName(assigned.getCustomerName());
        report.setBookingID(assigned.getBooking().getBookingId());
        report.setStatus("nothing");
        report.setNote("");
        report.setStaff(staff1);
        report.setManager(manager);
        report.setBookingAssigned(assigned);

        reportRepository.save(report);

        return ResponseEntity.ok("Assigned successfully");
    }
    @PatchMapping("/kit/{KitID}")
    public ResponseEntity<?> updateKitQuantity(@PathVariable(name="KitID") String kitID, @RequestBody BioKitDTO bioKit){
        kitService.updateQuantity(kitID,bioKit.getQuantity());
        return ResponseEntity.ok("Add Kit successfully");
    }
    @GetMapping("/booking-assigned")
    public ResponseEntity<List<BookingAssignedDTO>> getAllBookingAssigned(){
        List<BookingAssigned> bookingAssigned=bookingAssignedRepository.findAll();
        List<BookingAssignedDTO> bookingAssignedDTOS=bookingAssigned.stream().map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(bookingAssignedDTOS);
    }
    @GetMapping("/kit-transaction")
    public ResponseEntity<List<KitTransactionDTO>> getAllKitTransaction(){
        List<KitTransaction> kitTransactionList=kitTransactionRepository.findAll();
        List<KitTransactionDTO> kitTransactionDTOS=kitTransactionList.stream().map(this::transactionDTO).collect(Collectors.toList());
        return ResponseEntity.ok(kitTransactionDTOS);
    }

    @GetMapping("/my-info/{id}")
    public ResponseEntity<ManagerDTO> getMyInfo(@PathVariable(name = "id") String managerID){
        Manager manager = managerRepository.findById(managerID)
                .orElseThrow(() -> new RuntimeException("managerID not found"));
        ManagerDTO dto = convertToManagerDTO(manager);
        return ResponseEntity.ok(dto);
    }
    @PatchMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable(name = "id") String managerID, @RequestBody ChangePasswordDTO dto){
        Manager manager=managerRepository.findById(managerID).orElse(null);
        if(manager==null){
            return ResponseEntity.badRequest().body("Manager not found");
        }
        Account account=manager.getAccount();
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
    public ResponseEntity<?> updateInfo(@PathVariable(name = "id")String managerID, @RequestBody UpdateRequestDTO dto){
        try {
            Manager manager=managerService.updateInfo(managerID,dto);
            Account account= manager.getAccount();
            account.setFullname(dto.getFullName());
            accountRepository.save(account);
            return ResponseEntity.ok(manager);
        }catch (Exception e){
            throw new RuntimeException("Update failed");
        }
    }
    @GetMapping("/report/{managerID}")
    public ResponseEntity<?> myReport(@PathVariable(name = "managerID") String managerID){
        List<Report> reports=reportRepository.findByManager_ManagerID(managerID);
        List<ReportDTO> reportDTOS=reports.stream().map(this::convertToReportDTO).collect(Collectors.toList());
        return ResponseEntity.ok(reportDTOS);
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



    public ManagerDTO convertToManagerDTO(Manager manager){
        ManagerDTO managerDTO=new ManagerDTO();
        managerDTO.setDOB(manager.getDOB());
        managerDTO.setEmail(manager.getEmail());
        managerDTO.setPhone(manager.getPhone());
        managerDTO.setFullname(manager.getFullName());
        managerDTO.setAddress(manager.getAddress());
        managerDTO.setGender(manager.getGender());
        return managerDTO;
    }

    public BookingAssignedDTO mapToDTO(BookingAssigned assigned) {
        Booking booking = assigned.getBooking();
        Customer customer = booking.getCustomer();
        String staff = assigned.getAssignedStaff();

        BookingAssignedDTO dto = new BookingAssignedDTO();
        dto.setAssignedID(assigned.getAssignedID());
        dto.setBookingID(booking.getBookingId());
        dto.setCustomerName(customer.getFullName());
        dto.setStaffName(staff);
        dto.setLastUpdate(assigned.getLastUpdate());
        dto.setServiceType(booking.getService().getType());
        dto.setStatus(booking.getStatus());

        return dto;
    }
    public KitTransactionDTO transactionDTO(KitTransaction kitTransaction){
        KitTransactionDTO kitTransactionDTO=new KitTransactionDTO();
        Booking booking=kitTransaction.getBooking();
        BioKit kit=kitTransaction.getBioKit();

        kitTransactionDTO.setTransactionID(kitTransaction.getTransactionID());
        kitTransactionDTO.setKitID(kit.getKitID());
        kitTransactionDTO.setBookingID(booking.getBookingId());
        kitTransactionDTO.setReceived(kitTransaction.isReceived());

        return kitTransactionDTO;
    }


}

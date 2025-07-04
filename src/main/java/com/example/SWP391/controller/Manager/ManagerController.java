package com.example.SWP391.controller.Manager;

import com.example.SWP391.DTO.AuthRequest.AssignRequest;
import com.example.SWP391.DTO.AuthRequest.ChangePasswordDTO;
import com.example.SWP391.DTO.AuthUpdate.UpdateRequestDTO;
import com.example.SWP391.DTO.EntityDTO.*;
import com.example.SWP391.entity.*;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Booking.BookingAssigned;
import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.entity.User.Manager;
import com.example.SWP391.entity.User.Staff;
import com.example.SWP391.repository.BioRepository.KitTransactionRepository;
import com.example.SWP391.repository.BookingRepository.*;
import com.example.SWP391.repository.UserRepository.AccountRepository;
import com.example.SWP391.repository.UserRepository.ManagerRepository;
import com.example.SWP391.repository.UserRepository.StaffRepository;
import com.example.SWP391.service.Kit.KitService;
import com.example.SWP391.service.Manager.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    @Autowired ResultRepository resultRepository;
    @Autowired FeedbackRepository feedbackRepository;

    @PatchMapping("/assign-staff/{assignedId}")
    public ResponseEntity<?> assignStaff(@PathVariable(name = "assignedId") Long assignedId, @RequestBody AssignRequest assignRequest) {
        // ✅ Tìm Staff và Manager
        Staff staff1 = staffRepository.findById(assignRequest.getStaffID())
                .orElseThrow(() -> new RuntimeException("StaffID not found"));
        Manager manager = managerRepository.findById(assignRequest.getManagerID())
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        // ✅ Tìm thông tin BookingAssigned
        BookingAssigned assigned = bookingAssignedRepository.findById(assignedId)
                .orElseThrow(() -> new IllegalArgumentException("Booking assigned not found"));

        // ✅ Gán Staff vào BookingAssigned và Booking
        assigned.setAssignedStaff(staff1.getFullName());
        assigned.setStaff(staff1);
        assigned.setManager(manager);

        Booking booking = assigned.getBooking();
        booking.setStaff(staff1);

        // ✅ Cập nhật trạng thái nhân viên
        staff1.setAvaliable(false); // Đánh dấu đã được phân công

        // ✅ Lưu các thay đổi
        bookingRepository.save(booking);
        staffRepository.save(staff1);
        bookingAssignedRepository.save(assigned);

        // ✅ Tạo mới Report
        Report report = new Report();
        report.setAppointmentTime(assigned.getAppointmentTime());
        report.setCustomerName(assigned.getCustomerName());
        report.setBookingID(booking.getBookingId());
        report.setStatus("Pending");
        report.setNote("");
        report.setStaff(staff1);
        report.setManager(manager);
        report.setBookingAssigned(assigned);
        reportRepository.save(report);

        // ✅ Gán Staff cho Result nếu có
        Optional<Result> resultOpt = resultRepository.findByBooking(booking);
        if (resultOpt.isPresent()) {
            Result result = resultOpt.get();
            result.setStaffID(staff1.getStaffID());
            result.setUpdateAt(LocalDateTime.now());
            resultRepository.save(result);
        }

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
    public ResponseEntity<?> updateInfo(@PathVariable("id") String managerID, @RequestBody UpdateRequestDTO dto) {
        try {
            // Cập nhật thông tin Manager
            Manager manager = managerService.updateInfo(managerID, dto);

            // Cập nhật fullname trong bảng Account
            Account account = manager.getAccount();
            if (account != null && dto.getFullName() != null) {
                account.setFullname(dto.getFullName());
                accountRepository.save(account);
            }

            return ResponseEntity.ok(manager);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }
    @GetMapping("/report/{managerID}")
    public ResponseEntity<?> myReport(@PathVariable(name = "managerID") String managerID){
        List<Report> reports=reportRepository.findByManager_ManagerID(managerID);
        List<ReportDTO> reportDTOS=reports.stream().map(this::convertToReportDTO).collect(Collectors.toList());
        return ResponseEntity.ok(reportDTOS);
    }
    @GetMapping("/all-feedback")
    public ResponseEntity<?> getAllFeedback(){
        List<Feedback> feedbacks=feedbackRepository.findAll();
        List<FeedbackDTO> feedbackDTOS=feedbacks.stream().map(this::convertToFeedbackDTO).collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDTOS);
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
    public FeedbackDTO convertToFeedbackDTO(Feedback feedback){
        FeedbackDTO dto=new FeedbackDTO();
        dto.setTitle(feedback.getTitle());
        dto.setContent(feedback.getContent());
        dto.setRating(feedback.getRating());
        dto.setCreateAt(feedback.getCreateAt());
        dto.setBookingID(feedback.getBooking().getBookingId());
        dto.setCustomerID(feedback.getCustomer().getCustomerID());
        return dto;
    }



    public ManagerDTO convertToManagerDTO(Manager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null");
        }

        ManagerDTO dto = new ManagerDTO();
        dto.setDOB(manager.getDOB());
        dto.setEmail(manager.getEmail());
        dto.setPhone(manager.getPhone());
        dto.setFullname(manager.getFullName());
        dto.setAddress(manager.getAddress());
        dto.setGender(manager.getGender());

        return dto;
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

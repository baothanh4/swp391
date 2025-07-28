package com.example.SWP391.controller.Admin;


import com.example.SWP391.DTO.AuthRequest.ChangePasswordDTO;
import com.example.SWP391.DTO.AuthRequest.RegisterRequestDTO;
import com.example.SWP391.DTO.AuthUpdate.AccountUpdateDTO;
import com.example.SWP391.DTO.AuthUpdate.UpdateRequestDTO;
import com.example.SWP391.DTO.EntityDTO.AdminDTO;
import com.example.SWP391.entity.*;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.User.Admin;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.entity.User.Manager;
import com.example.SWP391.entity.User.Staff;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.repository.OtpRepository.OtpVerificationRepository;
import com.example.SWP391.repository.SystemLogRepository;
import com.example.SWP391.repository.UserRepository.*;
import com.example.SWP391.service.Admin.AdminService;
import com.example.SWP391.service.Service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AdminController {
    private final AccountRepository accountRepo;
    private final AdminRepository adminRepo;
    private final ManagerRepository managerRepo;
    private final StaffRepository staffRepo;
    private final CustomerRepository custRepo;
    private final OtpVerificationRepository otpRepo;
    private final JavaMailSender mailSender;
    private final AdminService adminService;
    @Autowired private final BookingRepository bookingRepo;
    @Autowired
    private ServiceService serviceService;
    @Autowired private final SystemLogRepository systemLogRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            // 1. Kiểm tra username đã tồn tại
            if (accountRepo.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại.");
            }


            Account account = new Account();
            account.setUsername(request.getUsername());
            account.setPassword(request.getPassword());
            account.setEmail(request.getEmail());
            account.setPhone(request.getPhone());
            account.setRole(request.getRole().toUpperCase());
            account.setCreateAt(LocalDate.now());
            account.setEnabled(true);
            account.setFullname(request.getFullName());

            account = accountRepo.save(account);


            String role = request.getRole().toUpperCase();
            String fullName = request.getFullName();

            switch (role) {
                case "ADMIN" -> {
                    com.example.SWP391.entity.User.Admin admin = new com.example.SWP391.entity.User.Admin();
                    admin.setAdminID(generateCustomId("ADM", adminRepo.count()));
                    admin.setFullName(fullName);
                    admin.setAccount(account);
                    adminRepo.save(admin);
                }
                case "MANAGER" -> {
                    Manager manager = new Manager();
                    manager.setManagerID(generateCustomId("MAN", managerRepo.count()));
                    manager.setFullName(fullName);
                    manager.setAccount(account);
                    managerRepo.save(manager);
                }
                case "STAFF" -> {
                    Staff staff = new Staff();
                    staff.setStaffID(generateCustomId("STF", staffRepo.count()));
                    staff.setFullName(fullName);
                    staff.setAccount(account);
                    staffRepo.save(staff);
                }
                default -> {
                    return ResponseEntity.badRequest().body("Vai trò không hợp lệ.");
                }
            }


            return ResponseEntity.ok("Tạo tài khoản thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi đăng ký.");
        }
    }

    private String generateCustomId(String prefix, long count) {
        return String.format("%s%03d", prefix, count + 1);
    }


    @PatchMapping("/account/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable("id") int accountId, @RequestBody AccountUpdateDTO update) {
        try {
            adminService.updateAccount(accountId, update);
            return ResponseEntity.ok("Account updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/account/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable("id") int accountId) {
        try {
            Optional<Account> optionalAccount = accountRepo.findById(accountId);
            if (optionalAccount.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found with id: " + accountId);
            }

            Account account = optionalAccount.get();


            if (account.getCustomer() != null) {
                List<Booking> bookings = bookingRepo.findByCustomer(account.getCustomer());

                boolean hasUncompletedBooking = bookings.stream()
                        .anyMatch(booking -> !"COMPLETED".equalsIgnoreCase(booking.getStatus()));

                if (hasUncompletedBooking) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Cannot delete customer account with uncompleted bookings.");
                }

                custRepo.delete(account.getCustomer());
            }


            if (account.getAdmin() != null) {
                adminRepo.delete(account.getAdmin());
            }

            if (account.getManager() != null) {
                managerRepo.delete(account.getManager());
            }

            if (account.getStaff() != null) {
                staffRepo.delete(account.getStaff());
            }


            accountRepo.delete(account);

            return ResponseEntity.ok("Account deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Delete failed");
        }
    }
    @GetMapping("/my-info/{id}")
    public ResponseEntity<?> getMyInfo(@PathVariable(name = "id") String adminID){
        Optional<Admin> admin=adminRepo.findById(adminID);
        List<AdminDTO> adminDTOS=admin.stream().map(this::converIntoAdminDTO).collect(Collectors.toList());
        return ResponseEntity.ok(adminDTOS);
    }
    @PatchMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable(name = "id") String adminID, @RequestBody ChangePasswordDTO dto){
        Admin admin=adminRepo.findById(adminID).orElse(null);
        if(admin==null){
            return ResponseEntity.badRequest().body("Admin not found");
        }
        Account account=admin.getAccount();
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
        accountRepo.save(account);
        return ResponseEntity.ok("Password changed successfully");

    }
    @PatchMapping("/my-account/{id}")
    public ResponseEntity<?> updateInfo(@PathVariable(name = "id") String adminID, @RequestBody UpdateRequestDTO dto){
        try {
            Admin admin=adminService.updateInfo(adminID,dto);
            Account account=admin.getAccount();
            account.setFullname(dto.getFullName());
            accountRepo.save(account);
            return ResponseEntity.ok(admin);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Update failed");
        }
    }



    @GetMapping("/account")
    public ResponseEntity<List<Account>> getAllListAccount() {
        List<Account> accounts = accountRepo.findAll();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/dashboard/customers")
    public ResponseEntity<?> getDashboardsStatus() {
        long totalCustomer = custRepo.count();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomer", totalCustomer);

        return ResponseEntity.ok(stats);
    }


    @GetMapping("/kitInventory/all")
    public ResponseEntity<List<BioKit>> getAllKits() {
        return ResponseEntity.ok(adminService.getAllKits());
    }


    @GetMapping("/kitInventory/available")
    public ResponseEntity<List<BioKit>> getAvailableKits() {
        return ResponseEntity.ok(adminService.getAvailableKits());
    }

    @PatchMapping("/{serviceId}/cost")
    public ResponseEntity<?> updateServiceCost(@PathVariable String serviceId,
                                               @RequestParam float cost) {
        try {
            Service updated = serviceService.updateCost(serviceId, cost);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/system-log")
    public ResponseEntity<List<SystemLog>> getAllSystemLog(){
        List<SystemLog> systemLogs=systemLogRepository.findAll();
        return ResponseEntity.ok(systemLogs);
    }

    public AdminDTO converIntoAdminDTO(Admin admin){
        AdminDTO adminDTO=new AdminDTO();
        adminDTO.setFullname(admin.getFullName());
        adminDTO.setDOB(admin.getDOB());
        adminDTO.setEmail(admin.getEmail());
        adminDTO.setPhone(admin.getPhone());
        adminDTO.setAddress(admin.getAddress());
        adminDTO.setGender(admin.getGender());
        return adminDTO;
    }
}

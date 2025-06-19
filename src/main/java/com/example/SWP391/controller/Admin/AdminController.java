package com.example.SWP391.controller.Admin;


import com.example.SWP391.DTO.AuthRequest.RegisterRequest;
import com.example.SWP391.DTO.AuthUpdate.AccountUpdate;
import com.example.SWP391.entity.*;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Otp.Account;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

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
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // 1. Kiểm tra username đã tồn tại
            if (accountRepo.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại.");
            }

            // 2. Tạo tài khoản mới
            Account account = new Account();
            account.setUsername(request.getUsername());
            account.setPassword(request.getPassword()); // 👈 Nhớ mã hóa
            account.setEmail(request.getEmail());
            account.setPhone(request.getPhone());
            account.setRole(request.getRole().toUpperCase());
            account.setCreateAt(LocalDate.now());
            account.setEnabled(true); // Chưa kích hoạt

            account = accountRepo.save(account); // Lưu & lấy lại ID

            // 3. Gán role
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
    public ResponseEntity<?> updateAccount(@PathVariable("id") int accountId, @RequestBody AccountUpdate update) {
        try {
            adminService.updateAccount(accountId, update);
            return ResponseEntity.ok("Account updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("account/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable("id") int accountId) {
        try {
            Optional<Account> optionalAccount = accountRepo.findById(accountId);
            if (optionalAccount.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found with id: " + accountId);
            }

            Account account = optionalAccount.get();

            // Nếu là Customer, kiểm tra các booking
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

            // Xử lý các role khác
            if (account.getAdmin() != null) {
                adminRepo.delete(account.getAdmin());
            }

            if (account.getManager() != null) {
                managerRepo.delete(account.getManager());
            }

            if (account.getStaff() != null) {
                staffRepo.delete(account.getStaff());
            }

            // Xóa tài khoản
            accountRepo.delete(account);

            return ResponseEntity.ok("Account deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Delete failed");
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
}

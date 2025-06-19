package com.example.SWP391.controller.Auth;

import com.example.SWP391.DTO.AuthRegister.AuthRegister;
import com.example.SWP391.DTO.AuthRequest.AuthRequest;
import com.example.SWP391.DTO.AuthRequest.OtpRequest;
import com.example.SWP391.DTO.AuthRequest.ResetPasswordRequest;
import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.entity.Otp.OtpVerification;
import com.example.SWP391.repository.UserRepository.AccountRepository;
import com.example.SWP391.repository.UserRepository.CustomerRepository;
import com.example.SWP391.repository.OtpRepository.OtpVerificationRepository;
import com.example.SWP391.security.JwtTokenProvider;
import com.example.SWP391.service.ResetPassword.PasswordResetService;
import com.example.SWP391.service.Customer.RegisterService;
import com.example.SWP391.service.System.SystemLogService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private OtpVerificationRepository otpRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private PasswordResetService resetService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    SystemLogService logService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            // Tạo JWT token
            String token = jwtTokenProvider.generateToken(request.getUsername());

            // Lấy role
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String role = roles.stream().findFirst().orElse("UNKNOWN");
            String redirect = switch (role) {
                case "ROLE_ADMIN" -> "/admin/register-page";
                case "ROLE_STAFF" -> "/staff/dashboard";
                case "ROLE_MANAGER" -> "/manager/dashboard";
                case "ROLE_CUSTOMER" -> "/customer/index.jsx";
                default -> null;
            };

            if (redirect == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền truy cập hợp lệ");
            }

            // ✅ Ghi log đăng nhập
            String ip = httpRequest.getRemoteAddr();
            logService.log(request.getUsername(), "Login", ip);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", role.replace("ROLE_", ""),
                    "redirect", redirect,
                    "message", "Đăng nhập thành công với vai trò " + role.replace("ROLE_", "")
            ));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRegister dto, HttpServletRequest request) {
        String result = registerService.register(dto);

        if (result.startsWith("Registration successful. An OTP has been sent to your email. Please verify it to activate your account.")) {
            String ip = request.getRemoteAddr(); // Lấy IP
            logService.log(dto.getUsername(), "Register", ip);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest request) {
        try {
            Optional<OtpVerification> otpOpt = otpRepo.findTopByEmailOrderByIdDesc(request.getEmail());

            if (otpOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy OTP cho email.");
            }

            OtpVerification otp = otpOpt.get();

            if (!otp.getOtp().equals(request.getOtp())) {
                return ResponseEntity.badRequest().body("Mã OTP không hợp lệ.");
            }

            if (otp.getExpirationTime().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("Mã OTP đã hết hạn.");
            }

// Kích hoạt tài khoản
            Account account = accountRepo.findByEmail(request.getEmail());
            account.setEnabled(true);
            accountRepo.save(account);

// Xóa OTP
            otpRepo.delete(otp);

            return ResponseEntity.ok("Tài khoản đã được kích hoạt thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server");
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> body) {
        String idToken = body.get("credential");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList("26142191146-7u8f63rgtupdv8v6kv8ug307j55hjfob.apps.googleusercontent.com"))
                .build();

        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");

                Account account = accountRepo.findByEmail(email);
                if (account == null) {
                    // ✅ Tạo mới Account
                    account = new Account();
                    account.setUsername(email); // hoặc name slug
                    account.setPassword(""); // Vì Google OAuth
                    account.setEmail(email);
                    account.setPhone(""); // Google không cung cấp
                    account.setRole("Customer");
                    account.setCreateAt(LocalDate.now());
                    account.setEnabled(true);
                    account = accountRepo.save(account);

                    // ✅ Tạo mới Customer
                    Customer customer = new Customer();
                    customer.setCustomerID(generateCustomId("CUST", customerRepository.count()));
                    customer.setFullName(name);
                    customer.setEmail(email);
                    customer.setPhone("");
                    customer.setAddress("");
                    customer.setGender(null);
                    customer.setDob(null);
                    customer.setAccount(account);
                    customerRepository.save(customer);
                }

                // ✅ Trả về thông tin (hoặc tạo JWT token tại đây)
                String role = account.getRole();
                return ResponseEntity.ok(Map.of(
                        "email", email,
                        "name", name,
                        "role", role
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token verification failed");
    }

    private String generateCustomId(String prefix, long count) {
        return String.format("%s%03d", prefix, count + 1);
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestReset(@RequestBody ResetPasswordRequest email) {
        try {
            resetService.sendOtp(email.getEmail().trim());
            return ResponseEntity.ok("OTP was send to your email");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/email/verify-otp")
    public ResponseEntity<?> verifyResetOtp(@RequestBody ResetPasswordRequest email) {
        boolean valid = resetService.verifyOtp(email.getEmail().trim(), email.getOtp());
        return valid ? ResponseEntity.ok("Otp good") : ResponseEntity.badRequest().body("OTP is available or expired");
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            resetService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword(), request.getConfirmPassword());
            return ResponseEntity.ok("Password was changed");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String username = userDetails.getUsername();
            String ip = request.getRemoteAddr();
            logService.log(username, "Logout", ip);
        }
        return ResponseEntity.ok("Logout successful");
    }
}

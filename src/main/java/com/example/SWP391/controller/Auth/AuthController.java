package com.example.SWP391.controller.Auth;

import com.example.SWP391.DTO.AuthRegister.AuthRegisterDTO;
import com.example.SWP391.DTO.AuthRequest.AuthRequestDTO;
import com.example.SWP391.DTO.AuthRequest.EmailRequestDTO;
import com.example.SWP391.DTO.AuthRequest.OtpRequestDTO;
import com.example.SWP391.DTO.AuthRequest.ResetPasswordRequestDTO;
import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.User.Admin;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.entity.Otp.OtpVerification;
import com.example.SWP391.repository.UserRepository.*;
import com.example.SWP391.repository.OtpRepository.OtpVerificationRepository;
import com.example.SWP391.security.JwtTokenProvider;
import com.example.SWP391.service.Email.EmailService;
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
    @Autowired private AdminRepository adminRepository;
    @Autowired private StaffRepository staffRepository;
    @Autowired private ManagerRepository managerRepository;
    @Autowired private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request, HttpServletRequest httpRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtTokenProvider.generateToken(request.getUsername());

            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            String role = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(r -> r.replace("ROLE_", ""))
                    .findFirst()
                    .orElse("UNKNOWN");

            String cleanRole = role.toUpperCase();

            String ip = httpRequest.getRemoteAddr();
            logService.log(request.getUsername(), "Login", ip);

            Account account = accountRepo.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy account"));

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("refreshToken", token);
            response.put("id", account.getAccountID());
            response.put("username", account.getUsername());
            response.put("fullName", account.getFullname());
            response.put("email", account.getEmail());
            response.put("phone", account.getPhone());
            response.put("role", cleanRole);
            response.put("isEmailVerified", account.isEnabled());

            // ✅ Add specific ID based on role
            switch (cleanRole) {
                case "CUSTOMER" -> {
                    customerRepository.findByAccount(account)
                            .ifPresent(customer -> response.put("customerID", customer.getCustomerID()));
                }
                case "STAFF" -> {
                    staffRepository.findByAccount(account)
                            .ifPresent(staff -> response.put("staffID", staff.getStaffID()));
                }
                case "MANAGER" -> {
                    managerRepository.findByAccount(account)
                            .ifPresent(manager -> response.put("managerID", manager.getManagerID()));
                }
                case "ADMIN" -> {
                    adminRepository.findByAccount(account)
                            .ifPresent(admin -> response.put("adminID", admin.getAdminID()));
                }
            }

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRegisterDTO dto, HttpServletRequest request) {
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
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequestDTO request) {
        try {
            Optional<OtpVerification> otpOpt = otpRepo.findTopByEmailOrderByIdDesc(request.getEmail());

            if (otpOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy OTP cho email.");
            }

            OtpVerification otp = otpOpt.get();



            if (!otp.getOtp().equals(request.getOtp())) {
                return ResponseEntity.badRequest().body("Mã OTP không hợp lệ.");
            }

            if (otp.getExpirationTime() == null || otp.getExpirationTime().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("Mã OTP đã hết hạn.");
            }


            Account account = accountRepo.findByEmail(request.getEmail());
            account.setEnabled(true);
            accountRepo.save(account);


            otpRepo.delete(otp);

            return ResponseEntity.ok("Tài khoản đã được kích hoạt thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server");
        }
    }
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody EmailRequestDTO dto){
        try {
            String email = dto.getEmail();

            Account account = accountRepo.findByEmail(email);
            if (account == null) {
                return ResponseEntity.badRequest().body("Email không tồn tại.");
            }

            if (account.isEnabled()) {
                return ResponseEntity.badRequest().body("Tài khoản đã được kích hoạt.");
            }


            Optional<OtpVerification> existingOtpOpt = otpRepo.findTopByEmailOrderByIdDesc(email);


            if (existingOtpOpt.isPresent()) {
                OtpVerification existingOtp = existingOtpOpt.get();
                if (existingOtp.getExpirationTime() == null || existingOtp.getExpirationTime().isBefore(LocalDateTime.now())) {
                    otpRepo.delete(existingOtp);
                }
            }


            String otpCode = String.format("%06d", new Random().nextInt(999999));

            OtpVerification newOtp = new OtpVerification();
            newOtp.setEmail(email);
            newOtp.setOtp(otpCode);
            newOtp.setExpirationTime(LocalDateTime.now().plusMinutes(5));

            otpRepo.save(newOtp);

            emailService.sendOtpEmail(email, otpCode);

            return ResponseEntity.ok("OTP mới đã được gửi đến email.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi gửi lại OTP");
        }
    }


    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> body) {
        String idToken=body.get("credential");

        GoogleIdTokenVerifier verifier=new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),new JacksonFactory())
                .setAudience(Collections.singletonList("26142191146-7u8f63rgtupdv8v6kv8ug307j55hjfob.apps.googleusercontent.com"))
                .build();

        try{
            GoogleIdToken googleIdToken=verifier.verify(idToken);
            if(googleIdToken==null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Google is not available");
            }
            GoogleIdToken.Payload payload=googleIdToken.getPayload();
            String email= payload.getEmail();
            String name=(String) payload.get("name");
            String picture=(String) payload.get("picture");

            //Find the email
            Account account=accountRepo.findByEmail(email);

            if(account==null){
                account=new Account();
                account.setUsername(email);
                account.setPassword("");
                account.setEmail(email);
                account.setPhone("");
                account.setRole("Customer");
                account.setCreateAt(LocalDate.now());
                account.setEnabled(true);
                account.setFullname(name);


                Customer customer=new Customer();
                customer.setCustomerID(generateCustomId("CUST",customerRepository.count()));
                customer.setFullName(name);
                customer.setEmail(email);
                customer.setPhone("");
                customer.setAddress("");
                customer.setGender(null);
                customer.setAccount(account);

                account.setCustomer(customer);

                accountRepo.save(account);
                customerRepository.save(customer);
            }else{
                if(account.getFullname()==null || account.getFullname().isEmpty()){
                    account.setFullname(name);
                }

                if(account.getCustomer()==null){
                    Customer customer=new Customer();
                    customer.setCustomerID(generateCustomId("CUST",customerRepository.count()));
                    customer.setFullName(name);
                    customer.setEmail(email);
                    customer.setPhone(account.getPhone()!=null? account.getPhone() : "");
                    customer.setAddress("");
                    customer.setGender(null);
                    customer.setDob(null);
                    customer.setAccount(account);

                    account.setCustomer(customer);
                    customerRepository.save(customer);
                }
                accountRepo.save(account);
            }
            Customer customer=account.getCustomer();
            if(customer==null){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Account that not link the customer");
            }
            String token= jwtTokenProvider.generateToken(account.getUsername());
            String role= account.getRole().toUpperCase();

            Map<String,Object>response=new HashMap<>();
            response.put("token",token);
            response.put("refreshToken",token);
            response.put("id",account.getAccountID());
            response.put("customerID",customer.getCustomerID());
            response.put("username",account.getUsername());
            response.put("fullname",account.getFullname());
            response.put("email",account.getEmail());
            response.put("phone",account.getPhone());
            response.put("role",role);
            response.put("avatar",picture);
            response.put("picture",picture);
            response.put("isEmailVerified",true);
            response.put("createAt",account.getCreateAt());
            response.put("loginMethod", "google");

            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error login by Google");
        }
    }


    private String generateCustomId(String prefix, long count) {
        return String.format("%s%03d", prefix, count + 1);
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestReset(@RequestBody ResetPasswordRequestDTO email) {
        try {
            resetService.sendOtp(email.getEmail().trim());
            return ResponseEntity.ok("OTP was send to your email");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/email/verify-otp")
    public ResponseEntity<?> verifyResetOtp(@RequestBody ResetPasswordRequestDTO email) {
        boolean valid = resetService.verifyOtp(email.getEmail().trim(), email.getOtp());
        return valid ? ResponseEntity.ok("Otp good") : ResponseEntity.badRequest().body("OTP is available or expired");
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
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

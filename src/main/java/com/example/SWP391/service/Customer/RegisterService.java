package com.example.SWP391.service.Customer;

import com.example.SWP391.DTO.AuthRegister.AuthRegisterDTO;
import com.example.SWP391.DTO.AuthRegister.AuthRegisterDTO;
import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.entity.Otp.OtpVerification;
import com.example.SWP391.repository.UserRepository.AccountRepository;
import com.example.SWP391.repository.UserRepository.CustomerRepository;
import com.example.SWP391.repository.OtpRepository.OtpVerificationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class RegisterService {
    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpVerificationRepository otpRepo;
    public String register(@Valid AuthRegisterDTO dto) {
        if (accountRepo.existsByUsername(dto.getUsername())) {
            return "Username was exist!";
        }
        if(accountRepo.existsByEmail(dto.getEmail())){
            return "Email was exist";
        }

        if(accountRepo.existsByPhone(dto.getPhone())){
            return "Phone was exist";
        }

        String customerID = generateCustomerID();

        Account account = new Account();
        account.setUsername(dto.getUsername());
        account.setPassword(dto.getPassword()); // nhớ mã hóa!
        account.setEmail(dto.getEmail());
        account.setPhone(dto.getPhone());
        account.setRole("Customer");
        account.setCreateAt(LocalDate.now());
        account.setEnabled(false); // chưa kích hoạt
        account.setFullname(dto.getFullname());

        Customer customer = new Customer();
        customer.setCustomerID(customerID);
        customer.setAccount(account);
        customer.setFullName(dto.getFullname());
        customer.setDob(dto.getDob());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());

        account.setCustomer(customer);
        accountRepo.save(account);

        // === Tạo mã OTP và gửi email ===
        String otp = generateOtp();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(dto.getEmail());
        otpVerification.setOtp(otp);
        otpVerification.setExpirationTime(expirationTime);
        otpRepo.save(otpVerification);

        // Gửi email
        sendOtpEmail(dto.getEmail(), otp);

        return "Registration successful. An OTP has been sent to your email. Please verify it to activate your account.";
    }
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // mã 6 chữ số
        return String.valueOf(otp);
    }
    private void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("genetix.noreply@gmail.com");
        message.setTo(to);
        message.setSubject("Confirm to register account");
        message.setText("Your OTP code is : " + otp + "\nExpirated in 5 minutes.");
        mailSender.send(message);
    }

    private String generateCustomerID() {
        int suffix = 1;
        String id;
        do {
            id = String.format("CUST%03d", suffix++);
        } while (customerRepo.existsById(id));
        return id;
    }
}

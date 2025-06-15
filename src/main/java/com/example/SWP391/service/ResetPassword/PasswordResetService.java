package com.example.SWP391.service.Customer;


import com.example.SWP391.DTO.AuthRequest.ResetPasswordRequest;
import com.example.SWP391.entity.Account;
import com.example.SWP391.entity.PasswordResetToken;
import com.example.SWP391.repository.AccountRepository;
import com.example.SWP391.repository.PasswordResetTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {
    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private AccountRepository accRepo;

    @Autowired

    private JavaMailSender mailSender;
    @Transactional
    public void sendOtp(String email){
        Account accountOtp=accRepo.findByEmail(email.trim());
        if(accountOtp==null){
            throw new RuntimeException("This "+email+" was not found");
        }
        tokenRepo.deleteByEmail(email);
        String otp=String.format("%06d",new Random().nextInt(999999));
        PasswordResetToken token=new PasswordResetToken();
        token.setEmail(email);
        token.setToken(otp);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        tokenRepo.save(token);
        sendOtpEmail(email,otp);
    }

    private void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("genetix.noreply@gmail.com"); // 👈 BẮT BUỘC PHẢI CÓ
        message.setTo(to);
        message.setSubject("Reset password");
        message.setText("Your OTP code is : " + otp + "\nExpirated in 5 minutes.");
        mailSender.send(message);
    }

    public boolean verifyOtp(String email,String otp){
        Optional<PasswordResetToken> tokenOtp=tokenRepo.findByEmailAndToken(email, otp);
        return tokenOtp.isPresent() && tokenOtp.get().getExpiryDate().isAfter(LocalDateTime.now());
    }

    @Transactional
    public void resetPassword(String email,String otp,String newPassword,String comfirmPassword){
        if(!newPassword.equals(comfirmPassword)){
            throw new RuntimeException("Not the same password");
        }

        if(!verifyOtp(email, otp)){
            throw new RuntimeException("Otp is available or expired");
        }

        Account account=accRepo.findByEmail(email);
        if(account==null){
            throw new RuntimeException("Email not found");
        }
        account.setPassword(newPassword);
        tokenRepo.deleteByEmail(email);
    }
}

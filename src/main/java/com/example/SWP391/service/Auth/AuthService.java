package com.example.SWP391.service.Auth;

import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.repository.UserRepository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private AccountRepository accountRepository;



    @Autowired
    private JavaMailSender mailSender;


    public Optional<Account> login(String username, String password) {
        return accountRepository.findByUsernameAndPassword(username, password);
    }


}

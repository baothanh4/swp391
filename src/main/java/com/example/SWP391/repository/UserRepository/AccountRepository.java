package com.example.SWP391.repository.UserRepository;

import com.example.SWP391.entity.Otp.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsernameAndPassword(String username, String password);
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Account findByEmail(String email);
    Optional<Account> findTopByEmailOrderByCreateAtDesc(String email);
    Optional<Account> findByPhone(String phone);

}

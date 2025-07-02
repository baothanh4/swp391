package com.example.SWP391.repository.UserRepository;

import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.User.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,String> {
    Optional<Admin> findTopByOrderByAdminIDDesc();
    @Query(value = "SELECT MAX(CAST(SUBSTRING(admin_id, 4) AS UNSIGNED)) FROM admin", nativeQuery = true)
    Integer findMaxAdminId();
    Optional<Admin> findByAccount(Account account);
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByPhone(String phone);
}

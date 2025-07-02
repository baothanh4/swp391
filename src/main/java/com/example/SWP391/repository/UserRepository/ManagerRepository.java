package com.example.SWP391.repository.UserRepository;

import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.User.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager,String> {
    Optional<Manager> findTopByOrderByManagerIDDesc();
    @Query(value = "SELECT MAX(CAST(SUBSTRING(manager_id, 4) AS UNSIGNED)) FROM manager", nativeQuery = true)
    Integer findMaxManagerId();
    Optional<Manager> findByAccount(Account account);
    Optional<Manager> findByEmail(String email);
    Optional<Manager> findByPhone(String phone);
}
